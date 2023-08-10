package com.midtern.SpringCommerce.repository;

import com.midtern.SpringCommerce.entity.CartProduct;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface CartProductRepository extends JpaRepository<CartProduct, String> {
    Optional<CartProduct> findByCartIdAndProductId(String cartId, String productId);

    void deleteAllByCart_IdAndProduct_Id(String cartId, String productId);
    void deleteAllByProduct_Id(String productId);
}
