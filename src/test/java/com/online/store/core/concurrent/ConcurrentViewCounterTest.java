package com.online.store.core.concurrent;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.verify;

import java.lang.reflect.Field;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicLong;

import com.online.store.entity.Product;
import com.online.store.repository.ProductRepository;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

class ConcurrentViewCounterTest {

	@Mock
	private ProductRepository productRepository;

	private ConcurrentViewCounter counter;

	@BeforeEach
	void setUp() {
		MockitoAnnotations.openMocks(this);
		counter = new ConcurrentViewCounter(productRepository);
	}

	@Test
	@DisplayName("Should aggregate in-memory views and flush them to repository on shutdown when threshold reached")
	void testIncrementAndFlushFull() {
		Long productId = 1L;
		AtomicLong dbCounter = new AtomicLong(100L);

		doAnswer(invocation -> {
			Long id = invocation.getArgument(0);
			Product p = new Product();
			p.setId(id);
			p.setViewsCount(dbCounter.get());
			return Optional.of(p);
		}).when(productRepository).findById(productId);

		doAnswer(invocation -> {
			Long id = invocation.getArgument(0);
			Long count = invocation.getArgument(1);
			dbCounter.addAndGet(count);
			return null;
		}).when(productRepository).incrementViewsById(anyLong(), anyLong());

		int increments = 1001;
		for (int i = 0; i < increments; i++) {
			counter.increment(productId);
		}

		Long before = counter.getTotalViews(productId);
		assertEquals(100L + increments, before);

		counter.shutdown();

		verify(productRepository).incrementViewsById(productId, (long) increments);

		Long after = counter.getTotalViews(productId);
		assertEquals(100L + increments, after);
	}

	@Test
	@DisplayName("getTotalViews returns DB value when no in-memory node exists")
	void testGetTotalViews_NoNode() {
		Long productId = 5L;
		AtomicLong dbCounter = new AtomicLong(50L);

		try {
			doAnswer(invocation -> {
				Long id = invocation.getArgument(0);
				Product p = new Product();
				p.setId(id);
				p.setViewsCount(dbCounter.get());
				return Optional.of(p);
			}).when(productRepository).findById(productId);

			Long total = counter.getTotalViews(productId);
			assertEquals(50L, total);
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	@Test
	@DisplayName("Cold flush removes node and persists small counts")
	void testFlushColdRemovesNode() throws Exception {
		Long productId = 2L;
		AtomicLong dbCounter = new AtomicLong(10L);

		doAnswer(invocation -> {
			Long id = invocation.getArgument(0);
			Product p = new Product();
			p.setId(id);
			p.setViewsCount(dbCounter.get());
			return Optional.of(p);
		}).when(productRepository).findById(productId);

		AtomicLong persisted = new AtomicLong(0L);
		doAnswer(invocation -> {
			Long id = invocation.getArgument(0);
			Long count = invocation.getArgument(1);
			persisted.addAndGet(count);
			return null;
		}).when(productRepository).incrementViewsById(anyLong(), anyLong());

		// create node with a single increment
		counter.increment(productId);

		// set lastAccess to far past to mark it as cold via reflection
		Field mapField = ConcurrentViewCounter.class.getDeclaredField("map");
		mapField.setAccessible(true);
		@SuppressWarnings("unchecked")
		ConcurrentHashMap<Long, Object> map = (ConcurrentHashMap<Long, Object>) mapField
				.get(counter);
		Object node = map.get(productId);

		Field lastAccessField = node.getClass().getDeclaredField("lastAccess");
		lastAccessField.setAccessible(true);

		Field coldField = ConcurrentViewCounter.class.getDeclaredField("MAX_TIME_LAST_ACCESS_MS");
		coldField.setAccessible(true);
		long coldMs = coldField.getLong(null);

		long past = System.currentTimeMillis() - (coldMs + 2000L);
		lastAccessField.setLong(node, past);

		// perform shutdown which triggers flush
		counter.shutdown();

		// persisted should have recorded the single increment
		assertEquals(1L, persisted.get());

		// internal map should no longer contain the node
		assertEquals(false, map.containsKey(productId));
	}

	@Test
	@DisplayName("Flush continues on repository exception and does not throw")
	void testFlushHandlesRepoException() {
		Long productId = 3L;
		AtomicLong dbCounter = new AtomicLong(0L);

		doAnswer(invocation -> {
			Long id = invocation.getArgument(0);
			Product p = new Product();
			p.setId(id);
			p.setViewsCount(dbCounter.get());
			return Optional.of(p);
		}).when(productRepository).findById(productId);

		// make repository throw when persisting
		doAnswer(invocation -> {
			throw new RuntimeException("DB is down");
		}).when(productRepository).incrementViewsById(anyLong(), anyLong());

		// create some in-memory counts
		counter.increment(productId);
		counter.increment(productId);

		try {
			// force cold node so flush will attempt to persist small counts
			Field mapField = ConcurrentViewCounter.class.getDeclaredField("map");
			mapField.setAccessible(true);
			@SuppressWarnings("unchecked")
			ConcurrentHashMap<Long, Object> map = (ConcurrentHashMap<Long, Object>) mapField
					.get(counter);
			Object node = map.get(productId);

			Field lastAccessField = node.getClass().getDeclaredField("lastAccess");
			lastAccessField.setAccessible(true);

			Field coldField = ConcurrentViewCounter.class
					.getDeclaredField("MAX_TIME_LAST_ACCESS_MS");
			coldField.setAccessible(true);
			long coldMs = coldField.getLong(null);

			long past = System.currentTimeMillis() - (coldMs + 2000L);
			lastAccessField.setLong(node, past);

			// ensure shutdown does not bubble the exception
			try {
				counter.shutdown();
			} catch (Exception e) {
				throw new AssertionError("shutdown should not throw despite repository error", e);
			}

			// repository should have been invoked
			verify(productRepository).incrementViewsById(productId, 2L);
		} catch (NoSuchFieldException | IllegalAccessException e) {
			throw new RuntimeException(e);
		}
	}

	@Test
	@DisplayName("Concurrent increments aggregate correctly")
	void testConcurrentIncrements() throws Exception {
		Long productId = 4L;
		AtomicLong dbCounter = new AtomicLong(0L);
		doAnswer(invocation -> {
			Long id = invocation.getArgument(0);
			Product p = new Product();
			p.setId(id);
			p.setViewsCount(dbCounter.get());
			return Optional.of(p);
		}).when(productRepository).findById(productId);

		AtomicLong persisted = new AtomicLong(0L);
		doAnswer(invocation -> {
			Long id = invocation.getArgument(0);
			Long count = invocation.getArgument(1);
			persisted.addAndGet(count);
			return null;
		}).when(productRepository).incrementViewsById(anyLong(), anyLong());

		int threads = 8;
		int perThread = 200;
		ExecutorService exec = Executors.newFixedThreadPool(threads);
		for (int t = 0; t < threads; t++) {
			exec.submit(() -> {
				for (int i = 0; i < perThread; i++) {
					counter.increment(productId);
				}
			});
		}
		exec.shutdown();
		exec.awaitTermination(5, TimeUnit.SECONDS);

		// total increments in memory
		long totalIncrements = (long) threads * perThread;
		Long before = counter.getTotalViews(productId);
		assertEquals(totalIncrements, before);

		counter.shutdown();
		// after flush persisted should equal total increments
		assertEquals(totalIncrements, persisted.get());
	}
}
