package com.example.Ecomm.controller;

import com.example.Ecomm.entity.Product;
import com.example.Ecomm.service.WishlistService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/wishlist")
public class WishlistController {

    private final WishlistService wishlistService;

    public WishlistController(WishlistService wishlistService) {
        this.wishlistService = wishlistService;
    }

    @GetMapping
    public ResponseEntity<Map<String, Object>> getUserWishlist(Authentication authentication) {
        if (authentication == null) return ResponseEntity.status(401).build();
        String username = authentication.getName();
        List<Product> products = wishlistService.getUserWishlistProducts(username);
        long count = wishlistService.getWishlistCount(username);

        Map<String, Object> response = new HashMap<>();
        response.put("products", products);
        response.put("count", count);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/{productId}")
    public ResponseEntity<Map<String, Object>> toggleWishlist(@PathVariable Long productId, Authentication authentication) {
        if (authentication == null) return ResponseEntity.status(401).build();
        String username = authentication.getName();

        boolean isWishlisted = wishlistService.isWishlisted(username, productId);
        if (isWishlisted) {
            wishlistService.removeFromWishlist(username, productId);
        } else {
            wishlistService.addToWishlist(username, productId);
        }

        long count = wishlistService.getWishlistCount(username);
        Map<String, Object> response = new HashMap<>();
        response.put("wishlisted", !isWishlisted);
        response.put("count", count);
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{productId}")
    public ResponseEntity<Map<String, Object>> removeFromWishlist(@PathVariable Long productId, Authentication authentication) {
        if (authentication == null) return ResponseEntity.status(401).build();
        String username = authentication.getName();

        wishlistService.removeFromWishlist(username, productId);
        long count = wishlistService.getWishlistCount(username);

        Map<String, Object> response = new HashMap<>();
        response.put("message", "Removed from wishlist");
        response.put("count", count);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/check/{productId}")
    public ResponseEntity<Map<String, Boolean>> checkWishlisted(@PathVariable Long productId, Authentication authentication) {
        boolean wishlisted = false;
        if (authentication != null) {
            wishlisted = wishlistService.isWishlisted(authentication.getName(), productId);
        }
        Map<String, Boolean> response = new HashMap<>();
        response.put("wishlisted", wishlisted);
        return ResponseEntity.ok(response);
    }
}
