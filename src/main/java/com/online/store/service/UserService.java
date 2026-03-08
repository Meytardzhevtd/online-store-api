package com.online.store.service;

import java.math.BigDecimal;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.online.store.dto.user.LoginRequest;
import com.online.store.dto.user.UserRequest;
import com.online.store.dto.user.UserResponse;
import com.online.store.repository.UserRepository;
import com.online.store.entity.User;
import com.online.store.exceptions.UserAlreadyExistsException;
import com.online.store.exceptions.UserNotFoundException;
import com.online.store.mapper.UserMapper;

@Service
@Transactional(readOnly = true)
public class UserService {
	private final UserRepository userRepository;
	private final PasswordEncoder passwordEncoder;
	private final UserMapper userMapper;

	@Autowired
	public UserService(UserRepository userRepository, PasswordEncoder passwordEncoder,
			UserMapper userMapper) {
		this.userRepository = userRepository;
		this.passwordEncoder = passwordEncoder;
		this.userMapper = userMapper;
	}

	@Transactional
	public UserResponse register(UserRequest request) {
		if (userRepository.existsByEmail(request.getEmail())) {
			throw new UserAlreadyExistsException(request.getEmail());
		}
		User user = new User(null, request.getName(), request.getEmail(),
				passwordEncoder.encode(request.getPassword()), BigDecimal.ZERO);

		return userMapper.toResponse(user);
	}

	public boolean login(LoginRequest request) {
		Optional<User> oUser = userRepository.findByEmail(request.getEmail());
		if (oUser.isPresent()) {
			return passwordEncoder.matches(oUser.get().getPassword(), request.getPassword());
		} else {
			return false;
		}
	}

	public UserResponse getUser(Long userId) {
		User user = userRepository.findById(userId)
				.orElseThrow(() -> new UserNotFoundException(userId));

		return userMapper.toResponse(user);
	}
}
