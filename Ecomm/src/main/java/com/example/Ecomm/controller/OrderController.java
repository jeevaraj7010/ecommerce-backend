package com.example.Ecomm.controller;

import com.example.Ecomm.entity.Orders;
import com.example.Ecomm.service.OrderService;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/orders")
public class OrderController {

    private final OrderService orderService;

    public OrderController(OrderService orderService) {
        this.orderService = orderService;
    }

    // 👤 USER places order
    @PostMapping("/{productId}/{quantity}")
    public Orders placeOrder(@PathVariable Long productId,
                             @PathVariable int quantity,
                             Authentication authentication) {

        return orderService.placeOrder(
                authentication.getName(),
                productId,
                quantity
        );
    }

    // 👑 ADMIN view all orders
    @GetMapping("/all")
    public List<Orders> getAllOrders() {
        return orderService.getAllOrders();
    }

    // 👤 USER view their own orders
    @GetMapping
    public List<Orders> getUserOrders(Authentication authentication) {
        return orderService.getUserOrders(authentication.getName());
    }
}