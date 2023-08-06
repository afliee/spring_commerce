package com.midtern.SpringCommerce.controller.api;

import com.midtern.SpringCommerce.dto.request.CategoryRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/category")
public class CategoryApiController {
    @PostMapping("/create")
    public ResponseEntity<?> create(
            @RequestBody CategoryRequest request
    ) {
        return null;
    }
}
