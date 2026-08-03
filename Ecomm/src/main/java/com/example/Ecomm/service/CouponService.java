package com.example.Ecomm.service;

import com.example.Ecomm.dto.CouponApplyRequest;
import com.example.Ecomm.dto.CouponApplyResponse;
import com.example.Ecomm.entity.Coupon;
import com.example.Ecomm.entity.CouponUsage;
import com.example.Ecomm.entity.Orders;
import com.example.Ecomm.entity.Product;
import com.example.Ecomm.repository.CouponRepository;
import com.example.Ecomm.repository.CouponUsageRepository;
import com.example.Ecomm.repository.OrderRepository;
import com.example.Ecomm.repository.ProductRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.*;

@Service
public class CouponService {

    private final CouponRepository couponRepository;
    private final CouponUsageRepository couponUsageRepository;
    private final OrderRepository orderRepository;
    private final ProductRepository productRepository;

    public CouponService(CouponRepository couponRepository,
                         CouponUsageRepository couponUsageRepository,
                         OrderRepository orderRepository,
                         ProductRepository productRepository) {
        this.couponRepository = couponRepository;
        this.couponUsageRepository = couponUsageRepository;
        this.orderRepository = orderRepository;
        this.productRepository = productRepository;
    }

    public List<Coupon> getAllCoupons() {
        return couponRepository.findAll();
    }

    public List<Coupon> getAvailableCoupons() {
        List<Coupon> activeCoupons = couponRepository.findByActiveTrue();
        LocalDateTime now = LocalDateTime.now();
        List<Coupon> available = new ArrayList<>();

        for (Coupon c : activeCoupons) {
            boolean startDateOk = (c.getStartDate() == null || !now.isBefore(c.getStartDate()));
            boolean expiryDateOk = (c.getExpiryDate() == null || !now.isAfter(c.getExpiryDate()));
            boolean limitOk = (c.getUsageLimit() <= 0 || c.getUsedCount() < c.getUsageLimit());

            if (startDateOk && expiryDateOk && limitOk) {
                available.add(c);
            }
        }
        return available;
    }

    public Coupon getCouponById(Long id) {
        return couponRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Coupon not found with ID: " + id));
    }

    public Coupon createCoupon(Coupon coupon) {
        if (coupon.getCode() == null || coupon.getCode().trim().isEmpty()) {
            throw new RuntimeException("Coupon code is required");
        }
        String cleanCode = coupon.getCode().trim().toUpperCase();
        if (couponRepository.existsByCodeIgnoreCase(cleanCode)) {
            throw new RuntimeException("Coupon code already exists: " + cleanCode);
        }
        coupon.setCode(cleanCode);
        if (coupon.getStartDate() == null) {
            coupon.setStartDate(LocalDateTime.now());
        }
        return couponRepository.save(coupon);
    }

    public Coupon updateCoupon(Long id, Coupon updated) {
        Coupon existing = getCouponById(id);

        if (updated.getCode() != null && !updated.getCode().trim().isEmpty()) {
            String cleanCode = updated.getCode().trim().toUpperCase();
            if (!cleanCode.equalsIgnoreCase(existing.getCode()) && couponRepository.existsByCodeIgnoreCase(cleanCode)) {
                throw new RuntimeException("Coupon code already exists: " + cleanCode);
            }
            existing.setCode(cleanCode);
        }

        if (updated.getDescription() != null) existing.setDescription(updated.getDescription());
        if (updated.getDiscountType() != null) existing.setDiscountType(updated.getDiscountType());
        existing.setDiscountValue(updated.getDiscountValue());
        existing.setMinimumPurchase(updated.getMinimumPurchase());
        existing.setMaximumDiscount(updated.getMaximumDiscount());
        if (updated.getStartDate() != null) existing.setStartDate(updated.getStartDate());
        if (updated.getExpiryDate() != null) existing.setExpiryDate(updated.getExpiryDate());
        existing.setActive(updated.isActive());
        existing.setUsageLimit(updated.getUsageLimit());
        existing.setPerUserLimit(updated.getPerUserLimit());
        if (updated.getCouponType() != null) existing.setCouponType(updated.getCouponType());
        if (updated.getCategory() != null) existing.setCategory(updated.getCategory());
        if (updated.getBannerImage() != null) existing.setBannerImage(updated.getBannerImage());

        return couponRepository.save(existing);
    }

