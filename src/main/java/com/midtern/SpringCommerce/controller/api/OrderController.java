package com.midtern.SpringCommerce.controller.api;

import com.midtern.SpringCommerce.dto.request.OrderRequest;
import com.midtern.SpringCommerce.service.OrderService;
import com.midtern.SpringCommerce.service.UserService;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.util.WebUtils;

@RestController
@RequestMapping("/order")
public class OrderController {
    @Autowired
    private OrderService orderService;
    @Autowired
    private UserService userService;

    @PostMapping("/create")
    public ResponseEntity<?> create(
            HttpServletRequest request,
            @RequestBody OrderRequest order
    ) {
        Cookie user = WebUtils.getCookie(request, "user");

        if (user == null) {
            return ResponseEntity.badRequest().body("User not found");
        }

        if (user.getValue().isEmpty()) {
            return ResponseEntity.badRequest().body("User not found");
        }

        var userEntity = userService.findByUsername(user.getValue());


        return ResponseEntity.ok().body(orderService.create(order, userEntity));
    }

    @DeleteMapping("/reverse/{id}")
    public ResponseEntity<?> reverse(
            HttpServletRequest request,
            @PathVariable("id") String id
    ) {

        Cookie user = WebUtils.getCookie(request, "user");
        if (user == null || user.getValue().isEmpty()) {
            return ResponseEntity.badRequest().body("User not found");
        }

        var userEntity = userService.findByUsername(user.getValue());
        if (userEntity == null) {
            return ResponseEntity.badRequest().body("User not found");
        }

        return ResponseEntity.ok().body(orderService.reverse(id, userEntity));
    }

    @PutMapping("/update/{id}")
    public ResponseEntity<?> update(
            HttpServletRequest request,
            @PathVariable("id") String id,
            @RequestBody OrderRequest order
    ) {
        Cookie user = WebUtils.getCookie(request, "user");
        if (user == null || user.getValue().isEmpty()) {
            return ResponseEntity.badRequest().body("User not found");
        }

        var userEntity = userService.findByUsername(user.getValue());
        if (userEntity == null) {
            return ResponseEntity.badRequest().body("User not found");
        }

        return ResponseEntity.ok().body(orderService.update(id, order, userEntity));
    }
}
