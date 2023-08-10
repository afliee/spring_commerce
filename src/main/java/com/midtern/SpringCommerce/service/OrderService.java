package com.midtern.SpringCommerce.service;

import com.midtern.SpringCommerce.converter.ProductConverter;
import com.midtern.SpringCommerce.dto.request.OrderRequest;
import com.midtern.SpringCommerce.dto.response.OrderDetailResponse;
import com.midtern.SpringCommerce.dto.response.OrderResponse;
import com.midtern.SpringCommerce.entity.Order;
import com.midtern.SpringCommerce.entity.OrderDetail;
import com.midtern.SpringCommerce.entity.User;
import com.midtern.SpringCommerce.repository.*;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class OrderService {
    @Autowired
    private OrderRepository orderRepository;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private OrderDetailRepository orderDetailRepository;
    @Autowired
    private CartRepository cartRepository;
    @Autowired
    private CartProductRepository cartProductRepository;

    @Transactional
    public OrderResponse create(OrderRequest request, User userEntity) {
        var order = Order.builder()
                .firstName(request.getFirstName())
                .lastName(request.getLastName())
                .address(request.getAddress())
                .phone(request.getPhone())
                .email(request.getEmail())
                .username(request.getUsername())
                .build();
        double price = userEntity.getCart().getCartProducts().stream().mapToDouble(cartProduct -> {
            return cartProduct.getProduct().getPrice() * cartProduct.getQuantity();
        }).sum();
        var orderDetails = userEntity.getCart().getCartProducts().stream().map(cartProduct -> {

            return OrderDetail.builder()
                    .product(cartProduct.getProduct())
                    .quantity(cartProduct.getQuantity())
                    .order(order)
                    .build();
        }).collect(Collectors.toSet());

        order.setOrderDetails(orderDetails);
        order.setUser(userEntity);
        order.setTotal(price);
        order.setExpiredDate(LocalDate.now().plusDays(3));
        orderRepository.save(order);

        var cart = userEntity.getCart();
        cart.setTotal(0.0);
        cartRepository.save(cart);
        cartProductRepository.deleteAll(userEntity.getCart().getCartProducts());

        userRepository.save(userEntity);
        orderDetailRepository.saveAll(orderDetails);
        userRepository.findByUsername(userEntity.getUsername()).ifPresent(user -> {
            System.out.println("cart size: " + user.getCart().getCartProducts().size());
        });
        return OrderResponse.builder()
                .id(order.getId())
                .firstName(order.getFirstName())
                .lastName(order.getLastName())
                .address(order.getAddress())
                .phone(order.getPhone())
                .email(order.getEmail())
                .username(order.getUsername())
                .createdDate(order.getCreatedDate())
                .expiredDate(order.getExpiredDate())
                .orderDetails(order.getOrderDetails().stream().map(orderDetail -> {
                    return OrderDetailResponse.builder()
                            .id(orderDetail.getId())
                            .quantity(orderDetail.getQuantity())
                            .product(ProductConverter.toResponse(orderDetail.getProduct()))
                            .build();
                }).collect(Collectors.toSet()))
                .build();
    }

    public List<OrderResponse> findAll() {
        return orderRepository.findAll().stream().map(order -> {
            return OrderResponse.builder()
                    .id(order.getId())
                    .total(order.getTotal())
                    .createdDate(order.getCreatedDate())
                    .expiredDate(order.getExpiredDate())
                    .phone(order.getPhone())
                    .address(order.getAddress())
                    .build();
        }).toList();
    }

    public Order findById(String id) {
        return orderRepository.findById(id).orElse(null);
    }

    public OrderResponse reverse(String id, User userEntity) {
        var order = orderRepository.findById(id).orElse(null);
        if (order == null) {
            return null;
        }
        userEntity.getCart().setTotal(userEntity.getCart().getTotal() - order.getTotal());

//        remove all data relate
        orderDetailRepository.deleteAll(order.getOrderDetails());
        orderRepository.delete(order);

        return OrderResponse.builder()
                .id(order.getId())
                .total(order.getTotal())
                .createdDate(order.getCreatedDate())
                .expiredDate(order.getExpiredDate())
                .phone(order.getPhone())
                .address(order.getAddress())
                .build();
    }

    public OrderResponse update(String id,OrderRequest request, User user) {
        var order = orderRepository.findById(id).orElse(null);
        if (order == null) {
            return null;
        }
        var orderQuantities = request.getCartQuantity().split("&");
        Map<String, Integer> orderQuantityMap = new HashMap<>();
        for (String orderQuantity : orderQuantities) {
            String[] orderQuantityArr = orderQuantity.split("=");
            orderQuantityMap.put(orderQuantityArr[0], Integer.parseInt(orderQuantityArr[1]));
        }

        double price = 0;
        for (var orderQuantity : orderQuantityMap.entrySet()) {
            var orderDetail = orderDetailRepository.findById(orderQuantity.getKey()).orElse(null);
            if (orderDetail == null) {
                return null;
            }

            orderDetail.setQuantity(orderQuantity.getValue());
            orderDetailRepository.save(orderDetail);
        }

        order.setFirstName(request.getFirstName());
        order.setLastName(request.getLastName());
        order.setAddress(request.getAddress());
        order.setPhone(request.getPhone());
        order.setEmail(request.getEmail());
        order.setUsername(request.getUsername());
        order.setTotal(request.getTotal());
        order.setExpiredDate(LocalDate.now().plusDays(3));

        orderRepository.save(order);

        var cart = user.getCart();
        cart.setTotal(0.0);
        cartRepository.save(cart);

        return OrderResponse.builder()
                .id(order.getId())
                .firstName(order.getFirstName())
                .lastName(order.getLastName())
                .address(order.getAddress())
                .phone(order.getPhone())
                .email(order.getEmail())
                .username(order.getUsername())
                .createdDate(order.getCreatedDate())
                .expiredDate(order.getExpiredDate())
                .orderDetails(order.getOrderDetails().stream().map(orderDetail -> {
                    return OrderDetailResponse.builder()
                            .id(orderDetail.getId())
                            .quantity(orderDetail.getQuantity())
                            .product(ProductConverter.toResponse(orderDetail.getProduct()))
                            .build();
                }).collect(Collectors.toSet()))
                .build();

    }
}