    public Coupon toggleStatus(Long id) {
        Coupon coupon = getCouponById(id);
        coupon.setActive(!coupon.isActive());
        return couponRepository.save(coupon);
    }

    public void deleteCoupon(Long id) {
        Coupon coupon = getCouponById(id);
        couponRepository.delete(coupon);
    }

    public CouponApplyResponse applyCoupon(CouponApplyRequest request) {
        if (request.getCoupon() == null || request.getCoupon().trim().isEmpty()) {
            return new CouponApplyResponse(false, "Please enter a valid coupon code");
        }

        String code = request.getCoupon().trim().toUpperCase();
        Optional<Coupon> opt = couponRepository.findByCodeIgnoreCase(code);

        if (opt.isEmpty()) {
            return new CouponApplyResponse(false, "Invalid coupon code");
        }

        Coupon coupon = opt.get();
        LocalDateTime now = LocalDateTime.now();

        if (!coupon.isActive()) {
            return new CouponApplyResponse(false, "Coupon is disabled");
        }

        if (coupon.getStartDate() != null && now.isBefore(coupon.getStartDate())) {
            return new CouponApplyResponse(false, "This coupon is not active yet");
        }

        if (coupon.getExpiryDate() != null && now.isAfter(coupon.getExpiryDate())) {
            return new CouponApplyResponse(false, "Coupon expired");
        }

        if (coupon.getUsageLimit() > 0 && coupon.getUsedCount() >= coupon.getUsageLimit()) {
            return new CouponApplyResponse(false, "Coupon usage limit exceeded");
        }

        // Per user usage check
        if (coupon.getPerUserLimit() > 0 && request.getUsername() != null && !request.getUsername().trim().isEmpty()) {
            long userCount = couponUsageRepository.countByUsernameAndCouponCodeIgnoreCase(request.getUsername().trim(), code);
            if (userCount >= coupon.getPerUserLimit()) {
                return new CouponApplyResponse(false, "You have already used this coupon maximum allowed times");
            }
        }

        double cartTotal = request.getCartTotal();
        if (cartTotal < coupon.getMinimumPurchase()) {
            return new CouponApplyResponse(false, "Minimum purchase of ₹" + ((int) coupon.getMinimumPurchase()) + " required for this coupon");
        }

        // Calculate eligible total based on category and couponApplicable flag
        double eligibleTotal = cartTotal;

        if (request.getCartItems() != null && !request.getCartItems().isEmpty()) {
            double categoryEligible = 0;
            String requiredCat = coupon.getCategory();

            for (CouponApplyRequest.CartItemDTO item : request.getCartItems()) {
                boolean itemApplicable = true;
                if (item.getProductId() != null) {
                    Optional<Product> prodOpt = productRepository.findById(item.getProductId());
                    if (prodOpt.isPresent() && !prodOpt.get().isCouponApplicable()) {
                        itemApplicable = false;
                    }
                }

                if (itemApplicable) {
                    if (requiredCat == null || requiredCat.equalsIgnoreCase("ALL") || requiredCat.equalsIgnoreCase("Entire Store")) {
                        categoryEligible += item.getPrice() * item.getQuantity();
                    } else if (item.getCategory() != null && item.getCategory().equalsIgnoreCase(requiredCat)) {
                        categoryEligible += item.getPrice() * item.getQuantity();
                    }
                }
            }

            if (categoryEligible <= 0) {
                return new CouponApplyResponse(false, "Coupon is not applicable for selected items in cart");
            }
            eligibleTotal = categoryEligible;
        }

        // Discount calculation
        double rawDiscount = 0;
        if ("PERCENTAGE".equalsIgnoreCase(coupon.getDiscountType())) {
            rawDiscount = eligibleTotal * (coupon.getDiscountValue() / 100.0);
        } else if ("FLAT".equalsIgnoreCase(coupon.getDiscountType())) {
            rawDiscount = coupon.getDiscountValue();
        }

        // Maximum discount cap
        if (coupon.getMaximumDiscount() > 0 && rawDiscount > coupon.getMaximumDiscount()) {
            rawDiscount = coupon.getMaximumDiscount();
        }

        // Round discount
        double finalDiscount = Math.min(cartTotal, Math.round(rawDiscount * 100.0) / 100.0);
        double subtotalAfterDiscount = cartTotal - finalDiscount;

        // Shipping logic: Free shipping if subtotal after discount >= 1500, else 99
        double shipping = subtotalAfterDiscount >= 1500 ? 0.0 : 99.0;
        double finalTotal = subtotalAfterDiscount + shipping;
        double totalSavings = finalDiscount + (shipping == 0.0 ? 99.0 : 0.0);

        return new CouponApplyResponse(
                true,
                finalDiscount,
                shipping,
                cartTotal,
                finalTotal,
                totalSavings,
                "Coupon Applied Successfully 🎉",
                coupon.getCode(),
                coupon.getDescription()
        );
    }

