package com.online.store.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.online.store.entity.CartItem;
import com.online.store.entity.User;

public interface CartItemRepository extends JpaRepository<CartItemRepository, Long> {
	public List<CartItem> findAllByUser(User user);
}
