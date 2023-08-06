package com.midtern.SpringCommerce.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Set;

@Entity
@Table(name = "product")
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Data
public class Product extends BaseEntity {
    private String name;
    private String description;
    private String image;
    private double price;
    private int quantity;

    @ManyToOne(
            targetEntity = Category.class
    )
    @JoinColumn(name = "category_id")
    private Category category;

    @ManyToMany(
            mappedBy = "products",
            targetEntity = Cart.class
    )
    private Set<Cart> carts;
}
