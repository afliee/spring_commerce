package com.midtern.SpringCommerce.controller;

import com.midtern.SpringCommerce.converter.ProductConverter;
import com.midtern.SpringCommerce.dto.response.CategoryResponse;
import com.midtern.SpringCommerce.dto.response.ProductResponse;
import com.midtern.SpringCommerce.entity.CartProduct;
import com.midtern.SpringCommerce.repository.CartProductRepository;
import com.midtern.SpringCommerce.service.CategoryService;
import com.midtern.SpringCommerce.service.OrderService;
import com.midtern.SpringCommerce.service.ProductService;
import com.midtern.SpringCommerce.service.UserService;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.util.WebUtils;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

@Controller
@RequestMapping("/")
@RequiredArgsConstructor
public class HomeController {
    private final CategoryService categoryService;
    private final ProductService productService;
    private final UserService userService;
    private final OrderService orderService;
    private final CartProductRepository cartProductRepository;

    @RequestMapping
    public String index(
            HttpServletRequest request,
            HttpServletResponse response,
            Model model
    ) {
        List<CategoryResponse> categories = categoryService.findAll(new String[] {
                "id", "name", "products"
        });
        Cookie products = WebUtils.getCookie(request, "products");
        Cookie user = WebUtils.getCookie(request, "user");
        if (products == null) {
            Cookie cookie = new Cookie("products", "");
            cookie.setMaxAge(60 * 60 * 24 * 30);
            cookie.setPath("/");
//            cookie.setHttpOnly(true);
            response.addCookie(cookie);
        }
        if (user != null) {
            var userEntity = userService.findByUsername(user.getValue());
            model.addAttribute("user", userEntity);

            Cookie product = new Cookie("products", "");
            product.setMaxAge(60 * 60 * 24 * 30);
            product.setPath("/");
//            cookie.setHttpOnly(true);
            response.addCookie(product);
        }
        model.addAttribute("categories", categories);
        return "pages/user/index";
    }

    @GetMapping("/login")
    public String login(
            HttpServletRequest request,
            HttpServletResponse response,
            Model model
    ) {
        return "pages/user/signin";
    }


    @GetMapping("/gallery")
    public String gallery(
            @RequestParam(value = "search", required = false) String search,
            Model model
    ) {
        if (search != null) {
            var products = productService.getAll(search);
            model.addAttribute("products", products);
        }
        List<CategoryResponse> categories = categoryService.findAll(new String[] {
                "id", "name", "products"
        });
        model.addAttribute("categories", categories);
        return "pages/user/gallery";
    }

    @GetMapping("/cart")
    public String cart(
            HttpServletRequest request,
            HttpServletResponse response,
            Model model
    ) {

        Cookie products = WebUtils.getCookie(request, "products");

        if (products != null && !products.getValue().isEmpty()) {
            Cookie userCookie = WebUtils.getCookie(request, "user");
            if (userCookie != null) {
                var user = userService.findByUsername(userCookie.getValue());
                System.out.println("product != null && user != null + " + user.getCart().getCartProducts().size());
                StringBuilder productsValue = new StringBuilder();
                user.getCart().getCartProducts().forEach(cartProduct -> {
                    productsValue.append(cartProduct.getProduct().getId()).append("/");
                });
                System.out.println("user" + user.getUsername());
                model.addAttribute("user", user);

                productsValue.append(products.getValue());
                Cookie cookie = new Cookie("products", productsValue.toString());
                cookie.setMaxAge(60 * 60 * 24 * 30);
                cookie.setPath("/");
//            cookie.setHttpOnly(true);
                response.addCookie(cookie);
                System.out.println("user cart size "  + user.getCart().getCartProducts().size());
                String[] productIds = products.getValue().split("/");
                System.out.println("productIds = " + productIds.toString());
                var productByCookie = productService.findAllById(productIds);
                var productResponses = ProductConverter.toResponseCartProduct(user.getCart().getCartProducts());

                if (productByCookie.size() > 0) {
                    var cartProduct = productByCookie.stream().map(productResponse -> {
                        var entity = cartProductRepository.findByCartIdAndProductId(user.getCart().getId(), productResponse.getId()).orElse(null);
                        if (entity != null) {
                            entity.setQuantity(entity.getQuantity() + 1);
                            return entity;
                        } else {
                            return CartProduct.builder()
                                    .cart(user.getCart())
                                    .product(productService.findById(productResponse.getId()))
                                    .quantity(1)
                                    .build();
                        }
                    }).toList();

                    user.getCart().getCartProducts().addAll(cartProduct);
                    cartProductRepository.saveAll(cartProduct);

                    var productResponses1 = ProductConverter.toResponseCartProduct(user.getCart().getCartProducts());
                    model.addAttribute("products", productResponses1);
                    return "pages/user/cart";
                }
                model.addAttribute("products", productResponses);
                return "pages/user/cart";
            }


            String[] productIds = products.getValue().split("/");
            System.out.println("productIds = " + productIds.toString());
            Set<ProductResponse> productResponses = productService.findAllById(productIds);
            Map<String, ProductResponse> productMap = new HashMap<>();
            double total = 0;
            for (var product : productResponses) {
                if (productMap.containsKey(product.getId())) {
                    productMap.get(product.getId()).setAmount(productMap.get(product.getId()).getAmount() + 1);
                } else {
                    product.setAmount(1);
                    productMap.put(product.getId(), product);
                }
                total += product.getPrice();
            }
            model.addAttribute("total", total);
            model.addAttribute("products", productMap.values());
        } else {
            Cookie userCookie = WebUtils.getCookie(request, "user");
            if (userCookie != null && !userCookie.getValue().isEmpty()) {
                var user = userService.findByUsername(userCookie.getValue());
                StringBuilder productsValue = new StringBuilder();
//                System.out.println("pruduct == null or empty && user != null ++ " + user.getCart().getCartProducts().size());
               if (user.getCart() != null) {
                   user.getCart().getCartProducts().forEach(cartProduct -> {
                       productsValue.append(cartProduct.getProduct().getId()).append("/");
                   });
                   System.out.println("user" + user.getUsername());
                   Cookie cookie = new Cookie("products", productsValue.toString());
                   cookie.setMaxAge(60 * 60 * 24 * 30);
                   cookie.setPath("/");
                   response.addCookie(cookie);
                   model.addAttribute("user", user);
                   System.out.println("user cart size "  + user.getCart().getCartProducts().size());
                   var productResponses = ProductConverter.toResponseCartProduct(user.getCart().getCartProducts());
                   model.addAttribute("products", productResponses);

               }
//            cookie.setHttpOnly(true);



            } else {
                System.out.println("user null");
                return "redirect:/login";
            }
        }

        return "pages/user/cart";
    }

