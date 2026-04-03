package com.example.Ecomm.entity;

import jakarta.persistence.*;

@Entity
public class Cart {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long productId;

    private int quantity;

    private String username;

    public Cart() {}

    public Cart(Long productId, int quantity, String username) {
        this.productId = productId;
        this.quantity = quantity;
        this.username = username;
    }

    public Long getId() { return id; }

    public Long getProductId() { return productId; }

    public void setProductId(Long productId) {
        this.productId = productId;
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