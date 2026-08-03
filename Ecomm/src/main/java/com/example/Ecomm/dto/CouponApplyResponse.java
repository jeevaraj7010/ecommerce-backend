package com.example.Ecomm.dto;

public class CouponApplyResponse {

    private boolean success;
    private double discount;
    private double shipping;
    private double subtotal;
    private double finalTotal;
    private double totalSavings;
    private String message;
    private String couponCode;
    private String description;

    public CouponApplyResponse() {
    }

    public CouponApplyResponse(boolean success, String message) {
        this.success = success;
        this.message = message;
    }

    public CouponApplyResponse(boolean success, double discount, double shipping, double subtotal, double finalTotal, double totalSavings, String message, String couponCode, String description) {
        this.success = success;
        this.discount = discount;
        this.shipping = shipping;
        this.subtotal = subtotal;
        this.finalTotal = finalTotal;
        this.totalSavings = totalSavings;
        this.message = message;
        this.couponCode = couponCode;
        this.description = description;
    }

    public boolean isSuccess() {
        return success;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }

    public double getDiscount() {
        return discount;
    }

    public void setDiscount(double discount) {
        this.discount = discount;
    }

    public double getShipping() {
        return shipping;
    }

    public void setShipping(double shipping) {
        this.shipping = shipping;
    }

    public double getSubtotal() {
        return subtotal;
    }

    public void setSubtotal(double subtotal) {
        this.subtotal = subtotal;
    }

    public double getFinalTotal() {
        return finalTotal;
    }

    public void setFinalTotal(double finalTotal) {
        this.finalTotal = finalTotal;
    }

    public double getTotalSavings() {
        return totalSavings;
    }

    public void setTotalSavings(double totalSavings) {
        this.totalSavings = totalSavings;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getCouponCode() {
        return couponCode;
    }

    public void setCouponCode(String couponCode) {
        this.couponCode = couponCode;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}
