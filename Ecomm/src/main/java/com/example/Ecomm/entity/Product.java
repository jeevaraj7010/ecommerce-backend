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

    @Column(nullable = false, columnDefinition = "boolean default false")
    private Boolean customizable = false;

    @Column(name = "coupon_applicable", nullable = false, columnDefinition = "boolean default true")
    private Boolean couponApplicable = true;

    @Column(name = "variant_enabled", nullable = false, columnDefinition = "boolean default false")
    private Boolean variantEnabled = false;

    @OneToMany(mappedBy = "product", cascade = CascadeType.ALL, orphanRemoval = true)
    private java.util.List<ProductVariant> variants = new java.util.ArrayList<>();

    @OneToMany(mappedBy = "product", cascade = CascadeType.ALL, orphanRemoval = true)
    @OrderBy("displayOrder ASC")
    private java.util.List<ProductImage> images = new java.util.ArrayList<>();

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

    // ===========================
    // Variant Enabled
    // ===========================

    public Boolean getVariantEnabled() {
        return variantEnabled;
    }

    public Boolean isVariantEnabled() {
        return variantEnabled;
    }

    public void setVariantEnabled(Boolean variantEnabled) {
        this.variantEnabled = variantEnabled;
    }

    // ===========================
    // Variants & Images
    // ===========================

    public java.util.List<ProductVariant> getVariants() {
        return variants;
    }

    public void setVariants(java.util.List<ProductVariant> variants) {
        this.variants = variants;
    }

    public java.util.List<ProductImage> getImages() {
        return images;
    }

    public void setImages(java.util.List<ProductImage> images) {
        this.images = images;
    }
}