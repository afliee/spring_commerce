package com.midtern.SpringCommerce.controller;

import com.midtern.SpringCommerce.service.AdminService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/admin")
public class AdminController {
    @Autowired
    private AdminService adminService;

    @GetMapping("/login")
    public String login() {
        return "pages/admin/login";
    }


    @GetMapping("/dashboard")
    public String dashboard(HttpServletRequest request) {
        String isAdmin = adminService.requireRole(request);
        return isAdmin != null ? isAdmin :  "pages/admin/dashboard";
    }

    @GetMapping("/category")
    public String category(
            HttpServletRequest request
    ) {
        String isAdmin = adminService.requireRole(request);
        return isAdmin != null ? isAdmin : "pages/admin/category";
    }

    @GetMapping("/category/create")
    public String categoryCreate(
            HttpServletRequest request
    ) {
        String isAdmin = adminService.requireRole(request);
        return isAdmin != null ? isAdmin : "pages/admin/category-create";
    }
}
