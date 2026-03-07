package com.online.store.exceptions;

public class CartItemNotFoundEexception extends RuntimeException {
    public CartItemNotFoundEexception(Long id) {
        super("Cart item with id = " + id + " not found");
    }
}
