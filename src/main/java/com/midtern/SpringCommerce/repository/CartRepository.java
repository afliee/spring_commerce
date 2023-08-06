package com.midtern.SpringCommerce.repository;

import com.midtern.SpringCommerce.entity.Cart;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CartRepository extends JpaRepository<Cart, String> {
}
