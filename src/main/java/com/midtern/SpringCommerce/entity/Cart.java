package com.midtern.SpringCommerce.entity;

import jakarta.persistence.*;
import lombok.*;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.util.Set;

@Entity
@Table(name = "cart")
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@EntityListeners(AuditingEntityListener.class)
@EqualsAndHashCode(exclude = "cartProducts", callSuper = false)
public class Cart extends BaseEntity {
    private Double total;

    @OneToOne(mappedBy = "cart", targetEntity = User.class)
    private User user;

//    @ManyToMany(targetEntity = Product.class)
//    @JoinTable(
//            name = "cart_product",
//            joinColumns = @JoinColumn(name = "cart_id"),
//            inverseJoinColumns = @JoinColumn(name = "product_id")
//    )
//    private Set<Product> products;
    @OneToMany(mappedBy = "cart", targetEntity = CartProduct.class)
    private Set<CartProduct> cartProducts;
}
