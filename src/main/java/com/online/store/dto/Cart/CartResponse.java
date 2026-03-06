package com.online.store.dto.Cart;

import java.util.List;

import com.online.store.dto.product.ProductCount;

import lombok.*;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CartResponse {
	private Long productId;
	private Integer quantity;
}
