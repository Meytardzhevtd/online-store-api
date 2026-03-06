package com.online.store.service;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.online.store.dto.Cart.CartResponse;
import com.online.store.repository.CartItemRepository;
import com.online.store.repository.UserRepository;
import com.online.store.exceptions.UserNotFoundException;
import com.online.store.mapper.CartMapper;
import com.online.store.entity.CartItem;
import com.online.store.entity.User;

@Service
@Transactional(readOnly = true)
public class CartItemService {
	private final CartItemRepository cartItemRepository;
	private final UserRepository userRepository;
	private final CartMapper cartMapper;

	@Autowired
	public CartItemService(CartItemRepository cartItemRepository, UserRepository userRepository,
			CartMapper cartMapper) {
		this.cartItemRepository = cartItemRepository;
		this.userRepository = userRepository;
		this.cartMapper = cartMapper;
	}

	public List<CartResponse> getCart(Long userId) {
		User user = userRepository.findById(userId)
				.orElseThrow(() -> new UserNotFoundException(userId));

		return cartItemRepository.findAllByUser(user).stream()
				.map((entity) -> cartMapper.toCartResponse(entity)).collect(Collectors.toList());

	}
}
