package com.midtern.SpringCommerce.dto.response;

import com.fasterxml.jackson.annotation.JsonInclude;
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
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ProductResponse {
    private String id;
    private String name;
    private String description;
    private String image;
    private double price;
    private int quantity;

    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private LocalDate createdAt;

    private int amount;
}
