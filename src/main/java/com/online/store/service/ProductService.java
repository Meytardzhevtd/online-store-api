package com.online.store.service;

import java.util.Optional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.online.store.dto.ProdactDTO;
import com.online.store.entity.Product;
import com.online.store.repository.ProductRepository;

@Service
@Transactional(readOnly = true)
public class ProductService {
	public final ProductRepository productRepository;

	@Autowired
	public ProductService(ProductRepository productRepository) {
		this.productRepository = productRepository;
	}

	@Transactional
	public Product create(ProdactDTO productDto) {
		Product saved = productRepository.save(productDto.toProduct());
		return saved;
	}

	public Optional<Product> get(Long productId) {
		return productRepository.findById(productId);
	}

	@Transactional
	public Product update(Long productId, ProdactDTO productDto) {
		Optional<Product> optionalProduct = productRepository.findById(productId);
		if (optionalProduct.isPresent()) {
			Product product = optionalProduct.get();
			product.setTitle(productDto.getTitle());
			product.setPrice(productDto.getPrice());
			product.setDescriprion(productDto.getDescription());
			product.setQuantity(productDto.getQuantity());
			Product saved = productRepository.save(product);
			return saved;
		} else {
			throw new RuntimeException("This product not found");
		}
	}

	@Transactional
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
		} else {
			throw new RuntimeException("This product not found");
		}
	}

	@Transactional
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
