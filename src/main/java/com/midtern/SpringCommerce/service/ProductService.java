package com.midtern.SpringCommerce.service;

import com.midtern.SpringCommerce.converter.ProductConverter;
import com.midtern.SpringCommerce.dto.request.ProductRequest;
import com.midtern.SpringCommerce.dto.response.CategoryResponse;
import com.midtern.SpringCommerce.dto.response.ProductResponse;
import com.midtern.SpringCommerce.exception.NotFoundException;
import com.midtern.SpringCommerce.repository.CategoryRepositoryImpl;
import com.midtern.SpringCommerce.repository.ProductRepository;
import com.midtern.SpringCommerce.utils.FileUploadUtil;
import org.apache.tomcat.util.http.fileupload.FileUpload;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.HashMap;
import java.util.Map;

@Service
public class ProductService {
    @Autowired
    private ProductRepository productRepository;
    @Autowired
    private CategoryRepositoryImpl categoryRepository;


    public ProductResponse create(ProductRequest request) {
        if (!productRepository.findAllByNameIgnoreCase(request.getName()).isEmpty()) {
            throw new RuntimeException("Product name is already exist");
        }
        var category = categoryRepository.findById(request.getCategoryId()).orElseThrow(
                () -> new RuntimeException("Category not found")
        );


        var productEntity = ProductConverter.toEntity(request);
        var productSaved = productRepository.save(productEntity);
        productSaved.setCategory(category);
        String id = productSaved.getId();

        try {
            FileUploadUtil.saveFile("product", id + ".png", request.getImage());
            productSaved.setImage("/uploads/product/" + id + ".png");
            category.getProducts().add(productSaved);
            categoryRepository.save(category);
            productRepository.save(productSaved);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

        return ProductConverter.toResponse(productSaved);
    }

    public ProductResponse delete(String id) {
        var product = productRepository.findById(id).orElseThrow(
                () -> new RuntimeException("Product not found")
        );
        categoryRepository.findById(product.getCategory().getId()).ifPresent(
                category -> {
                    category.getProducts().remove(product);
                    categoryRepository.save(category);
                }
        );
//        delete file in server
        String filePath = product.getImage();
        try {
            FileUploadUtil.deleteFile(filePath);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        productRepository.delete(product);
        return ProductConverter.toResponse(product);
    }

    public Page<?> get(int page, int size, String categoryId) {
        PageRequest pageRequest = PageRequest.of(page, size);
        return productRepository.findAllByCategory_Id(categoryId ,pageRequest).map(ProductConverter::toResponse);
    }

    public Page<?> get(int page, int size, String filter, String categoryId) {
        PageRequest pageRequest = PageRequest.of(page, size);
        return productRepository.findAllByNameContainingIgnoreCaseAndCategoryId(filter, categoryId, pageRequest).map(ProductConverter::toResponse);
    }

    public ProductResponse update(String id, ProductRequest request) {
        var product = productRepository.findById(id).orElseThrow(
                () -> new NotFoundException("Product not found")
        );

        var fields = ProductRequest.class.getDeclaredFields();
        Map<String, Object> updates = new HashMap<>();
        for (var field : fields) {
            try {
                field.setAccessible(true);
                var value = field.get(request);
                if (value != null) {
                    updates.put(field.getName(), value);
                }
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }

        if (updates.containsKey("name")) {
            if (!productRepository.findAllByNameIgnoreCaseAndIdNot(request.getName(), id).isEmpty()) {
                throw new RuntimeException("Product name is already exist");
            }
        }

        for (var entry : updates.entrySet()) {
            try {
                var field = product.getClass().getDeclaredField(entry.getKey());
                field.setAccessible(true);
                if (entry.getValue() instanceof MultipartFile) {
                    String filePath = product.getImage();
                    FileUploadUtil.deleteFile(filePath);
                    FileUploadUtil.saveFile("product", id + ".png", (MultipartFile) entry.getValue());
                    field.set(product, "/uploads/product/" + id + ".png");
                } else {
                    field.set(product, entry.getValue());
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        return ProductConverter.toResponse(productRepository.save(product));
    }
}
