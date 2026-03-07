package com.online.store.exceptions;

public class CartItemNotFoundEexception extends RuntimeException {
	public CartItemNotFoundEexception(Long id) {
		super("Cart item with id = " + id + " not found");
	}

	public CartItemNotFoundEexception(Long userId, Long productId) {
		super("Cart item with userId = " + userId + ", productId = " + productId + " not found");
	}
}
