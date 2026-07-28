package com.example.Ecomm.repository;

import com.example.Ecomm.entity.Customization;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CustomizationRepository extends JpaRepository<Customization, Long> {
    List<Customization> findByUserId(Long userId);
    List<Customization> findByProductId(Long productId);
}
