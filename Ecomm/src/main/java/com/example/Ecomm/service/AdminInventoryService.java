package com.example.Ecomm.service;

import com.example.Ecomm.dto.AdminInventoryDTO;
import com.example.Ecomm.entity.*;
import com.example.Ecomm.repository.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class AdminInventoryService {

    private final ProductRepository productRepository;
    private final ProductVariantRepository variantRepository;
    private final ProductImageRepository imageRepository;
    private final InventoryAuditLogRepository auditLogRepository;

    public AdminInventoryService(ProductRepository productRepository,
                                 ProductVariantRepository variantRepository,
                                 ProductImageRepository imageRepository,
                                 InventoryAuditLogRepository auditLogRepository) {
        this.productRepository = productRepository;
        this.variantRepository = variantRepository;
        this.imageRepository = imageRepository;
        this.auditLogRepository = auditLogRepository;
    }

    // 📊 Summary metrics + variant inventory list
    public Map<String, Object> getInventoryOverview() {
        List<Product> products = productRepository.findAll();
        List<ProductVariant> allVariants = variantRepository.findAll();
        List<ProductVariant> activeVariants = allVariants.stream().filter(ProductVariant::isActive).collect(Collectors.toList());

        long totalProducts = products.size();
        long variantEnabledProducts = products.stream().filter(p -> Boolean.TRUE.equals(p.getVariantEnabled())).count();
        long totalVariants = activeVariants.size();
        long totalInventoryUnits = products.stream()
                .mapToLong(p -> Boolean.TRUE.equals(p.getVariantEnabled())
                        ? p.getVariants().stream().filter(ProductVariant::isActive).mapToInt(ProductVariant::getStockQuantity).sum()
                        : Math.max(0, p.getQuantity()))
                .sum();

        long lowStockVariants = activeVariants.stream().filter(v -> v.getStockQuantity() > 0 && v.getStockQuantity() <= 5).count();
        long outOfStockVariants = activeVariants.stream().filter(v -> v.getStockQuantity() <= 0).count();

        List<AdminInventoryDTO> inventoryItems = activeVariants.stream()
                .map(AdminInventoryDTO::fromEntity)
                .collect(Collectors.toList());

        Map<String, Object> response = new HashMap<>();
        response.put("totalProducts", totalProducts);
        response.put("variantEnabledProducts", variantEnabledProducts);
        response.put("totalVariants", totalVariants);
        response.put("totalInventoryUnits", totalInventoryUnits);
        response.put("lowStockVariants", lowStockVariants);
        response.put("outOfStockVariants", outOfStockVariants);
        response.put("inventory", inventoryItems);

        return response;
    }

    // ➕ Add Variant
    @Transactional
    public ProductVariant addVariant(Long productId, String size, int stockQuantity, String adminUsername) {
        if (stockQuantity < 0) {
            throw new IllegalArgumentException("Stock cannot be negative.");
        }
        if (size == null || size.trim().isEmpty()) {
            throw new IllegalArgumentException("Size is required.");
        }

        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new RuntimeException("Product not found"));

        String formattedSize = size.trim().toUpperCase();

        // Check if active variant with same size already exists
        Optional<ProductVariant> existing = variantRepository.findByProductIdAndSizeAndActiveTrue(productId, formattedSize);
        if (existing.isPresent()) {
            throw new IllegalArgumentException("Variant with size " + formattedSize + " already exists for this product.");
        }

        ProductVariant variant = new ProductVariant(product, formattedSize, stockQuantity);
        product.setVariantEnabled(true);
        ProductVariant saved = variantRepository.save(variant);

        syncProductTotalStock(product);
        productRepository.save(product);

        // Audit Log
        recordAuditLog(adminUsername, product.getId(), product.getName(), formattedSize, 0, stockQuantity, "Added new size variant");

        return saved;
    }

    // ✏️ Update Stock Quantity
    @Transactional
    public ProductVariant updateVariantStock(Long variantId, int newStock, String reason, String adminUsername) {
        if (newStock < 0) {
            throw new IllegalArgumentException("Stock cannot be negative.");
        }

        ProductVariant variant = variantRepository.findById(variantId)
                .orElseThrow(() -> new RuntimeException("Variant not found"));

        int oldStock = variant.getStockQuantity();
        variant.setStockQuantity(newStock);
        ProductVariant saved = variantRepository.save(variant);

        Product product = variant.getProduct();
        if (product != null) {
            syncProductTotalStock(product);
            productRepository.save(product);
            recordAuditLog(adminUsername, product.getId(), product.getName(), variant.getSize(), oldStock, newStock, reason != null ? reason : "Stock updated by admin");
        }

        return saved;
    }

    // ❌ Delete Variant (Soft delete if used, else hard delete)
    @Transactional
    public String deleteVariant(Long variantId, String adminUsername) {
        ProductVariant variant = variantRepository.findById(variantId)
                .orElseThrow(() -> new RuntimeException("Variant not found"));

        Product product = variant.getProduct();
        int oldStock = variant.getStockQuantity();
        variant.setActive(false);
        variant.setStockQuantity(0);
        variantRepository.save(variant);

        if (product != null) {
            syncProductTotalStock(product);
            productRepository.save(product);
            recordAuditLog(adminUsername, product.getId(), product.getName(), variant.getSize(), oldStock, 0, "Variant deactivated/deleted");
        }

        return "Variant deleted successfully";
    }

    // 🔄 Toggle Variant Enabled mode
    @Transactional
    public Product toggleVariantMode(Long productId, boolean enabled) {
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new RuntimeException("Product not found"));

        product.setVariantEnabled(enabled);
        if (enabled) {
            syncProductTotalStock(product);
        }
        return productRepository.save(product);
    }

    // 🖼️ Image Management APIs
    @Transactional
    public ProductImage addProductImage(Long productId, String imageUrl, Integer displayOrder) {
        if (imageUrl == null || imageUrl.trim().isEmpty()) {
            throw new IllegalArgumentException("Image URL is required.");
        }

        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new RuntimeException("Product not found"));

        List<ProductImage> existingImages = imageRepository.findByProductIdOrderByDisplayOrderAsc(productId);
        if (existingImages.size() >= 8) {
            throw new IllegalArgumentException("Maximum 8 gallery images allowed per product.");
        }

        int order = (displayOrder != null) ? displayOrder : existingImages.size() + 1;
        ProductImage image = new ProductImage(product, imageUrl.trim(), order);
        ProductImage saved = imageRepository.save(image);

        // Update product's main imageUrl if not set or if it's the first image
        if (product.getImageUrl() == null || product.getImageUrl().trim().isEmpty() || existingImages.isEmpty()) {
            product.setImageUrl(imageUrl.trim());
            productRepository.save(product);
        }

        return saved;
    }

    @Transactional
    public ProductImage updateProductImage(Long productId, Long imageId, String imageUrl, Integer displayOrder) {
        ProductImage image = imageRepository.findById(imageId)
                .orElseThrow(() -> new RuntimeException("Image not found"));

        if (imageUrl != null && !imageUrl.trim().isEmpty()) {
            image.setImageUrl(imageUrl.trim());
        }
        if (displayOrder != null) {
            image.setDisplayOrder(displayOrder);
        }

        return imageRepository.save(image);
    }

    @Transactional
    public String deleteProductImage(Long productId, Long imageId) {
        ProductImage image = imageRepository.findById(imageId)
                .orElseThrow(() -> new RuntimeException("Image not found"));

        Product product = image.getProduct();
        imageRepository.delete(image);

        // Sync main imageUrl if deleted image was the primary image
        List<ProductImage> remaining = imageRepository.findByProductIdOrderByDisplayOrderAsc(productId);
        if (remaining.isEmpty()) {
            // Keep existing product.getImageUrl() as fallback
        } else {
            product.setImageUrl(remaining.get(0).getImageUrl());
            productRepository.save(product);
        }

        return "Image deleted successfully";
    }

    // 📑 Audit Logs
    public List<InventoryAuditLog> getAuditLogs() {
        return auditLogRepository.findTop50ByOrderByTimestampDesc();
    }

    // Sync helper
    private void syncProductTotalStock(Product product) {
        if (Boolean.TRUE.equals(product.getVariantEnabled()) && product.getVariants() != null) {
            int total = product.getVariants().stream()
                    .filter(ProductVariant::isActive)
                    .mapToInt(ProductVariant::getStockQuantity)
                    .sum();
            product.setQuantity(total);
        }
    }

    private void recordAuditLog(String adminUsername, Long productId, String productName, String size, int oldStock, int newStock, String reason) {
        try {
            InventoryAuditLog log = new InventoryAuditLog(
                    adminUsername != null ? adminUsername : "ADMIN",
                    productId,
                    productName,
                    size,
                    oldStock,
                    newStock,
                    reason
            );
            auditLogRepository.save(log);
        } catch (Exception e) {
            System.err.println("Failed to record audit log: " + e.getMessage());
        }
    }
}
