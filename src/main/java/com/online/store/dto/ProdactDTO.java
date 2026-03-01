package com.online.store.dto;

import com.online.store.entity.Product;

import lombok.*;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ProdactDTO {
    private String title;
    private String description;
    private double price;
    private Integer quantity;

    public Product toProduct() {
        return new Product(null, title, description, price, quantity);
    }
}
