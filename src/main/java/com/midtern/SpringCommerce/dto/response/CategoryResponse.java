package com.midtern.SpringCommerce.dto.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.midtern.SpringCommerce.entity.Product;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.Set;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class CategoryResponse {
    private String id;
    private String name;
    private String description;
    private LocalDate createdAt;
    private Set<ProductResponse> products;
}
