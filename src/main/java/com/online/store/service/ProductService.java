package com.online.store.service;

import java.util.Optional;

import javax.management.RuntimeErrorException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.online.store.dto.ProdactDTO;
import com.online.store.entity.Product;
import com.online.store.repository.ProductRepository;

@Service
public class ProductService {
    public final ProductRepository productRepository;

    @Autowired
    public ProductService(ProductRepository productRepository) {
        this.productRepository = productRepository;
    }

    public Product create(ProdactDTO productDto) {
        Product saved = productRepository.save(productDto.toProduct());
        return saved;
    }

    public void buy(Long productId) {
        Optional<Product> optionalProduct = productRepository.findById(productId);
        if (optionalProduct.isPresent()) {
            Product product = optionalProduct.get();
            Integer quantity = product.getQuantity();
            if (quantity.equals(0)) {
                throw new RuntimeException("This product is over");
            }
            product.setQuantity(quantity - 1);
            productRepository.save(product);
        }
    }

    public void delete(Long productId) {
        Optional<Product> optionalProduct = productRepository.findById(productId);
        if (optionalProduct.isPresent()) {
            Product product = optionalProduct.get();
            productRepository.delete(product);
        } else {
            throw new RuntimeException("Product not found");
        }
    }
}
