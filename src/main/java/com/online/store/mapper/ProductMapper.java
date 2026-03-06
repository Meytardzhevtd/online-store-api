package com.online.store.mapper;

import org.mapstruct.Mapper;

import com.online.store.dto.product.ProductRequest;
import com.online.store.dto.product.ProductResponse;
import com.online.store.entity.Product;

@Mapper(componentModel = "spring")
public interface ProductMapper {
	Product toEntity(ProductRequest dto);

	Product toEntityFromProductResponse(ProductResponse dto);

	ProductResponse toResponse(Product entity);
}
