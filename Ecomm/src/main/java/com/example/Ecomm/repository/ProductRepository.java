package com.example.Ecomm.repository;

import com.example.Ecomm.entity.Product;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.*;

public interface ProductRepository extends JpaRepository<Product, Long> {
	List<Product> findByCategory(String category);
}