package com.midtern.SpringCommerce.repository;

import com.midtern.SpringCommerce.entity.Category;
import org.antlr.v4.runtime.misc.NotNull;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.Optional;

public interface CategoryRepositoryImpl extends JpaRepository<Category, String>, JpaSpecificationExecutor<Category> {

    Optional<Category> findByName(String name);

}
