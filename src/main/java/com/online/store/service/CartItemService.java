package com.online.store.service;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.online.store.dto.Cart.CartRequest;
import com.online.store.dto.Cart.CartResponse;
import com.online.store.repository.CartItemRepository;
import com.online.store.repository.ProductRepository;
import com.online.store.repository.UserRepository;
import com.online.store.exceptions.CartItemNotFoundEexception;
import com.online.store.exceptions.ProductNotFoundException;
import com.online.store.exceptions.UserNotFoundException;
import com.online.store.mapper.CartMapper;
import com.online.store.entity.CartItem;
import com.online.store.entity.Product;
import com.online.store.entity.User;

@Service
@Transactional(readOnly = true)
public class CartItemService {
	private final CartItemRepository cartItemRepository;
	private final UserRepository userRepository;
	private final CartMapper cartMapper;
	private final ProductRepository productRepository;

	@Autowired
	public CartItemService(CartItemRepository cartItemRepository, UserRepository userRepository,
			CartMapper cartMapper, ProductRepository productRepository) {
		this.cartItemRepository = cartItemRepository;
		this.userRepository = userRepository;
		this.cartMapper = cartMapper;
		this.productRepository = productRepository;
	}

	public List<CartResponse> getCart(Long userId) {
		User user = userRepository.findById(userId)
				.orElseThrow(() -> new UserNotFoundException(userId));

		return cartItemRepository.findAllByUser(user).stream()
				.map((entity) -> cartMapper.toCartResponse(entity)).collect(Collectors.toList());

	}

	@Transactional
	public void update(CartRequest request) {
		User user = userRepository.findById(request.getUserId())
				.orElseThrow(() -> new UserNotFoundException(request.getUserId()));
		Product product = productRepository.findById(request.getProductId())
				.orElseThrow(() -> new ProductNotFoundException(request.getProductId()));

		if (cartItemRepository.existsByUserAndProduct(user, product)) {
			CartItem cartItem = cartItemRepository.findByUserAndProduct(user, product).orElseThrow(
					() -> new CartItemNotFoundEexception(user.getId(), product.getId()));

			cartItem.setQuantity(request.getQuantity());
		} else {
			cartItemRepository.save(new CartItem(null, user, product, request.getQuantity()));
		}

	}

}
