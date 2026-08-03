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

    // 🎨 Custom text
    @Column(name = "custom_text")
    private String customText;


    // 🚚 Order status
    @Column(name = "status")
    private String status;

    // 🚚 NEW: Tracking ID
    @Column(name = "tracking_id")
    private String trackingId;

    // 🚚 NEW: Courier name
    @Column(name = "courier")
    private String courier;

    // 🎟️ Coupon & Pricing Summary
    @Column(name = "coupon_code")
    private String couponCode;

    @Column(name = "discount_amount")
    private double discountAmount;

    @Column(name = "shipping_charge")
    private double shippingCharge;

    @Column(name = "total_savings")
    private double totalSavings;

    @Column(name = "final_total")
    private double finalTotal;

    // 🏠 Delivery Address Snapshot
    @Column(name = "delivery_name")
    private String deliveryName;

    @Column(name = "delivery_phone")
    private String deliveryPhone;

    @Column(name = "delivery_house_no")
    private String deliveryHouseNo;

    @Column(name = "delivery_street")
    private String deliveryStreet;

    @Column(name = "delivery_landmark")
    private String deliveryLandmark;

    @Column(name = "delivery_instructions")
    private String deliveryInstructions;

    @Column(name = "delivery_city")
    private String deliveryCity;

    @Column(name = "delivery_district")
    private String deliveryDistrict;

    @Column(name = "delivery_state")
    private String deliveryState;

    @Column(name = "delivery_pincode")
    private String deliveryPincode;

    // 🚀 Shiprocket & Advanced Tracking Structure
    @Column(name = "tracking_url")
    private String trackingUrl;

    @Column(name = "shipment_status")
    private String shipmentStatus;

    @Column(name = "awb_number")
    private String awbNumber;

    @Column(name = "estimated_delivery_date")
    private String estimatedDeliveryDate;

    @Column(name = "shipped_date")
    private LocalDateTime shippedDate;

    @Column(name = "delivered_date")
    private LocalDateTime deliveredDate;

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

    public String getCustomText() {
        return customText;
    }

    public void setCustomText(String customText) {
        this.customText = customText;
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

    // 🎟️ Coupon Getters & Setters
    public String getCouponCode() {
        return couponCode;
    }

    public void setCouponCode(String couponCode) {
        this.couponCode = couponCode;
    }

    public double getDiscountAmount() {
        return discountAmount;
    }

    public void setDiscountAmount(double discountAmount) {
        this.discountAmount = discountAmount;
    }

    public double getShippingCharge() {
        return shippingCharge;
    }

    public void setShippingCharge(double shippingCharge) {
        this.shippingCharge = shippingCharge;
    }

    public double getTotalSavings() {
        return totalSavings;
    }

    public void setTotalSavings(double totalSavings) {
        this.totalSavings = totalSavings;
    }

    public double getFinalTotal() {
        return finalTotal;
    }

    public void setFinalTotal(double finalTotal) {
        this.finalTotal = finalTotal;
    }

    // 🏠 Delivery Address Snapshot Getters & Setters
    public String getDeliveryName() { return deliveryName; }
    public void setDeliveryName(String deliveryName) { this.deliveryName = deliveryName; }

    public String getDeliveryPhone() { return deliveryPhone; }
    public void setDeliveryPhone(String deliveryPhone) { this.deliveryPhone = deliveryPhone; }

    public String getDeliveryHouseNo() { return deliveryHouseNo; }
    public void setDeliveryHouseNo(String deliveryHouseNo) { this.deliveryHouseNo = deliveryHouseNo; }

    public String getDeliveryStreet() { return deliveryStreet; }
    public void setDeliveryStreet(String deliveryStreet) { this.deliveryStreet = deliveryStreet; }

    public String getDeliveryLandmark() { return deliveryLandmark; }
    public void setDeliveryLandmark(String deliveryLandmark) { this.deliveryLandmark = deliveryLandmark; }

    public String getDeliveryInstructions() { return deliveryInstructions; }
    public void setDeliveryInstructions(String deliveryInstructions) { this.deliveryInstructions = deliveryInstructions; }

    public String getDeliveryCity() { return deliveryCity; }
    public void setDeliveryCity(String deliveryCity) { this.deliveryCity = deliveryCity; }

    public String getDeliveryDistrict() { return deliveryDistrict; }
    public void setDeliveryDistrict(String deliveryDistrict) { this.deliveryDistrict = deliveryDistrict; }

    public String getDeliveryState() { return deliveryState; }
    public void setDeliveryState(String deliveryState) { this.deliveryState = deliveryState; }

    public String getDeliveryPincode() { return deliveryPincode; }
    public void setDeliveryPincode(String deliveryPincode) { this.deliveryPincode = deliveryPincode; }

    // 🚀 Shiprocket & Tracking Getters & Setters
    public String getTrackingUrl() { return trackingUrl; }
    public void setTrackingUrl(String trackingUrl) { this.trackingUrl = trackingUrl; }

    public String getShipmentStatus() { return shipmentStatus; }
    public void setShipmentStatus(String shipmentStatus) { this.shipmentStatus = shipmentStatus; }

    public String getAwbNumber() { return awbNumber; }
    public void setAwbNumber(String awbNumber) { this.awbNumber = awbNumber; }

    public String getEstimatedDeliveryDate() { return estimatedDeliveryDate; }
    public void setEstimatedDeliveryDate(String estimatedDeliveryDate) { this.estimatedDeliveryDate = estimatedDeliveryDate; }

    public LocalDateTime getShippedDate() { return shippedDate; }
    public void setShippedDate(LocalDateTime shippedDate) { this.shippedDate = shippedDate; }

    public LocalDateTime getDeliveredDate() { return deliveredDate; }
    public void setDeliveredDate(LocalDateTime deliveredDate) { this.deliveredDate = deliveredDate; }
}