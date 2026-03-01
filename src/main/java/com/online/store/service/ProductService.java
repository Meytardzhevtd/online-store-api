package com.online.store.service;

import org.springframework.beans.factory.annotation.Autowired;

import com.online.store.entity.Product;
import com.online.store.repository.ProductRepository;

public class ProductService {
    public final ProductRepository productRepository;

    @Autowired
    public ProductService(ProductRepository productRepository) {
        this.productRepository = productRepository;
    }

    Product createProduct() {
        return new Product();
    }
}
