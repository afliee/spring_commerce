package com.midtern.SpringCommerce.service;

import com.midtern.SpringCommerce.converter.CategoryConverter;
import com.midtern.SpringCommerce.dto.request.CategoryRequest;
import com.midtern.SpringCommerce.dto.response.CategoryResponse;
import com.midtern.SpringCommerce.dto.response.ProductResponse;
import com.midtern.SpringCommerce.entity.Category;
import com.midtern.SpringCommerce.entity.Product;
import com.midtern.SpringCommerce.repository.CategoryRepositoryImpl;
import com.midtern.SpringCommerce.repository.ProductRepository;
import com.midtern.SpringCommerce.utils.CategorySpecification;
import com.midtern.SpringCommerce.utils.SearchCriteria;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class CategoryService {
    @Autowired
    private CategoryRepositoryImpl categoryRepository;
    @Autowired
    private ProductRepository productRepository;

    public CategoryResponse create(CategoryRequest request) {
        categoryRepository.findByName(request.getName()).ifPresent(category -> {
            throw new RuntimeException("Category already exists");
        });

        var category = categoryRepository.save(CategoryConverter.toEntity(request));

        return CategoryConverter.toResponse(category);
    }

    public Page<?> get(int page, int size) {
        PageRequest pageRequest = PageRequest.of(page, size);
        return categoryRepository.findAll(pageRequest).map(CategoryConverter::toResponse);
    }

    public Page<?> get(int page, int size, String filter) {
        PageRequest pageRequest = PageRequest.of(page, size);
        List<SearchCriteria> params = new ArrayList<>();
        List<String> fieldsArr = new ArrayList<>();

        var fields = Category.class.getDeclaredFields();
        for (var field : fields) {
//            get type of field;
            var type = field.getType();
            if (type == String.class) {
                fieldsArr.add(field.getName());
            }
        }

        for (var field : fieldsArr) {
            params.add(new SearchCriteria(field, ":" , filter));
        }
        Specification<Category> specification = Specification.where(null);
        for (SearchCriteria param : params) {
            specification = specification.or(CategorySpecification.builder().criteria(param).build());
        }

        return categoryRepository.findAll(specification, pageRequest).map(CategoryConverter::toResponse);
    }

    public CategoryResponse findById(String id) {
        return CategoryConverter.toResponse(categoryRepository.findById(id).orElseThrow(() -> new RuntimeException("Category not found")));
    }

    @Transactional
    public CategoryResponse delete(String id) {
        var category = categoryRepository.findById(id).orElseThrow(() -> new RuntimeException("Category not found"));

        var products = productRepository.findAllByCategory_Id(id);
        if (!products.isEmpty()) {
            productRepository.deleteAll(products);
        }

        categoryRepository.delete(category);
        return CategoryConverter.toResponse(category);
    }

    public Product findProductById(String id) {
        return productRepository.findById(id).orElseThrow(() -> new RuntimeException("Product not found"));
    }
}
