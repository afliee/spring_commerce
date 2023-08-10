package com.midtern.SpringCommerce.service;

import com.midtern.SpringCommerce.converter.ProductConverter;
import com.midtern.SpringCommerce.dto.request.ProductRequest;
import com.midtern.SpringCommerce.dto.response.CategoryResponse;
import com.midtern.SpringCommerce.dto.response.ProductResponse;
import com.midtern.SpringCommerce.entity.Category;
import com.midtern.SpringCommerce.entity.Product;
import com.midtern.SpringCommerce.exception.NotFoundException;
import com.midtern.SpringCommerce.repository.*;
import com.midtern.SpringCommerce.utils.CategorySpecification;
import com.midtern.SpringCommerce.utils.FileUploadUtil;
import com.midtern.SpringCommerce.utils.ProductSpecification;
import com.midtern.SpringCommerce.utils.SearchCriteria;
import jakarta.transaction.Transactional;
import org.apache.tomcat.util.http.fileupload.FileUpload;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class ProductService {
    @Autowired
    private ProductRepository productRepository;
    @Autowired
    private CategoryRepositoryImpl categoryRepository;

    @Autowired
    private CartProductRepository cartProductRepository;
    @Autowired
    private OrderRepository orderRepository;
    @Autowired
    private OrderDetailRepository orderDetailRepository;


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

    @Transactional
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

        cartProductRepository.deleteAllByProduct_Id(id);
        orderRepository.findAllByOrderDetails_Product_Id(id).forEach(
                order -> {
                    order.getOrderDetails().removeIf(orderDetail -> orderDetail.getProduct().getId().equals(id));
                    orderRepository.save(order);
                }
        );
        orderDetailRepository.deleteAllByProduct_Id(id);

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

    public Set<ProductResponse> getAll() {
        return ProductConverter.toResponse(new HashSet<>(productRepository.findAll()));
    }

    public Set<ProductResponse> getAll(String filter) {
        List<SearchCriteria> params = new ArrayList<>();
        List<String> fieldsArr = new ArrayList<>();

        var fields = Product.class.getDeclaredFields();
        for (var field : fields) {
            var type = field.getType();
            if (type == String.class) {
                fieldsArr.add(field.getName());
            }
        }

        for (var field : fieldsArr) {
            params.add(new SearchCriteria(field, ":", filter));
        }

        Specification<Product> specification = Specification.where(null);
        for (SearchCriteria param : params) {
            specification = specification.or(ProductSpecification.builder().criteria(param).build());
        }

        return ProductConverter.toResponse(new HashSet<>(productRepository.findAll(specification)));
    }

    public ProductResponse getById(String id) {
        return ProductConverter.toResponse(productRepository.findById(id).orElseThrow(
                () -> new NotFoundException("Product not found")
        ));
    }

    public Set<ProductResponse> findAllById(String[] ids) {
        return ProductConverter.toResponse(new HashSet<>(productRepository.findAllByIdIn(Arrays.asList(ids))));
    }

    public Product findById(String id) {
        return productRepository.findById(id).orElseThrow(
                () -> new NotFoundException("Product not found")
        );
    }
}
