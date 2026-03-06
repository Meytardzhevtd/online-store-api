package com.online.store.exceptions;

public class UserNotFoundException extends Exception {
    UserNotFoundException(Long id) {
        super("User with id = " + id + " not found");
    }
}
