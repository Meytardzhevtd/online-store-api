package com.online.store.dto.product;

import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ProductCount {
	private Long id; // productId
	private Integer count;
}
