package com.midtern.SpringCommerce.controller;

import com.midtern.SpringCommerce.service.AdminService;
import com.midtern.SpringCommerce.service.CategoryService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/admin")
public class AdminController {
    @Autowired
    private AdminService adminService;
    @Autowired
    private CategoryService categoryService;

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

    @GetMapping("/category/{id}")
    public String categoryShow(
            @PathVariable(name = "id") String id,
            Model model
    ) {
        var category = categoryService.findById(id);
        model.addAttribute("category", category);
        return "pages/admin/category-show";
    }

    @GetMapping("/category/{id}/product/create")
    public String createProduct(
            @PathVariable(name = "id") String id,
            Model model
    ) {
        var category = categoryService.findById(id);
        model.addAttribute("category", category);
        return "pages/admin/product-create";
    }

    @GetMapping("/product/edit/{id}")
    public String editProduct(
            @PathVariable(name = "id") String id,
            Model model
    ) {
        var product = categoryService.findProductById(id);
        model.addAttribute("product", product);
        return "pages/admin/product-edit";
    }
}
