package com.example.Ecomm.controller;

import com.example.Ecomm.entity.InventoryAuditLog;
import com.example.Ecomm.entity.Product;
import com.example.Ecomm.entity.ProductImage;
import com.example.Ecomm.entity.ProductVariant;
import com.example.Ecomm.service.AdminInventoryService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/admin")
public class AdminInventoryController {

    private final AdminInventoryService adminInventoryService;

    public AdminInventoryController(AdminInventoryService adminInventoryService) {
        this.adminInventoryService = adminInventoryService;
    }

    // 📊 Admin Inventory Overview
    @GetMapping("/inventory")
    public ResponseEntity<Map<String, Object>> getInventoryOverview() {
        return ResponseEntity.ok(adminInventoryService.getInventoryOverview());
    }

    // ➕ Add Variant
    @PostMapping("/inventory/variant")
    public ResponseEntity<?> addVariant(@RequestBody Map<String, Object> body, Authentication auth) {
        try {
            Long productId = Long.parseLong(String.valueOf(body.get("productId")));
            String size = String.valueOf(body.get("size"));
            int stockQuantity = Integer.parseInt(String.valueOf(body.get("stockQuantity")));
            String adminUsername = (auth != null) ? auth.getName() : "ADMIN";

            ProductVariant variant = adminInventoryService.addVariant(productId, size, stockQuantity, adminUsername);
            return ResponseEntity.ok(variant);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(Map.of("message", e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body(Map.of("message", e.getMessage()));
        }
    }

    // ✏️ Update Stock Quantity
    @PutMapping("/inventory/variant/{id}")
    public ResponseEntity<?> updateVariantStock(@PathVariable Long id,
                                               @RequestBody Map<String, Object> body,
                                               Authentication auth) {
        try {
            int newStock = Integer.parseInt(String.valueOf(body.get("stockQuantity")));
            String reason = body.get("reason") != null ? String.valueOf(body.get("reason")) : "Manual stock update";
            String adminUsername = (auth != null) ? auth.getName() : "ADMIN";

            ProductVariant updated = adminInventoryService.updateVariantStock(id, newStock, reason, adminUsername);
            return ResponseEntity.ok(updated);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(Map.of("message", e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body(Map.of("message", e.getMessage()));
        }
    }

    // ❌ Delete Variant
    @DeleteMapping("/inventory/variant/{id}")
    public ResponseEntity<?> deleteVariant(@PathVariable Long id, Authentication auth) {
        try {
            String adminUsername = (auth != null) ? auth.getName() : "ADMIN";
            String result = adminInventoryService.deleteVariant(id, adminUsername);
            return ResponseEntity.ok(Map.of("message", result));
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body(Map.of("message", e.getMessage()));
        }
    }

    // 🔄 Toggle Variant Mode for Product
    @PutMapping("/inventory/product/{id}/toggle-variant")
    public ResponseEntity<?> toggleVariantMode(@PathVariable Long id, @RequestBody Map<String, Boolean> body) {
        try {
            boolean enabled = body.getOrDefault("variantEnabled", true);
            Product product = adminInventoryService.toggleVariantMode(id, enabled);
            return ResponseEntity.ok(product);
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body(Map.of("message", e.getMessage()));
        }
    }

    // 🖼️ Add Product Image
    @PostMapping("/products/{id}/images")
    public ResponseEntity<?> addProductImage(@PathVariable Long id, @RequestBody Map<String, Object> body) {
        try {
            String imageUrl = String.valueOf(body.get("imageUrl"));
            Integer displayOrder = body.get("displayOrder") != null ? Integer.parseInt(String.valueOf(body.get("displayOrder"))) : null;
            ProductImage image = adminInventoryService.addProductImage(id, imageUrl, displayOrder);
            return ResponseEntity.ok(image);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(Map.of("message", e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body(Map.of("message", e.getMessage()));
        }
    }

    // 🖼️ Update Product Image
    @PutMapping("/products/{id}/images/{imageId}")
    public ResponseEntity<?> updateProductImage(@PathVariable Long id,
                                                @PathVariable Long imageId,
                                                @RequestBody Map<String, Object> body) {
        try {
            String imageUrl = body.get("imageUrl") != null ? String.valueOf(body.get("imageUrl")) : null;
            Integer displayOrder = body.get("displayOrder") != null ? Integer.parseInt(String.valueOf(body.get("displayOrder"))) : null;
            ProductImage image = adminInventoryService.updateProductImage(id, imageId, imageUrl, displayOrder);
            return ResponseEntity.ok(image);
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body(Map.of("message", e.getMessage()));
        }
    }

    // 🖼️ Delete Product Image
    @DeleteMapping("/products/{id}/images/{imageId}")
    public ResponseEntity<?> deleteProductImage(@PathVariable Long id, @PathVariable Long imageId) {
        try {
            String msg = adminInventoryService.deleteProductImage(id, imageId);
            return ResponseEntity.ok(Map.of("message", msg));
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body(Map.of("message", e.getMessage()));
        }
    }

    // 📑 Audit Logs
    @GetMapping("/inventory/audit-logs")
    public ResponseEntity<List<InventoryAuditLog>> getAuditLogs() {
        return ResponseEntity.ok(adminInventoryService.getAuditLogs());
    }
}
