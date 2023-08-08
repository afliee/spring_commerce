package com.midtern.SpringCommerce.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDate;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ProductResponse {
    private String id;
    private String name;
    private String description;
    private String image;
    private double price;
    private int quantity;

    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private LocalDate createdAt;
}
