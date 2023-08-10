package com.midtern.SpringCommerce.repository;

import com.midtern.SpringCommerce.entity.Product;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Set;

public interface ProductRepository extends JpaRepository<Product, String> {
    Set<Product> findAll(Specification<Product> spec);
    List<Product> findAllByNameIgnoreCase(String name);
    List<Product> findAllByNameIgnoreCaseAndIdNot(String name, String id);
    List<Product> findAllByCategory_Id(String id);
    Page<Product> findAllByCategory_Id(String id, Pageable pageRequest);
    Page<Product> findAllByNameContainingIgnoreCaseAndCategoryId(String name, String categoryId, Pageable pageRequest);
    List<Product> findAllByIdIn(List<String> ids);
}
