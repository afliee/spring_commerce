package com.midtern.SpringCommerce.entity;

import jakarta.persistence.*;
import lombok.*;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDate;
import java.util.Set;

@Entity
@Table(name = "`order`")
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Data
@EntityListeners(AuditingEntityListener.class)
@EqualsAndHashCode(exclude = {"orderDetails", "user"}, callSuper = false)
public class Order extends BaseEntity {
    @Column(name = "`total`")
    private Double total;

    @Column(
            name = "address",
            columnDefinition = "TEXT"
    )
    private String address;
    @Column(
            name = "address2",
            columnDefinition = "TEXT"
    )
    private String address2;

    @Column(name = "phone")
    private String phone;
    private String firstName;
    private String lastName;
    private String username;
    private String email;

    @ManyToOne(targetEntity = User.class)
    @JoinColumn(name = "user_id")
    private User user;

    @OneToMany(
            mappedBy = "order",
            targetEntity = OrderDetail.class
    )
    private Set<OrderDetail> orderDetails;

    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private LocalDate expiredDate;
}
