package com.online.store.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.online.store.entity.CartItem;
import com.online.store.entity.Product;
import com.online.store.entity.User;

public interface CartItemRepository extends JpaRepository<CartItem, Long> {
	public List<CartItem> findAllByUser(User user);

	public Optional<CartItem> findByUserAndProduct(User user, Product product);

	public boolean existsByUserAndProduct(User user, Product product);
}
