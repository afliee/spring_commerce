package com.midtern.SpringCommerce.controller.api;

import com.midtern.SpringCommerce.dto.request.CategoryRequest;
import com.midtern.SpringCommerce.service.CategoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/category")
public class CategoryApiController {
    @Autowired
    private CategoryService categoryService;

    @PostMapping("/create")
    public ResponseEntity<?> create(
            @RequestBody CategoryRequest request
    ) {
        return ResponseEntity.ok().body(categoryService.create(request));
    }

    @GetMapping("/get")
    public ResponseEntity<?> get(
            @RequestParam(name = "page", value = "page", defaultValue = "0") int page,
            @RequestParam(name = "size", value = "size", defaultValue = "10") int size,
            @RequestParam(name = "filter", value = "filter", defaultValue = "", required = false) String filter
    ) {
        if (filter.isEmpty()) {
            return ResponseEntity.ok().body(categoryService.get(page, size));
        }
        return ResponseEntity.ok().body(categoryService.get(page, size, filter));
    }

    @DeleteMapping("/delete/{id}")
    public ResponseEntity<?> delete(
            @PathVariable(name = "id") String id
    ) {
        return ResponseEntity.ok().body(categoryService.delete(id));
    }
}
