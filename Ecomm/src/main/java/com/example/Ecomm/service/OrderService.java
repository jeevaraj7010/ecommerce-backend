package com.example.Ecomm.service;

import com.example.Ecomm.entity.Orders;
import com.example.Ecomm.entity.Product;
import com.example.Ecomm.repository.OrderRepository;
import com.example.Ecomm.repository.ProductRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class OrderService {

    private final OrderRepository orderRepository;
    private final ProductRepository productRepository;
    private final CouponService couponService;

    public OrderService(OrderRepository orderRepository,
                        ProductRepository productRepository,
                        CouponService couponService) {
        this.orderRepository = orderRepository;
        this.productRepository = productRepository;
        this.couponService = couponService;
    }
    
    public Orders getOrderById(Long id) {
        return orderRepository.findById(id).orElseThrow();
    }

    public Orders save(Orders order) {
        return orderRepository.save(order);
    }

    public Orders placeOrder(String username, Long productId, int quantity) {
        return placeOrder(username, productId, quantity, null, null);
    }

    public Orders placeOrder(String username, Long productId, int quantity, String designImageUrl) {
        return placeOrder(username, productId, quantity, designImageUrl, null);
    }

    @Transactional
    public synchronized Orders placeOrder(String username, Long productId, int quantity, String designImageUrl, String customText) {
        return placeOrder(username, productId, quantity, designImageUrl, customText, null, 0.0, 0.0, 0.0, 0.0);
    }

    @Transactional
    public synchronized Orders placeOrder(String username, Long productId, int quantity, String designImageUrl, String customText, String couponCode, double discountAmount, double shippingCharge, double totalSavings, double finalTotal) {
        return placeOrder(username, productId, quantity, designImageUrl, customText, couponCode, discountAmount, shippingCharge, totalSavings, finalTotal, null, null, null, null, null, null, null, null, null, null);
    }

    @Transactional
    public synchronized Orders placeOrder(String username, Long productId, int quantity, String designImageUrl, String customText, String couponCode, double discountAmount, double shippingCharge, double totalSavings, double finalTotal, String deliveryName, String deliveryPhone, String deliveryHouseNo, String deliveryStreet, String deliveryLandmark, String deliveryInstructions, String deliveryCity, String deliveryDistrict, String deliveryState, String deliveryPincode) {

        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new RuntimeException("Product not found"));

        if (product.getQuantity() < quantity) {
            throw new RuntimeException("Not enough stock available");
        }

        // reduce stock
        product.setQuantity(product.getQuantity() - quantity);
        productRepository.save(product);

        Orders order = new Orders();
        order.setUsername(username);
        order.setProductName(product.getName());
        order.setProductId(productId);
        order.setQuantity(quantity);
        order.setTotalPrice(product.getPrice() * quantity);
        if (designImageUrl != null && !designImageUrl.trim().isEmpty()) {
            order.setDesignImageUrl(designImageUrl);
        }
        if (customText != null && !customText.trim().isEmpty()) {
            order.setCustomText(customText);
        }
        if (couponCode != null && !couponCode.trim().isEmpty()) {
            order.setCouponCode(couponCode.trim().toUpperCase());
        }
        order.setDiscountAmount(discountAmount);
        order.setShippingCharge(shippingCharge);
        order.setTotalSavings(totalSavings);
        order.setFinalTotal(finalTotal > 0 ? finalTotal : order.getTotalPrice());

        // Delivery address snapshot
        order.setDeliveryName(deliveryName);
        order.setDeliveryPhone(deliveryPhone);
        order.setDeliveryHouseNo(deliveryHouseNo);
        order.setDeliveryStreet(deliveryStreet);
        order.setDeliveryLandmark(deliveryLandmark);
        order.setDeliveryInstructions(deliveryInstructions);
        order.setDeliveryCity(deliveryCity);
        order.setDeliveryDistrict(deliveryDistrict);
        order.setDeliveryState(deliveryState);
        order.setDeliveryPincode(deliveryPincode);

        Orders savedOrder = orderRepository.save(order);

        if (couponCode != null && !couponCode.trim().isEmpty()) {
            couponService.recordCouponUsage(username, couponCode, savedOrder.getId());
        }

        return savedOrder;
    }


    @Transactional
    public Orders cancelOrder(Long id, String username) {
        Orders order = getOrderById(id);
        if (!order.getUsername().equalsIgnoreCase(username)) {
            throw new RuntimeException("Unauthorized to cancel this order");
        }

        String currentStatus = order.getStatus() != null ? order.getStatus().trim().toUpperCase() : "";
        if (!"PLACED".equals(currentStatus)) {
            throw new RuntimeException("Order cannot be cancelled once it moves beyond PLACED status (Current: " + order.getStatus() + ")");
        }

        order.setStatus("CANCELLED");
        Orders savedOrder = orderRepository.save(order);

        // Restore stock
        productRepository.findById(order.getProductId()).ifPresent(product -> {
            product.setQuantity(product.getQuantity() + order.getQuantity());
            productRepository.save(product);
        });

        return savedOrder;
    }

    public boolean hasDeliveredOrder(String username, Long productId) {
        return orderRepository.existsByUsernameAndProductIdAndStatus(username, productId, "DELIVERED");
    }

    public List<Orders> getAllOrders() {
        return orderRepository.findAll();
    }

    public Page<Orders> getAllOrdersPaged(Pageable pageable) {
        return orderRepository.findAll(pageable);
    }

    public List<Orders> getUserOrders(String username) {
        return orderRepository.findByUsername(username);
    }

    public Page<Orders> getUserOrdersPaged(String username, Pageable pageable) {
        return orderRepository.findByUsername(username, pageable);
    }
}