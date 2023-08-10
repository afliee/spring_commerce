package com.midtern.SpringCommerce.repository;

import com.midtern.SpringCommerce.entity.OrderDetail;
import org.springframework.data.jpa.repository.JpaRepository;

public interface OrderDetailRepository extends JpaRepository<OrderDetail, String> {
    void deleteAllByProduct_Id(String id);
}
