package com.example.Ecomm.service;

import com.example.Ecomm.entity.Cart;
import com.example.Ecomm.entity.Product;
import com.example.Ecomm.entity.ProductVariant;
import com.example.Ecomm.repository.CartRepository;
import com.example.Ecomm.repository.ProductRepository;
import com.example.Ecomm.repository.ProductVariantRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class CartService {

    private final CartRepository cartRepository;
    private final ProductRepository productRepository;
    private final ProductVariantRepository variantRepository;

    public CartService(CartRepository cartRepository,
                       ProductRepository productRepository,
                       ProductVariantRepository variantRepository) {
        this.cartRepository = cartRepository;
        this.productRepository = productRepository;
        this.variantRepository = variantRepository;
    }

    public Cart addToCart(Cart cart) {
        Product product = productRepository.findById(cart.getProductId())
                .orElseThrow(() -> new RuntimeException("Product not found"));

        if (Boolean.TRUE.equals(product.getVariantEnabled())) {
            ProductVariant variant = null;
            if (cart.getVariantId() != null) {
                variant = variantRepository.findById(cart.getVariantId()).orElse(null);
            }
            if (variant == null && cart.getSize() != null) {
                variant = variantRepository.findByProductIdAndSizeAndActiveTrue(product.getId(), cart.getSize().trim().toUpperCase()).orElse(null);
            }

            if (variant == null) {
                throw new RuntimeException("Please select a valid size for this product.");
            }

            if (variant.getStockQuantity() <= 0) {
                throw new RuntimeException("Selected size is out of stock.");
            }

            if (cart.getQuantity() > variant.getStockQuantity()) {
                throw new RuntimeException("Only " + variant.getStockQuantity() + " items available for selected size.");
            }

            cart.setVariantId(variant.getId());
            cart.setSize(variant.getSize());
        } else {
            if (product.getQuantity() <= 0) {
                throw new RuntimeException("Product is out of stock.");
            }
            if (cart.getQuantity() > product.getQuantity()) {
                throw new RuntimeException("Only " + product.getQuantity() + " items available.");
            }
        }

        return cartRepository.save(cart);
    }

    public List<Cart> getUserCart(String username) {
        return cartRepository.findByUsername(username);
    }

    public void removeFromCart(Long id) {
        cartRepository.deleteById(id);
    }
}
