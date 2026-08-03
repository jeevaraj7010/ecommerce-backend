package com.example.Ecomm.repository;

import com.example.Ecomm.entity.CouponUsage;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CouponUsageRepository extends JpaRepository<CouponUsage, Long> {

    List<CouponUsage> findByUsernameAndCouponCodeIgnoreCase(String username, String couponCode);

    long countByUsernameAndCouponCodeIgnoreCase(String username, String couponCode);

    List<CouponUsage> findByCouponCodeIgnoreCase(String couponCode);
}
