package com.online.store.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.online.store.dto.product.ProductRequest;
import com.online.store.dto.product.ProductResponse;
import com.online.store.entity.Product;
import com.online.store.exceptions.ProductNotFoundException;
import com.online.store.mapper.ProductMapper;
import com.online.store.repository.ProductRepository;

@Service
@Transactional(readOnly = true)
public class ProductService {
	public final ProductRepository productRepository;
	private final ProductMapper productMapper;

	@Autowired
	public ProductService(ProductRepository productRepository, ProductMapper productMapper) {
		this.productRepository = productRepository;
		this.productMapper = productMapper;
	}

	@Transactional
	public ProductResponse create(ProductRequest request) {
		Product saved = productRepository.save(productMapper.toEntity(request));
		return productMapper.toResponse(saved);
	}

	@Transactional
	public ProductResponse update(ProductResponse request) {
		Product product = productRepository.findById(request.getId())
				.orElseThrow(() -> new ProductNotFoundException(request.getId()));

		product = productMapper.toEntityFromProductResponse(request);
		Product saved = productRepository.save(product);
		return productMapper.toResponse(saved);
	}

	public ProductResponse get(Long productId) {
		Product product = productRepository.findById(productId)
				.orElseThrow(() -> new ProductNotFoundException(productId));
		return productMapper.toResponse(product);
	}

	@Transactional
	public void delete(Long productId) {
		Product product = productRepository.findById(productId)
				.orElseThrow(() -> new ProductNotFoundException(productId));
		productRepository.delete(product);
	}

}
