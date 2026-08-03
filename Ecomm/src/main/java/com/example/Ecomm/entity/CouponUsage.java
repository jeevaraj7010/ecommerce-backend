package com.example.Ecomm.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "coupon_usage")
public class CouponUsage {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String username;

    @Column(name = "coupon_code", nullable = false)
    private String couponCode;

    @Column(name = "order_id")
    private Long orderId;

    @Column(name = "used_date")
    private LocalDateTime usedDate = LocalDateTime.now();

    public CouponUsage() {
    }

    public CouponUsage(String username, String couponCode, Long orderId) {
        this.username = username;
        this.couponCode = couponCode != null ? couponCode.toUpperCase() : null;
        this.orderId = orderId;
        this.usedDate = LocalDateTime.now();
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getCouponCode() {
        return couponCode;
    }

    public void setCouponCode(String couponCode) {
        this.couponCode = couponCode != null ? couponCode.toUpperCase() : null;
    }

    public Long getOrderId() {
        return orderId;
    }

    public void setOrderId(Long orderId) {
        this.orderId = orderId;
    }

    public LocalDateTime getUsedDate() {
        return usedDate;
    }

    public void setUsedDate(LocalDateTime usedDate) {
        this.usedDate = usedDate;
    }
}
