package com.midtern.SpringCommerce.repository;

import com.midtern.SpringCommerce.entity.Product;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ProductRepository extends JpaRepository<Product, String> {
    List<Product> findAllByNameIgnoreCase(String name);
    List<Product> findAllByNameIgnoreCaseAndIdNot(String name, String id);
    List<Product> findAllByCategory_Id(String id);
    Page<Product> findAllByCategory_Id(String id, Pageable pageRequest);
    Page<Product> findAllByNameContainingIgnoreCaseAndCategoryId(String name, String categoryId, Pageable pageRequest);
}
