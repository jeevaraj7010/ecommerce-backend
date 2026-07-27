package com.example.Ecomm.service;

import com.example.Ecomm.entity.Product;
import com.example.Ecomm.entity.Wishlist;
import com.example.Ecomm.repository.ProductRepository;
import com.example.Ecomm.repository.WishlistRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Service
public class WishlistService {

    private final WishlistRepository wishlistRepository;
    private final ProductRepository productRepository;

    public WishlistService(WishlistRepository wishlistRepository, ProductRepository productRepository) {
        this.wishlistRepository = wishlistRepository;
        this.productRepository = productRepository;
    }

    public List<Product> getUserWishlistProducts(String username) {
        List<Wishlist> wishlists = wishlistRepository.findByUsername(username);
        return wishlists.stream()
                .map(w -> productRepository.findById(w.getProductId()).orElse(null))
                .filter(Objects::nonNull)
                .collect(Collectors.toList());
    }

    public Wishlist addToWishlist(String username, Long productId) {
        if (!wishlistRepository.existsByUsernameAndProductId(username, productId)) {
            Wishlist wishlist = new Wishlist(username, productId);
            return wishlistRepository.save(wishlist);
        }
        return wishlistRepository.findByUsernameAndProductId(username, productId).orElse(null);
    }

    @Transactional
    public void removeFromWishlist(String username, Long productId) {
        wishlistRepository.deleteByUsernameAndProductId(username, productId);
    }

    public boolean isWishlisted(String username, Long productId) {
        return wishlistRepository.existsByUsernameAndProductId(username, productId);
    }

    public long getWishlistCount(String username) {
        return wishlistRepository.countByUsername(username);
    }
}
