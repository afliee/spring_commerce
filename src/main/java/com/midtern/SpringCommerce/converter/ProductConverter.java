package com.midtern.SpringCommerce.converter;

import com.midtern.SpringCommerce.dto.request.ProductRequest;
import com.midtern.SpringCommerce.dto.response.ProductResponse;
import com.midtern.SpringCommerce.entity.Product;

import java.util.Set;
import java.util.stream.Collectors;

public class ProductConverter {
    public static Set<ProductResponse> toResponse(Set<Product> products) {
        return products.stream().map(ProductConverter::toResponse).collect(Collectors.toSet());
    }

    public static ProductResponse toResponse(Product product) {
        return ProductResponse.builder()
                .id(product.getId())
                .name(product.getName())
                .description(product.getDescription())
                .image(product.getImage())
                .price(product.getPrice())
                .quantity(product.getQuantity())
                .createdAt(product.getCreatedDate())
                .build();
    }

    public static Product toEntity(ProductRequest request) {
        return Product.builder()
                .name(request.getName())
                .description(request.getDescription())
                .price(request.getPrice())
                .quantity(request.getQuantity())
                .build();
    }
}
