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

    public OrderService(OrderRepository orderRepository,
                        ProductRepository productRepository) {
        this.orderRepository = orderRepository;
        this.productRepository = productRepository;
    }
    
    public Orders getOrderById(Long id) {
        return orderRepository.findById(id).orElseThrow();
    }

    public Orders save(Orders order) {
        return orderRepository.save(order);
    }

    public Orders placeOrder(String username, Long productId, int quantity) {
        return placeOrder(username, productId, quantity, null);
    }

    @Transactional
    public synchronized Orders placeOrder(String username, Long productId, int quantity, String designImageUrl) {

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

        return orderRepository.save(order);
    }

    @Transactional
    public Orders cancelOrder(Long id, String username) {
        Orders order = getOrderById(id);
        if (!order.getUsername().equalsIgnoreCase(username)) {
            throw new RuntimeException("Unauthorized to cancel this order");
        }

        if ("DELIVERED".equalsIgnoreCase(order.getStatus()) || "CANCELLED".equalsIgnoreCase(order.getStatus())) {
            throw new RuntimeException("Order cannot be cancelled in status: " + order.getStatus());
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