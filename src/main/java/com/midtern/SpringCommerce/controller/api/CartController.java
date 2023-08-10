package com.midtern.SpringCommerce.controller.api;

import com.midtern.SpringCommerce.dto.request.CartProductRequest;
import com.midtern.SpringCommerce.entity.CartProduct;
import com.midtern.SpringCommerce.entity.Product;
import com.midtern.SpringCommerce.exception.NotFoundException;
import com.midtern.SpringCommerce.repository.CartProductRepository;
import com.midtern.SpringCommerce.repository.ProductRepository;
import com.midtern.SpringCommerce.repository.UserRepository;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.util.WebUtils;

import java.util.HashMap;
import java.util.List;
import java.util.stream.Stream;

@RestController
@RequestMapping("/cart")
public class CartController {
    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private CartProductRepository cartProductRepository;

    @PostMapping("/add")
    public ResponseEntity<?> add(
            HttpServletRequest request,
            @RequestParam(value = "productId") String productId
    ) {
        var product = productRepository.findById(productId).orElseThrow(
                () -> new NotFoundException("Product not found")
        );

        Cookie userCookie = WebUtils.getCookie(request, "user");

        if (userCookie == null) {
            return ResponseEntity.badRequest().body("User is required");
        }

        if (userCookie.getValue().isEmpty()) {
            return ResponseEntity.badRequest().body("User is required");
        }

        var user = userRepository.findByUsername(userCookie.getValue()).orElseThrow(
                () -> new NotFoundException("User not found")
        );

        var cart = user.getCart();

        var cartProduct = cartProductRepository.findByCartIdAndProductId(cart.getId(), product.getId()).orElse(null);

        if (cartProduct != null) {
            cartProduct.setQuantity(cartProduct.getQuantity() + 1);
            cartProductRepository.save(cartProduct);
        } else {
            var cartProductBuilder = CartProduct.builder()
                    .cart(cart)
                    .product(product)
                    .quantity(1)
                    .build();
            cart.getCartProducts().add(cartProductBuilder);
            cartProductRepository.save(cartProductBuilder);
        }

        cart.setTotal(cart.getTotal() + product.getPrice());
        userRepository.save(user);

        return ResponseEntity.ok().build();
    }

    @PostMapping("/merge")
    public ResponseEntity<?> merge(
            HttpServletRequest request,
            HttpServletResponse response,
            @RequestBody String username
    ) {
        if (username == null) {
            return ResponseEntity.badRequest().body("Username is required");
        }

        Cookie usernameCookie = WebUtils.getCookie(request, "user");
        if (usernameCookie == null) {
            return ResponseEntity.badRequest().body("Username is required");
        }

        String usernameValue = usernameCookie.getValue();
        var user = userRepository.findByUsername(usernameValue).orElseThrow(
                () -> new NotFoundException("User not found")
        );

        Cookie productsCookie = WebUtils.getCookie(request, "products");

        if (productsCookie == null || productsCookie.getValue().isEmpty()) {
            return ResponseEntity.ok().body(user.getCart());
        }

        String[] productIds = productsCookie.getValue().split("/");

        List<Product> products = Stream.of(productIds).map(productId -> {
            return productRepository.findById(productId).orElseThrow(
                    () -> new NotFoundException("Product not found")
            );
        }).toList();

        var cart = user.getCart();
        var total = cart.getTotal();
        var cartProducts = cart.getCartProducts();

        for (Product product : products) {
            var cartProduct = cartProductRepository.findByCartIdAndProductId(cart.getId(), product.getId()).orElse(null);

            if (cartProduct != null) {
                cartProduct.setQuantity(cartProduct.getQuantity() + 1);
                cartProductRepository.save(cartProduct);
            } else {
                var cartProductBuilder = CartProduct.builder()
                        .cart(cart)
                        .product(product)
                        .quantity(1)
                        .build();
                cartProducts.add(cartProductBuilder);
                cartProductRepository.save(cartProductBuilder);
            }

            total += product.getPrice();
        }

        cart.setTotal(total);
        userRepository.save(user);

        Cookie cookie = new Cookie("products", "");
        cookie.setMaxAge(60 * 60 * 24 * 30);
        cookie.setPath("/");
        response.addCookie(cookie);

        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/remove/{productId}")
    public ResponseEntity<?> remove(
            @PathVariable(value = "productId") String productId,
            HttpServletRequest request
    ) {
        Cookie userCookie = WebUtils.getCookie(request, "user");

        if (userCookie == null) {
            return ResponseEntity.badRequest().body("User is required");
        }

        if (userCookie.getValue().isEmpty()) {
            return ResponseEntity.badRequest().body("User is required");
        }

        var user = userRepository.findByUsername(userCookie.getValue()).orElseThrow(
                () -> new NotFoundException("User not found")
        );

        var cart = user.getCart();

        var cartProduct = cartProductRepository.findByCartIdAndProductId(cart.getId(), productId).orElseThrow(
                () -> new NotFoundException("Cart product not found")
        );

        cart.setTotal(cart.getTotal() - cartProduct.getProduct().getPrice() * cartProduct.getQuantity());

        cartProductRepository.delete(cartProduct);
        cart.getCartProducts().remove(cartProduct);
        userRepository.save(user);

        return ResponseEntity.ok()
                .body(cart.getTotal());
    }

    @PutMapping("/update")
    public ResponseEntity<?> update(
            @RequestBody CartProductRequest cartProductRequest,
            HttpServletRequest request
    ) {
        Cookie userCookie = WebUtils.getCookie(request, "user");
        if (userCookie == null) {
            return ResponseEntity.badRequest().body("User is required");
        }

        if (userCookie.getValue().isEmpty()) {
            return ResponseEntity.badRequest().body("User is required");
        }

        var user = userRepository.findByUsername(userCookie.getValue()).orElseThrow(
                () -> new NotFoundException("User not found")
        );

        var cart = user.getCart();
        cartProductRequest.getProducts().forEach(product -> {
            var cartProduct = cartProductRepository.findByCartIdAndProductId(cart.getId(), product.getProductId()).orElseThrow(
                    () -> new NotFoundException("Cart product not found")
            );

            cartProduct.setQuantity(product.getQuantity());

            cartProductRepository.save(cartProduct);
        });

        var total = cartProductRequest.getProducts().stream().mapToDouble(product -> {
            var cartProduct = cartProductRepository.findByCartIdAndProductId(cart.getId(), product.getProductId()).orElseThrow(
                    () -> new NotFoundException("Cart product not found")
            );

            return cartProduct.getProduct().getPrice() * cartProduct.getQuantity();
        }).sum();

        cart.setTotal(total);

        userRepository.save(user);

        return ResponseEntity.ok().body(cart.getTotal());
    }
}