    @GetMapping("/checkout")
    public String checkout(
            HttpServletRequest request,
            HttpServletResponse response,
            Model model
    ) {
        Cookie userCookie = WebUtils.getCookie(request, "user");
        if (userCookie != null) {
            var user = userService.findByUsername(userCookie.getValue());
            StringBuilder productsValue = new StringBuilder();
            user.getCart().getCartProducts().forEach(cartProduct -> {
                productsValue.append(cartProduct.getProduct().getId()).append("/");
            });
            System.out.println("user" + user.getUsername());
            Cookie cookie = new Cookie("products", productsValue.toString());
            cookie.setMaxAge(60 * 60 * 24 * 30);
            cookie.setPath("/");
//            cookie.setHttpOnly(true);
            response.addCookie(cookie);
            System.out.println("user cart size "  + user.getCart().getCartProducts().size());
            var productResponses = ProductConverter.toResponseCartProduct(user.getCart().getCartProducts());

            model.addAttribute("user", user);
            model.addAttribute("products", productResponses);
        } else {
            Cookie products = WebUtils.getCookie(request, "products");
            if (products == null || products.getValue().isEmpty()) {
                return "redirect:/login";
            }

            System.out.println("user null");
            String[] productIds = products.getValue().split("/");
            System.out.println("productIds = " + productIds.toString());
            Set<ProductResponse> productResponses = productService.findAllById(productIds);
            Map<String, ProductResponse> productMap = new HashMap<>();
            double total = 0;
            for (var product : productResponses) {
                if (productMap.containsKey(product.getId())) {
                    productMap.get(product.getId()).setAmount(productMap.get(product.getId()).getAmount() + 1);
                } else {
                    product.setAmount(1);
                    productMap.put(product.getId(), product);
                }
                total += product.getPrice();
            }
            model.addAttribute("total", total);
            model.addAttribute("products", productMap.values());
        }
        return "pages/user/checkout";
    }

    @GetMapping("/orders")
    public String orders(
            HttpServletRequest request,
            HttpServletResponse response,
            Model model
    ) {
        Cookie user = WebUtils.getCookie(request, "user");
        if (user == null || user.getValue().isEmpty()) {
            return "redirect:/login";
        }

        var userEntity = userService.findByUsername(user.getValue());

        model.addAttribute("user", userEntity);
        return "pages/user/orders";
    }

    @GetMapping("/order/{id}")
    public String order(
            @PathVariable("id") String id,
            HttpServletRequest request,
            HttpServletResponse response,
            Model model
    ) {
        Cookie user = WebUtils.getCookie(request, "user");
        if (user == null || user.getValue().isEmpty()) {
            return "redirect:/login";
        }

        var userEntity = userService.findByUsername(user.getValue());
        if (userEntity == null) {
            return "redirect:/login";
        }

        var order = orderService.findById(id);
        model.addAttribute("user", userEntity);
        model.addAttribute("order", order);
        return "pages/user/order";
    }

}
