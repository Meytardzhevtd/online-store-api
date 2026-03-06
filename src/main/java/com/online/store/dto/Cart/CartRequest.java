package com.online.store.dto.Cart;

import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CartRequest {
	private Long userId;
	private Long productId;
	private Integer quantity;
}
