package com.example.Ecomm.controller;

import com.example.Ecomm.entity.Orders;
import com.example.Ecomm.service.OrderService;
import org.springframework.data.domain.PageRequest;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/orders")
public class OrderController {

    private final OrderService orderService;

    public OrderController(OrderService orderService) {
        this.orderService = orderService;
    }
    
    @PutMapping("/{id}/ship")
    public Orders shipOrder(@PathVariable Long id,
                            @RequestBody Map<String, String> data) {

        Orders order = orderService.getOrderById(id); // 👈 get order

        order.setStatus("SHIPPED");
        order.setTrackingId(data.get("trackingId"));
        order.setCourier(data.get("courier"));

        return orderService.save(order); // 👈 save updated order
    }

    // 👤 USER places order (supports optional body with designImageUrl / customImageUrl)
    @PostMapping("/{productId}/{quantity}")
    public Orders placeOrder(@PathVariable Long productId,
                             @PathVariable int quantity,
                             @RequestBody(required = false) Map<String, String> body,
                             Authentication authentication) {

        String designImageUrl = (body != null) ? body.get("designImageUrl") : null;
        if (designImageUrl == null && body != null) {
            designImageUrl = body.get("customImageUrl");
        }

        return orderService.placeOrder(
                authentication.getName(),
                productId,
                quantity,
                designImageUrl
        );
    }

    // 👤 USER cancels order (restores stock)
    @PutMapping("/{id}/cancel")
    public Orders cancelOrder(@PathVariable Long id, Authentication authentication) {
        return orderService.cancelOrder(id, authentication.getName());
    }

    // 👑 ADMIN update order status
    @PutMapping("/{id}/status")
    public Orders updateStatus(@PathVariable Long id, @RequestBody Map<String, String> data) {
        Orders order = orderService.getOrderById(id);
        String status = data.get("status");
        if (status != null && !status.trim().isEmpty()) {
            order.setStatus(status.toUpperCase());
        }
        if (data.containsKey("trackingId")) order.setTrackingId(data.get("trackingId"));
        if (data.containsKey("courier")) order.setCourier(data.get("courier"));
        return orderService.save(order);
    }

    // 👑 ADMIN view all orders (supports optional pagination)
    @GetMapping("/all")
    public Object getAllOrders(@RequestParam(required = false) Integer page,
                               @RequestParam(required = false) Integer size) {
        if (page != null) {
            int pageSize = (size != null) ? size : 10;
            return orderService.getAllOrdersPaged(PageRequest.of(page, pageSize));
        }
        return orderService.getAllOrders();
    }

    // 👤 USER view their own orders (supports optional pagination)
    @GetMapping
    public Object getUserOrders(@RequestParam(required = false) Integer page,
                                @RequestParam(required = false) Integer size,
                                Authentication authentication) {
        if (page != null) {
            int pageSize = (size != null) ? size : 10;
            return orderService.getUserOrdersPaged(authentication.getName(), PageRequest.of(page, pageSize));
        }
        return orderService.getUserOrders(authentication.getName());
    }
}