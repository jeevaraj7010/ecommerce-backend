package com.example.Ecomm.repository;

import com.example.Ecomm.entity.Wishlist;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

public interface WishlistRepository extends JpaRepository<Wishlist, Long> {

    List<Wishlist> findByUsername(String username);

    boolean existsByUsernameAndProductId(String username, Long productId);

    Optional<Wishlist> findByUsernameAndProductId(String username, Long productId);

    @Transactional
    void deleteByUsernameAndProductId(String username, Long productId);

    @Transactional
    void deleteByProductId(Long productId);

    long countByUsername(String username);
}
