package com.example.Ecomm.repository;

import com.example.Ecomm.entity.Orders;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface OrderRepository extends JpaRepository<Orders, Long> {

    List<Orders> findByUsername(String username);
}