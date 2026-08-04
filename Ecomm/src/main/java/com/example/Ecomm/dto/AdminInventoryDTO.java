package com.example.Ecomm.dto;

import com.example.Ecomm.entity.Product;
import com.example.Ecomm.entity.ProductVariant;

public class AdminInventoryDTO {

    private Long variantId;
    private Long productId;
    private String productName;
    private String category;
    private String imageUrl;
    private String size;
    private int stockQuantity;
    private boolean active;
    private String status; // "Out Of Stock", "Low Stock", "In Stock"

    public AdminInventoryDTO() {
    }

    public static AdminInventoryDTO fromEntity(ProductVariant variant) {
        AdminInventoryDTO dto = new AdminInventoryDTO();
        dto.setVariantId(variant.getId());
        dto.setSize(variant.getSize());
        dto.setStockQuantity(variant.getStockQuantity());
        dto.setActive(variant.isActive());

        int qty = variant.getStockQuantity();
        if (qty <= 0) {
            dto.setStatus("Out Of Stock");
        } else if (qty <= 5) {
            dto.setStatus("Low Stock");
        } else {
            dto.setStatus("In Stock");
        }

        Product product = variant.getProduct();
        if (product != null) {
            dto.setProductId(product.getId());
            dto.setProductName(product.getName());
            dto.setCategory(product.getCategory());
            dto.setImageUrl(product.getImageUrl());
        }

        return dto;
    }

    // Getters and Setters

    public Long getVariantId() {
        return variantId;
    }

    public void setVariantId(Long variantId) {
        this.variantId = variantId;
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

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public String getSize() {
        return size;
    }

    public void setSize(String size) {
        this.size = size;
    }

    public int getStockQuantity() {
        return stockQuantity;
    }

    public void setStockQuantity(int stockQuantity) {
        this.stockQuantity = stockQuantity;
    }

    public boolean isActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}
