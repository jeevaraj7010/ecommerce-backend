package com.example.Ecomm.controller;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import com.example.Ecomm.entity.Orders;
import com.example.Ecomm.entity.User;
import com.example.Ecomm.repository.OrderRepository;
import com.example.Ecomm.repository.ProductRepository;
import com.example.Ecomm.repository.UserRepository;

@RestController
@RequestMapping("/api/admin")
public class AdminController {

    @Autowired
    private UserRepository userRepo;

    @Autowired
    private ProductRepository productRepo;

    @Autowired
    private OrderRepository orderRepo;

    // 📊 Dashboard overview
    @GetMapping("/overview")
    public Map<String, Object> getOverview() {
        Map<String, Object> data = new HashMap<>();

        data.put("users", userRepo.count());
        data.put("products", productRepo.count());
        data.put("orders", orderRepo.count());

        // 💰 Total Revenue
        double totalRevenue = orderRepo.findAll()
                .stream()
                .mapToDouble(Orders::getTotalPrice)
                .sum();

        data.put("revenue", totalRevenue);

        return data;
    }

    // 👤 All users
    @GetMapping("/users")
    public List<User> getUsers() {
        return userRepo.findAll();
    }

    // 🛒 All orders
    @GetMapping("/orders")
    public List<Orders> getOrders() {
        return orderRepo.findAll();
    }

    // 🎨 Custom hoodie orders (with image)
    @GetMapping("/custom-orders")
    public List<Orders> getCustomOrders() {
        return orderRepo.findAll()
                .stream()
                .filter(order -> order.getDesignImageUrl() != null)
                .toList();
    }

    // 🚚 Update order status
    @PutMapping("/orders/{id}/status")
    public Orders updateOrderStatus(@PathVariable Long id,
                                    @RequestParam String status) {

        Orders order = orderRepo.findById(id)
                .orElseThrow(() -> new RuntimeException("Order not found"));

        order.setStatus(status);
        return orderRepo.save(order);
    }

    // ❌ Delete user (optional)
    @DeleteMapping("/users/{id}")
    public String deleteUser(@PathVariable Long id) {
        userRepo.deleteById(id);
        return "User deleted successfully";
    }
}