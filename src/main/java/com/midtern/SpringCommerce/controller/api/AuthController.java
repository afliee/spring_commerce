package com.midtern.SpringCommerce.controller.api;

import com.midtern.SpringCommerce.dto.request.AuthenticationRequest;
import com.midtern.SpringCommerce.dto.request.RegisterRequest;
import com.midtern.SpringCommerce.service.UserService;
import jakarta.servlet.http.HttpServletResponse;
import org.apache.coyote.Response;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/auth")
public class AuthController {
    @Autowired
    private UserService userService;

    @PostMapping("/signup")
    public ResponseEntity<?> signup(
            @RequestBody RegisterRequest registerRequest
    ) {
        return ResponseEntity.ok(userService.signup(registerRequest));
    }

    @PostMapping("/authenticate")
    public ResponseEntity<?> authentication(
            @RequestBody AuthenticationRequest authenticationRequest,
            HttpServletResponse response
    ) {
        return ResponseEntity.ok(userService.authenticate(authenticationRequest, response));
    }
}
