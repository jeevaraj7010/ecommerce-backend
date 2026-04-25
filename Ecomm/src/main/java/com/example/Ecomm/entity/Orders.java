package com.example.Ecomm.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "orders")
public class Orders {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String username;

    private Long productId;

    private String productName;

    private int quantity;

    private double totalPrice;

    private LocalDateTime orderDate;

    // 🎨 Custom hoodie image
    @Column(name = "design_image_url")
    private String designImageUrl;

    // 🚚 Order status
    @Column(name = "status")
    private String status;

    // 🚚 NEW: Tracking ID
    @Column(name = "tracking_id")
    private String trackingId;

    // 🚚 NEW: Courier name
    @Column(name = "courier")
    private String courier;

    // ✅ Constructor
    public Orders() {
        this.orderDate = LocalDateTime.now();
        this.status = "PLACED"; // better than PENDING
        this.trackingId = null;
        this.courier = null;
    }

    // ================= GETTERS & SETTERS =================

    public Long getId() {
        return id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public Long getProductId() {
        return productId;
    }

    public void setProductId(Long productId) {
        this.productId = productId;
    }

    public String getProductName() {
        return productName;
    }

    public void setProductName(String productName) {
        this.productName = productName;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public double getTotalPrice() {
        return totalPrice;
    }

    public void setTotalPrice(double totalPrice) {
        this.totalPrice = totalPrice;
    }

    public LocalDateTime getOrderDate() {
        return orderDate;
    }

    public String getDesignImageUrl() {
        return designImageUrl;
    }

    public void setDesignImageUrl(String designImageUrl) {
        this.designImageUrl = designImageUrl;
    }

    // 🚚 Status
    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    // 🚚 Tracking ID
    public String getTrackingId() {
        return trackingId;
    }

    public void setTrackingId(String trackingId) {
        this.trackingId = trackingId;
    }

    // 🚚 Courier
    public String getCourier() {
        return courier;
    }

    public void setCourier(String courier) {
        this.courier = courier;
    }
}