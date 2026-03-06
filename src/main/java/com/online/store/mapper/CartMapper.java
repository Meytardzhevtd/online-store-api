package com.online.store.mapper;

import org.mapstruct.Mapper;

import com.online.store.dto.Cart.CartResponse;
import com.online.store.entity.CartItem;

@Mapper(componentModel = "spring")
public interface CartMapper {
	default CartResponse toCartResponse(CartItem entity) {
		return new CartResponse(entity.getUser().getId(), entity.getQuantity());
	}
}
