package com.example.Ecomm.service;

import com.example.Ecomm.entity.Product;
import com.example.Ecomm.repository.ProductRepository;
import com.example.Ecomm.repository.WishlistRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class ProductService {

    private final ProductRepository productRepository;
    private final WishlistRepository wishlistRepository;

    public ProductService(ProductRepository productRepository, WishlistRepository wishlistRepository) {
        this.productRepository = productRepository;
        this.wishlistRepository = wishlistRepository;
    }

    public Product saveProduct(Product product) {
        return productRepository.save(product);
    }

    public List<Product> getAllProducts() {
        return productRepository.findAllWithDetails();
    }

    public Page<Product> getAllProductsPaged(Pageable pageable) {
        return productRepository.findAll(pageable);
    }

    public List<Product> getByCategory(String category) {
        return productRepository.findByCategoryWithDetails(category);
    }

    public Page<Product> getByCategoryPaged(String category, Pageable pageable) {
        return productRepository.findByCategory(category, pageable);
    }

    public Product getProductById(Long id) {
        return productRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Product not found"));
    }

    public Product updateStock(Long id, int quantity) {
        Product product = getProductById(id);
        product.setQuantity(quantity);
        return productRepository.save(product);
    }

    @Transactional
    public void deleteProduct(Long id) {
        wishlistRepository.deleteByProductId(id);
        productRepository.deleteById(id);
    }
}