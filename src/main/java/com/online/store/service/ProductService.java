package com.online.store.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.online.store.dto.product.ProductCreate;
import com.online.store.dto.product.ProductResponse;
import com.online.store.entity.Product;
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

	ProductResponse create(ProductCreate request) {
		Product saved = productRepository.save(productMapper.toEntity(request));
		return productMapper.toResponse(saved);
	}

}
