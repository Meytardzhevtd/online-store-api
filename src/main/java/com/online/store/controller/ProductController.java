package com.online.store.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.online.store.dto.product.ProductRequest;
import com.online.store.dto.product.ProductResponse;
import com.online.store.service.ProductService;

@RestController
@RequestMapping("/api/products")
public class ProductController {
	private final ProductService productService;

	@Autowired
	public ProductController(ProductService productService) {
		this.productService = productService;
	}

	@PostMapping
	public ResponseEntity<ProductResponse> create(@RequestBody ProductRequest request) {
		ProductResponse saved = productService.create(request);
		return ResponseEntity.status(HttpStatus.CREATED).body(saved);
	}

	@GetMapping
	public ResponseEntity<List<ProductResponse>> getAllProducts() {
		return ResponseEntity.status(HttpStatus.OK).body(productService.getAllProducts());
	}

	@GetMapping("/{id}")
	public ResponseEntity<ProductResponse> get(@PathVariable Long id) {
		return ResponseEntity.ok(productService.get(id));
	}

	@PutMapping
	public ResponseEntity<ProductResponse> update(@RequestBody ProductResponse requsest) {
		// ProductResponse -- используется, потому что удобно. Не образать внимание на
		// название
		ProductResponse response = productService.update(requsest);
		return ResponseEntity.status(HttpStatus.OK).body(response);
	}

	@DeleteMapping("/{id}")
	public void delete(@PathVariable Long id) {
		productService.delete(id);
	}

}
