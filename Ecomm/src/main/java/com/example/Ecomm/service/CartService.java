package com.example.Ecomm.service;

import com.example.Ecomm.entity.Cart;
import com.example.Ecomm.repository.CartRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CartService {

    private final CartRepository cartRepository;

    public CartService(CartRepository cartRepository) {
        this.cartRepository = cartRepository;
    }

    public Cart addToCart(Cart cart) {
        return cartRepository.save(cart);
    }

    public List<Cart> getUserCart(String username) {
        return cartRepository.findByUsername(username);
    }

    public void removeFromCart(Long id) {
        cartRepository.deleteById(id);
    }
}
