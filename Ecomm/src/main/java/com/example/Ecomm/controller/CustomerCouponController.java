package com.example.Ecomm.controller;

import com.example.Ecomm.dto.CouponApplyRequest;
import com.example.Ecomm.dto.CouponApplyResponse;
import com.example.Ecomm.entity.Coupon;
import com.example.Ecomm.service.CouponService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/coupons")
@CrossOrigin(origins = "*")
public class CustomerCouponController {

    private final CouponService couponService;

    public CustomerCouponController(CouponService couponService) {
        this.couponService = couponService;
    }

    // 🏷️ Customer Apply Coupon
    @PostMapping("/apply")
    public ResponseEntity<CouponApplyResponse> applyCoupon(@RequestBody CouponApplyRequest request, Authentication authentication) {
        if (request.getUsername() == null || request.getUsername().trim().isEmpty()) {
            if (authentication != null && authentication.getName() != null) {
                request.setUsername(authentication.getName());
            }
        }
        CouponApplyResponse response = couponService.applyCoupon(request);
        return ResponseEntity.ok(response);
    }

    // 🎁 Available Public Coupons for Cart page
    @GetMapping("/available")
    public ResponseEntity<List<Coupon>> getAvailableCoupons() {
        return ResponseEntity.ok(couponService.getAvailableCoupons());
    }
}
