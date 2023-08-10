package com.midtern.SpringCommerce.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "cart_product")
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class CartProduct extends BaseEntity {
    @ManyToOne(targetEntity = Cart.class)
    @JoinColumn(name = "cart_id")
    private Cart cart;

    @ManyToOne(targetEntity = Product.class)
    @JoinColumn(name = "product_id")
    private Product product;

    private int quantity;
}
