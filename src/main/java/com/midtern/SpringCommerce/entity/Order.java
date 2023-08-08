package com.midtern.SpringCommerce.entity;

import jakarta.persistence.*;
import lombok.*;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.util.Set;

@EqualsAndHashCode(callSuper = true)
@Entity
@Table(name = "`order`")
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Data
@EntityListeners(AuditingEntityListener.class)
public class Order extends BaseEntity {
    @Column(name = "`total`",
            columnDefinition = "INT(11) DEFAULT 0",
            nullable = false
    )
    private Integer total;

    @Column(
            name = "address",
            columnDefinition = "TEXT"
    )
    private String address;

    @Column(name = "phone")
    private String phone;

    @ManyToOne(targetEntity = User.class)
    @JoinColumn(name = "user_id")
    private User user;

    @OneToMany(
            mappedBy = "order",
            targetEntity = OrderDetail.class
    )
    private Set<OrderDetail> orderDetails;
}
