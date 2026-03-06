package com.online.store.dto.product;

import java.math.BigDecimal;

import lombok.Data;

@Data
public class ProductCreate {
    private String title;
    private String description;
    private BigDecimal price;
    private Integer quantity;
}
