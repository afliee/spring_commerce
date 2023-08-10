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

    public static CategoryResponse toResponse(Category category, String[] attrs) {
        var attributes = List.of(attrs);
        var fields = CategoryResponse.class.getDeclaredFields();

        var categoryResponse = new CategoryResponse();
        for (var field : fields) {
            if (attributes.contains(field.getName())) {
                try {
                    var method = category.getClass().getMethod("get" + field.getName().substring(0, 1).toUpperCase() + field.getName().substring(1));
                    var value = method.invoke(category);
                    field.setAccessible(true);
                    field.set(categoryResponse, value);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }

        return categoryResponse;
    }

    public static List<CategoryResponse> toResponse(List<Category> categories, String[] attrs) {
        return categories.stream().map(category -> toResponse(category, attrs)).toList();
    }
    public static Category toEntity(CategoryRequest request) {
        return Category.builder()
                .name(request.getName())
                .description(request.getDescription())
                .build();
    }
}
