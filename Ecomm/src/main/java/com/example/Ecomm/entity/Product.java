package com.example.Ecomm.entity;

import jakarta.persistence.*;

@Entity
@Table(name = "products")
public class Product {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;

    @Column(length = 2000)
    private String description;

    private double price;

    private int quantity;

    private String category;

    private String imageUrl;

    @Column(nullable = false)
    private Boolean customizable = false;

    @Column(name = "coupon_applicable", nullable = false)
    private Boolean couponApplicable = true;

    public Product() {
    }

    // ===========================
    // ID
    // ===========================

    public Long getId() {
        return id;
    }

    // ===========================
    // Name
    // ===========================

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    // ===========================
    // Description
    // ===========================

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    // ===========================
    // Price
    // ===========================

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    // ===========================
    // Quantity
    // ===========================

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    // ===========================
    // Category
    // ===========================

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    // ===========================
    // Image URL
    // ===========================

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    // ===========================
    // Customizable
    // ===========================

    public Boolean getCustomizable() {
        return customizable;
    }

    public Boolean isCustomizable() {
        return customizable;
    }

    public void setCustomizable(Boolean customizable) {
        this.customizable = customizable;
    }

    // ===========================
    // Coupon Applicable
    // ===========================

    public Boolean getCouponApplicable() {
        return couponApplicable;
    }

    public Boolean isCouponApplicable() {
        return couponApplicable;
    }

    public void setCouponApplicable(Boolean couponApplicable) {
        this.couponApplicable = couponApplicable;
    }
}