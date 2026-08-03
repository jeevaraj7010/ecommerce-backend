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

    // 👤 USER places order (supports optional body with designImageUrl / customImageUrl, customText & coupon metrics)
    @PostMapping("/{productId}/{quantity}")
    public Orders placeOrder(@PathVariable Long productId,
                             @PathVariable int quantity,
                             @RequestBody(required = false) Map<String, Object> body,
                             Authentication authentication) {

        String designImageUrl = null;
        String customText = null;
        String couponCode = null;
        double discountAmount = 0.0;
        double shippingCharge = 0.0;
        double totalSavings = 0.0;
        double finalTotal = 0.0;

        String deliveryName = null;
        String deliveryPhone = null;
        String deliveryHouseNo = null;
        String deliveryStreet = null;
        String deliveryLandmark = null;
        String deliveryInstructions = null;
        String deliveryCity = null;
        String deliveryDistrict = null;
        String deliveryState = null;
        String deliveryPincode = null;

        if (body != null) {
            if (body.get("designImageUrl") != null) designImageUrl = String.valueOf(body.get("designImageUrl"));
            if (designImageUrl == null && body.get("customImageUrl") != null) designImageUrl = String.valueOf(body.get("customImageUrl"));
            if (body.get("customText") != null) customText = String.valueOf(body.get("customText"));
            if (body.get("couponCode") != null) couponCode = String.valueOf(body.get("couponCode"));
            
            if (body.get("discountAmount") != null) discountAmount = Double.parseDouble(String.valueOf(body.get("discountAmount")));
            if (body.get("shippingCharge") != null) shippingCharge = Double.parseDouble(String.valueOf(body.get("shippingCharge")));
            if (body.get("totalSavings") != null) totalSavings = Double.parseDouble(String.valueOf(body.get("totalSavings")));
            if (body.get("finalTotal") != null) finalTotal = Double.parseDouble(String.valueOf(body.get("finalTotal")));

            if (body.get("deliveryName") != null) deliveryName = String.valueOf(body.get("deliveryName"));
            if (body.get("deliveryPhone") != null) deliveryPhone = String.valueOf(body.get("deliveryPhone"));
            if (body.get("deliveryHouseNo") != null) deliveryHouseNo = String.valueOf(body.get("deliveryHouseNo"));
            if (body.get("deliveryStreet") != null) deliveryStreet = String.valueOf(body.get("deliveryStreet"));
            if (body.get("deliveryLandmark") != null) deliveryLandmark = String.valueOf(body.get("deliveryLandmark"));
            if (body.get("deliveryInstructions") != null) deliveryInstructions = String.valueOf(body.get("deliveryInstructions"));
            if (body.get("deliveryCity") != null) deliveryCity = String.valueOf(body.get("deliveryCity"));
            if (body.get("deliveryDistrict") != null) deliveryDistrict = String.valueOf(body.get("deliveryDistrict"));
            if (body.get("deliveryState") != null) deliveryState = String.valueOf(body.get("deliveryState"));
            if (body.get("deliveryPincode") != null) deliveryPincode = String.valueOf(body.get("deliveryPincode"));
        }

        return orderService.placeOrder(
                authentication.getName(),
                productId,
                quantity,
                designImageUrl,
                customText,
                couponCode,
                discountAmount,
                shippingCharge,
                totalSavings,
                finalTotal,
                deliveryName,
                deliveryPhone,
                deliveryHouseNo,
                deliveryStreet,
                deliveryLandmark,
                deliveryInstructions,
                deliveryCity,
                deliveryDistrict,
                deliveryState,
                deliveryPincode
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