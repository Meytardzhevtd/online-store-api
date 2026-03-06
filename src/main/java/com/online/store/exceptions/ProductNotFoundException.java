package com.online.store.exceptions;

public class ProductNotFoundException extends RuntimeException {
	public ProductNotFoundException(Long id) {
		super("Product with id = " + id + " not foound");
	}
}
