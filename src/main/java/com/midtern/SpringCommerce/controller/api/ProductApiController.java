package com.midtern.SpringCommerce.controller.api;

import com.midtern.SpringCommerce.dto.request.ProductRequest;
import com.midtern.SpringCommerce.service.ProductService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/product")
public class ProductApiController {
    @Autowired
    private ProductService productService;

    @PostMapping("/create")
    public ResponseEntity<?> create(
            @ModelAttribute ProductRequest request
    ) {
        return ResponseEntity.ok().body(productService.create(request));
    }

    @GetMapping("/get")
    public ResponseEntity<?> get(
            @RequestParam(name = "page", value = "page", defaultValue = "0") int page,
            @RequestParam(name = "size", value = "size", defaultValue = "10") int size,
            @RequestParam(name = "filter", value = "filter", defaultValue = "", required = false) String filter,
            @RequestParam(name = "categoryId", value = "categoryId", defaultValue = "", required = true) String categoryId
    ) {
        if (filter.isEmpty()) {
            return ResponseEntity.ok().body(productService.get(page, size, categoryId));
        }
        return ResponseEntity.ok().body(productService.get(page, size, filter, categoryId));
    }

    @PutMapping("/update/{id}")
    public ResponseEntity<?> update(
            @PathVariable("id") String id,
            @ModelAttribute ProductRequest request
    ) {
        return ResponseEntity.ok().body(productService.update(id, request));
    }

    @DeleteMapping("/delete/{id}")
    public ResponseEntity<?> delete(
            @PathVariable("id") String id
    ) {
        return ResponseEntity.ok().body(productService.delete(id));
    }
}
