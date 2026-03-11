package com.online.store.entity;

import java.math.BigDecimal;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.*;

@Data
// @AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "products")
public class Product {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@Column(name = "title", nullable = false)
	private String title;

	@Column(name = "description")
	private String descriprion;

	@Column(name = "price", nullable = false)
	private BigDecimal price;

	@Column(name = "quantity", nullable = false)
	private Integer quantity;

	@Column(name = "views_count")
	private Long viewsCount = 0L;

	public static Product build(String title, String descriprion, BigDecimal price,
			Integer quantity) {
		Product product = new Product();
		product.setTitle(title);
		product.setDescriprion(descriprion);
		product.setPrice(price);
		product.setQuantity(quantity);
		return product;
	}
}
