package com.online.store.service;

import com.online.store.dto.product.ProductRequest;
import com.online.store.dto.product.ProductResponse;
import com.online.store.entity.Product;
import com.online.store.exceptions.ProductNotFoundException;
import com.online.store.mapper.ProductMapper;
import com.online.store.repository.ProductRepository;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import java.math.BigDecimal;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ProductServiceTest {

	@Mock
	private ProductRepository productRepository;

	@Mock
	private ProductMapper productMapper;

	@InjectMocks
	private ProductService productService;

	private ProductRequest request;
	private Product productEntity;
	private ProductResponse response;

	@BeforeEach
	void setUp() {
		request = new ProductRequest();
		request.setTitle("Test Laptop");
		request.setDescription("Powerful gaming laptop");
		request.setPrice(BigDecimal.valueOf(1000.0));
		request.setQuantity(5);

		productEntity = new Product();
		productEntity.setId(1L);
		productEntity.setTitle("Test Laptop");
		productEntity.setDescriprion("Powerful gaming laptop");
		productEntity.setPrice(BigDecimal.valueOf(1000.0));
		productEntity.setQuantity(5);

		response = new ProductResponse();
		response.setId(1L);
		response.setTitle("Test Laptop");
		response.setDescription("Powerful gaming laptop");
		response.setPrice(BigDecimal.valueOf(1000.0));
		response.setQuantity(5);
	}

	@Test
	@DisplayName("Should create product successfully when request is valid")
	void testCreateProduct_Success() {
		when(productMapper.toEntity(request)).thenReturn(productEntity);
		when(productRepository.save(any(Product.class))).thenReturn(productEntity);
		when(productMapper.toResponse(productEntity)).thenReturn(response);
		ProductResponse result = productService.create(request);

		assertNotNull(result, "Результат не должен быть null");
		assertEquals(1L, result.getId(), "ID должен совпадать");
		assertEquals("Test Laptop", result.getTitle(), "Название должно совпадать");
		assertEquals(BigDecimal.valueOf(1000.0), result.getPrice(), "Цена должна совпадать");

		verify(productMapper, times(1)).toEntity(request);
		verify(productRepository, times(1)).save(any(Product.class));
		verify(productMapper, times(1)).toResponse(productEntity);
	}

	@Test
	@DisplayName("Should return product when found by ID")
	void testGetProduct_Found() {
		Long productId = 1L;

		when(productRepository.findById(productId)).thenReturn(Optional.of(productEntity));
		when(productMapper.toResponse(productEntity)).thenReturn(response);

		ProductResponse result = productService.get(productId);

		assertNotNull(result);
		assertEquals(productId, result.getId());
		assertEquals("Test Laptop", result.getTitle());

		verify(productRepository).findById(productId);
		verify(productMapper).toResponse(productEntity);
	}

	@Test
	@DisplayName("Should throw ProductNotFoundException when product not found")
	void testGetProduct_NotFound() {
		Long productId = 999L;

		when(productRepository.findById(productId)).thenReturn(Optional.empty());
		assertThrows(ProductNotFoundException.class, () -> productService.get(productId),
				"Ожидалось выбрасывание ProductNotFoundException");

		verify(productRepository, never()).save(any());
		verify(productMapper, never()).toResponse(any());
	}

	@Test
	@DisplayName("Should update product successfully")
	void testUpdateProduct_Success() {

		ProductResponse updateRequest = new ProductResponse();
		updateRequest.setId(1L);
		updateRequest.setTitle("Updated Title");
		updateRequest.setDescription("Updated description");
		updateRequest.setPrice(BigDecimal.valueOf(1200.0));
		updateRequest.setQuantity(10);

		Product updatedEntity = new Product();
		updatedEntity.setId(1L);
		updatedEntity.setTitle("Updated Title");
		updatedEntity.setDescriprion("Updated description");
		updatedEntity.setPrice(BigDecimal.valueOf(1200.0));
		updatedEntity.setQuantity(10);

		when(productRepository.findById(1L)).thenReturn(Optional.of(productEntity));
		when(productMapper.toEntityFromProductResponse(updateRequest)).thenReturn(updatedEntity);
		when(productRepository.save(updatedEntity)).thenReturn(updatedEntity);

		ProductResponse expectedResponse = new ProductResponse();
		expectedResponse.setId(1L);
		expectedResponse.setTitle("Updated Title");
		expectedResponse.setPrice(BigDecimal.valueOf(1200.0));
		when(productMapper.toResponse(updatedEntity)).thenReturn(expectedResponse);

		ProductResponse result = productService.update(updateRequest);

		assertNotNull(result);
		assertEquals("Updated Title", result.getTitle());
		assertEquals(BigDecimal.valueOf(1200.0), result.getPrice());

		verify(productRepository).findById(1L);
		verify(productMapper).toEntityFromProductResponse(updateRequest);
		verify(productRepository).save(updatedEntity);
	}

	@Test
	@DisplayName("Should delete product successfully")
	void testDeleteProduct_Success() {
		Long productId = 1L;

		when(productRepository.findById(productId)).thenReturn(Optional.of(productEntity));
		doNothing().when(productRepository).delete(productEntity);

		productService.delete(productId);

		verify(productRepository).findById(productId);
		verify(productRepository).delete(productEntity);
		verify(productRepository, times(1)).delete(productEntity);
	}
}