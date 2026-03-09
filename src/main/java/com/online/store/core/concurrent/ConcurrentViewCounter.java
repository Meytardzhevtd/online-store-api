package com.online.store.core.concurrent;

import java.util.Iterator;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.LongAdder;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import com.online.store.entity.Product;
import com.online.store.exceptions.ProductNotFoundException;
import com.online.store.repository.ProductRepository;

class Node {
	private final LongAdder count;
	private volatile long lastAccess;

	public Node() {
		this.count = new LongAdder();
		this.lastAccess = System.currentTimeMillis();
	}

	public void upd() {
		count.increment();
		this.lastAccess = System.currentTimeMillis();
	}

	public long getCount() {
		return count.sum();
	}

	public long sumThenReset() {
		return count.sumThenReset();
	}

	public long getLastAccess() {
		return lastAccess;
	}
}

@Component
public class ConcurrentViewCounter {
	private final ConcurrentHashMap<Long, Node> map = new ConcurrentHashMap<>();
	private final LongAdder globalCounter = new LongAdder();

	private static final long MAX_TIME_LAST_ACCESS_MS = 60_000L; // 1 минута
	private static final long MAX_COUNT_THRESHOLD = 1_000L; // 1000 просмотров

	// Лимит операций до принудительного прохода
	private static final long MAX_OPERATIONS_BEFORE_FLUSH = 100_000L;

	private final ProductRepository productRepository;

	@Autowired
	public ConcurrentViewCounter(ProductRepository productRepository) {
		this.productRepository = productRepository;
	}

	public void increment(Long productId) {
		Node node = map.computeIfAbsent(productId, k -> new Node());
		node.upd();

		globalCounter.increment();

		if (globalCounter.sum() >= MAX_OPERATIONS_BEFORE_FLUSH) {
			flush();
			globalCounter.reset();
		}
	}

	@Transactional
	private void flush() {
		long now = System.currentTimeMillis();
		Iterator<Map.Entry<Long, Node>> iterator = map.entrySet().iterator();

		while (iterator.hasNext()) {
			Map.Entry<Long, Node> entry = iterator.next();
			Node node = entry.getValue();

			long timeDiff = now - node.getLastAccess();
			long currentCount = node.getCount();

			boolean isCold = timeDiff > MAX_TIME_LAST_ACCESS_MS;
			boolean isFull = currentCount >= MAX_COUNT_THRESHOLD;

			if (isCold || isFull) {
				long toSave = node.sumThenReset();

				if (toSave > 0) {
					try {
						productRepository.incrementViewsById(entry.getKey(), toSave);
						System.out.println("Flushed views for product " + entry.getKey() + ": "
								+ toSave + (isCold ? " (cold)" : " (full)"));
					} catch (Exception e) {
						System.err.println("Error saving views: " + e.getMessage());
					}
				}

				if (isCold) {
					iterator.remove();
				}
			}
		}
	}

	public Long getTotalViews(Long productId) {
		Node node = map.get(productId);
		Product product = productRepository.findById(productId)
				.orElseThrow(() -> new ProductNotFoundException(productId));

		long dbCount = product.getViewsCount();

		if (node == null) {
			return dbCount;
		} else {
			return dbCount + node.getCount();
		}
	}

	public void shutdown() {
		flush();
	}
}