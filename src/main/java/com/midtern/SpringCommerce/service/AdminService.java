package com.midtern.SpringCommerce.service;

import com.midtern.SpringCommerce.constant.Role;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.util.WebUtils;

@Service
public class AdminService {
    @Autowired
    private JwtService jwtService;
    public String requireRole(HttpServletRequest request) {
        Cookie token = WebUtils.getCookie(request, "token");
        if (token == null) {
            return "redirect:/admin/login";
        }
        String jwt = token.getValue();
        boolean isAdmin = jwtService.isAdmin(jwt);
        if (!isAdmin) {
            return "redirect:/admin/login";
        }
        return null;
    }
}
