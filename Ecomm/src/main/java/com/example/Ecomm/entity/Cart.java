package com.example.Ecomm.entity;

import jakarta.persistence.*;

@Entity
public class Cart {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long productId;

    private Long variantId;

    private String size;

    private int quantity;

    private String username;

    public Cart() {}

    public Cart(Long productId, int quantity, String username) {
        this.productId = productId;
        this.quantity = quantity;
        this.username = username;
    }

    public Cart(Long productId, Long variantId, String size, int quantity, String username) {
        this.productId = productId;
        this.variantId = variantId;
        this.size = size;
        this.quantity = quantity;
        this.username = username;
    }

    public Long getId() { return id; }

    public Long getProductId() { return productId; }

    public void setProductId(Long productId) {
        this.productId = productId;
    }

    public Long getVariantId() { return variantId; }

    public void setVariantId(Long variantId) {
        this.variantId = variantId;
    }

    public String getSize() { return size; }

    public void setSize(String size) {
        this.size = size;
    }

    public int getQuantity() { return quantity; }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public String getUsername() { return username; }

    public void setUsername(String username) {
        this.username = username;
    }
}