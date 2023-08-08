package com.midtern.SpringCommerce.converter;

import com.midtern.SpringCommerce.dto.request.CategoryRequest;
import com.midtern.SpringCommerce.dto.response.CategoryResponse;
import com.midtern.SpringCommerce.entity.Category;

import java.util.List;

public class CategoryConverter {
    public static CategoryResponse toResponse(Category category) {
        return CategoryResponse.builder()
                .id(category.getId())
                .name(category.getName())
                .description(category.getDescription())
                .products(category.getProducts() == null ? null : ProductConverter.toResponse(category.getProducts()))
                .createdAt(category.getCreatedDate())
                .build();
    }

    public static List<CategoryResponse> toResponse(List<Category> categories) {
        return categories.stream().map(CategoryConverter::toResponse).toList();
    }

    public static Category toEntity(CategoryRequest request) {
        return Category.builder()
                .name(request.getName())
                .description(request.getDescription())
                .build();
    }
}