    @Transactional
    public synchronized void recordCouponUsage(String username, String couponCode, Long orderId) {
        if (couponCode == null || couponCode.trim().isEmpty()) {
            return;
        }
        String cleanCode = couponCode.trim().toUpperCase();
        Optional<Coupon> opt = couponRepository.findByCodeIgnoreCase(cleanCode);
        if (opt.isPresent()) {
            Coupon coupon = opt.get();
            coupon.setUsedCount(coupon.getUsedCount() + 1);
            couponRepository.save(coupon);

            if (username != null && !username.trim().isEmpty()) {
                CouponUsage usage = new CouponUsage(username, cleanCode, orderId);
                couponUsageRepository.save(usage);
            }
        }
    }

    public Map<String, Object> getAnalytics() {
        List<Coupon> allCoupons = couponRepository.findAll();
        List<Orders> allOrders = orderRepository.findAll();

        int totalCoupons = allCoupons.size();
        int activeCoupons = 0;
        int totalUsedCount = 0;
        double totalDiscountGiven = 0;
        double revenueWithCoupons = 0;

        for (Coupon c : allCoupons) {
            if (c.isActive()) activeCoupons++;
            totalUsedCount += c.getUsedCount();
        }

        long ordersWithCouponCount = 0;
        for (Orders order : allOrders) {
            if (order.getCouponCode() != null && !order.getCouponCode().trim().isEmpty()) {
                ordersWithCouponCount++;
                totalDiscountGiven += order.getDiscountAmount();
                revenueWithCoupons += order.getFinalTotal() > 0 ? order.getFinalTotal() : order.getTotalPrice();
            }
        }

        double avgSavings = ordersWithCouponCount > 0 ? (totalDiscountGiven / ordersWithCouponCount) : 0.0;

        Map<String, Object> analytics = new HashMap<>();
        analytics.put("totalCoupons", totalCoupons);
        analytics.put("activeCoupons", activeCoupons);
        analytics.put("totalUsed", totalUsedCount);
        analytics.put("revenueGenerated", Math.round(revenueWithCoupons * 100.0) / 100.0);
        analytics.put("totalDiscountGiven", Math.round(totalDiscountGiven * 100.0) / 100.0);
        analytics.put("avgSavingsPerOrder", Math.round(avgSavings * 100.0) / 100.0);

        return analytics;
    }
}
