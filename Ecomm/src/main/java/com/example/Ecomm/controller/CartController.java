package com.example.Ecomm.controller;

import com.example.Ecomm.entity.Cart;
import com.example.Ecomm.service.CartService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/cart")
@CrossOrigin(origins = "http://localhost:3000")
public class CartController {

    private final CartService cartService;

    public CartController(CartService cartService) {
        this.cartService = cartService;
    }

    // Add to cart
    @PostMapping
    public Cart addToCart(@RequestBody Cart cart) {
        return cartService.addToCart(cart);
    }

    // Get user cart
    @GetMapping("/{username}")
    public List<Cart> getCart(@PathVariable String username) {
        return cartService.getUserCart(username);
    }

    // Remove item
    @DeleteMapping("/{id}")
    public String removeFromCart(@PathVariable Long id) {
        cartService.removeFromCart(id);
        return "Item removed from cart";
    }
}