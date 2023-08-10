package com.midtern.SpringCommerce.entity;

import jakarta.persistence.*;
import lombok.*;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.util.Set;

@Entity
@Table(name = "product")
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Data
@EntityListeners(AuditingEntityListener.class)
@EqualsAndHashCode(exclude = {"carts"}, callSuper = true)
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

//    @ManyToMany(
//            mappedBy = "products",
//            targetEntity = Cart.class
//    )
//    private Set<Cart> carts;

        @OneToMany(mappedBy = "product", targetEntity = CartProduct.class)
        private Set<CartProduct> cartProducts;
}
