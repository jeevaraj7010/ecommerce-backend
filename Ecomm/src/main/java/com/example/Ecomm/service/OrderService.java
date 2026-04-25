package com.example.Ecomm.service;

import com.example.Ecomm.entity.Orders;
import com.example.Ecomm.entity.Product;
import com.example.Ecomm.repository.OrderRepository;
import com.example.Ecomm.repository.ProductRepository;
import org.springframework.stereotype.Service;

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

        return orderRepository.save(order);
    }

    public List<Orders> getAllOrders() {
        return orderRepository.findAll();
    }

    public List<Orders> getUserOrders(String username) {
        return orderRepository.findByUsername(username);
    }
}