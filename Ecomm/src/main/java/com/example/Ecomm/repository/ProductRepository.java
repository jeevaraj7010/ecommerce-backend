package com.example.Ecomm.repository;

import com.example.Ecomm.entity.Product;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface ProductRepository extends JpaRepository<Product, Long> {

    @Query("SELECT DISTINCT p FROM Product p LEFT JOIN FETCH p.variants LEFT JOIN FETCH p.images")
    List<Product> findAllWithDetails();

    @Query("SELECT DISTINCT p FROM Product p LEFT JOIN FETCH p.variants LEFT JOIN FETCH p.images WHERE LOWER(p.category) = LOWER(:category)")
    List<Product> findByCategoryWithDetails(@Param("category") String category);

    List<Product> findByCategory(String category);
    Page<Product> findByCategory(String category, Pageable pageable);
    Page<Product> findAll(Pageable pageable);
}