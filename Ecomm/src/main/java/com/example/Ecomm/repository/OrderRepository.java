package com.example.Ecomm.repository;

import com.example.Ecomm.entity.Orders;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface OrderRepository extends JpaRepository<Orders, Long> {

    List<Orders> findByUsername(String username);

    Page<Orders> findByUsername(String username, Pageable pageable);

    Page<Orders> findAll(Pageable pageable);

    boolean existsByUsernameAndProductIdAndStatus(String username, Long productId, String status);

    List<Orders> findByCouponCodeIgnoreCase(String couponCode);
}