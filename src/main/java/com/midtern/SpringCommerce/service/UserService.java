package com.midtern.SpringCommerce.service;

import com.midtern.SpringCommerce.constant.Role;
import com.midtern.SpringCommerce.converter.UserConverter;
import com.midtern.SpringCommerce.dto.request.AuthenticationRequest;
import com.midtern.SpringCommerce.dto.request.RegisterRequest;
import com.midtern.SpringCommerce.dto.response.AuthenticationResponse;
import com.midtern.SpringCommerce.entity.Admin;
import com.midtern.SpringCommerce.entity.Cart;
import com.midtern.SpringCommerce.entity.Token;
import com.midtern.SpringCommerce.entity.User;
import com.midtern.SpringCommerce.exception.UserAlreadyExistException;
import com.midtern.SpringCommerce.repository.AdminRepository;
import com.midtern.SpringCommerce.repository.CartRepository;
import com.midtern.SpringCommerce.repository.TokenRepository;
import com.midtern.SpringCommerce.repository.UserRepository;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.util.WebUtils;

import java.util.Collections;

@Service
public class UserService {
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private AdminRepository adminRepository;
    @Autowired
    private PasswordEncoder passwordEncoder;
    @Autowired
    private TokenRepository tokenRepository;
    @Autowired
    private JwtService jwtService;
    @Autowired
    private CartRepository cartRepository;

    public AuthenticationResponse signup(RegisterRequest request) {
        if (Role.ADMIN.equals(request.getRole())) {
            adminRepository.findByUsername(request.getUsername()).ifPresent((userEntity) -> {
                throw new UserAlreadyExistException(userEntity.getUsername() + " is already exist");
            });

            userRepository.findByUsername(request.getUsername()).ifPresent((userEntity) -> {
                throw new UserAlreadyExistException(userEntity.getUsername() + " is already exist");
            });

            var admin = User.builder()
                    .username(request.getUsername())
                    .password(passwordEncoder.encode(request.getPassword()))
                    .roles(Collections.singleton(request.getRole()))
                    .build();

            var jwtToken = jwtService.generateToken(admin);
            adminRepository.save(Admin.builder()
                    .username(admin.getUsername())
                    .password(admin.getPassword())
                    .build()
            );
            userRepository.save(admin);
            saveUserToken(admin, jwtToken);
            return AuthenticationResponse.builder()
                    .token(jwtToken)
                    .user(UserConverter.toResponse(admin, new String[]{"username"}))
                    .build();
        }


        userRepository.findByUsername(request.getUsername()).ifPresent((userEntity) -> {
            throw new UserAlreadyExistException(userEntity.getUsername() + " is already exist");
        });

        var cart = Cart.builder()
                .build();


        var user = User.builder()
                .username(request.getUsername())
                .password(passwordEncoder.encode(request.getPassword()))
                .roles(Collections.singleton(request.getRole()))
                .cart(cart)
                .build();

//        var user = new User(request.getUsername(), passwordEncoder.encode(request.getPassword()), Collections.singleton(request.getRole()), cart

        cartRepository.save(cart);
        var jwtToken = jwtService.generateToken(user);
        var userSaved = userRepository.save(user);
        saveUserToken(userSaved, jwtToken);
        return AuthenticationResponse.builder()
                .token(jwtToken)
                .user(UserConverter.toResponse(userSaved, new String[]{"username", "avatar"}))
                .build();
    }

    public AuthenticationResponse authenticate(
            AuthenticationRequest authenticationRequest,
            HttpServletResponse response,
            HttpServletRequest request
    ) {

        var user = userRepository.findByUsername(authenticationRequest.getUsername())
                .orElseThrow(() -> new RuntimeException("User not found"));
        if (!passwordEncoder.matches(authenticationRequest.getPassword(), user.getPassword())) {
            throw new RuntimeException("Password is incorrect");
        }

        var jwtToken = jwtService.generateToken(user);
        revokeToken(user);
        saveUserToken(user, jwtToken);
        Cookie cookie = new Cookie("token", jwtToken);
        cookie.setPath("/");
        cookie.setHttpOnly(true);
//         set age 1 day
        cookie.setMaxAge(60 * 60 * 24);

        Cookie userCookie = new Cookie("user", user.getUsername());
        userCookie.setPath("/");
        userCookie.setMaxAge(60 * 60 * 24);

        response.addCookie(userCookie);

        if (user.getCart() != null) {
            System.out.println("Cart found");
            StringBuilder stringBuilder = new StringBuilder();
            user.getCart().getCartProducts().forEach(product -> {
                stringBuilder.append(product.getId());
                stringBuilder.append("/");
            });

//            merge cart
            Cookie productCookie = WebUtils.getCookie(request, "products");
            if (productCookie != null && productCookie.getValue() != null) {
                System.out.println("Cookie product found");
                System.out.println(productCookie.getValue());
                stringBuilder.append(productCookie.getValue());
                productCookie.setMaxAge(60 * 60 * 24 * 30);
                productCookie.setPath("/");
                response.addCookie(productCookie);
            } else {
                System.out.println("Cookie product not found");
            }
            Cookie products = new Cookie("products",stringBuilder.toString() );
            products.setPath("/");
            products.setMaxAge(60 * 60 * 24);
            response.addCookie(products);
        } else {
            Cookie products = WebUtils.getCookie(request, "products");

            if (products != null && products.getValue() != null) {
                System.out.println("Cookie product found");
                System.out.println(products.getValue());
                products.setMaxAge(0);
                response.addCookie(products);
            } else {
                System.out.println("Cookie product not found");
            }
        }


        if (user.getRoles().contains(Role.ADMIN)) {
            response.addCookie(cookie);
            return AuthenticationResponse.builder()
                    .token(jwtToken)
                    .user(UserConverter.toResponse(user, new String[]{"username"}))
                    .build();
        }

        response.addCookie(cookie);
        return AuthenticationResponse.builder()
                .token(jwtToken)
                .user(UserConverter.toResponse(user, new String[]{"username", "avatar"}))
                .build();
    }

    private void saveUserToken(User user, String jwtToken) {
        var token = Token.builder()
                .user(user)
                .token(jwtToken)
                .expired(false)
                .revoked(false)
                .build();
        tokenRepository.save(token);
    }

    public void revokeToken(User user) {
        var tokens = tokenRepository.findAllTokenValid(user.getId());
        if (tokens.isEmpty()) {
            return;
        }

        tokens.forEach(token -> {
            token.setExpired(true);
            token.setRevoked(true);
        });

        tokenRepository.saveAll(tokens);
    }

    public User findByUsername(String username) {
        return userRepository.findByUsername(username).orElseThrow(() -> new RuntimeException("User not found"));
    }
}
