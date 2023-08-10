package com.midtern.SpringCommerce.repository;

import com.midtern.SpringCommerce.entity.Order;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface OrderRepository extends JpaRepository<Order, String> {
    List<Order> findAllByOrderDetails_Product_Id(String productId);
}
