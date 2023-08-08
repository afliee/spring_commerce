package com.midtern.SpringCommerce.entity;

import jakarta.persistence.*;
import lombok.*;
import org.springframework.context.event.EventListener;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.util.Set;

@Entity
@Table(name = "category")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EntityListeners(AuditingEntityListener.class)
@EqualsAndHashCode(exclude = {"products"}, callSuper = true)
public class Category extends BaseEntity {
    private String name;
    private String description;

    @OneToMany(
            mappedBy = "category",
            targetEntity = Product.class
    )
    private Set<Product> products;
}
