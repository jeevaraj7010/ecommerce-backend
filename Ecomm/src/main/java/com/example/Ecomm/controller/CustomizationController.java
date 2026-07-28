package com.example.Ecomm.controller;

import com.example.Ecomm.entity.Customization;
import com.example.Ecomm.entity.Product;
import com.example.Ecomm.entity.User;
import com.example.Ecomm.repository.ProductRepository;
import com.example.Ecomm.repository.UserRepository;
import com.example.Ecomm.service.CloudinaryService;
import com.example.Ecomm.service.CustomizationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@CrossOrigin
public class CustomizationController {

    @Autowired
    private CloudinaryService cloudinaryService;

    @Autowired
    private CustomizationService customizationService;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ProductRepository productRepository;

    // 👤 Customer: Upload customization design
    @PostMapping("/api/customization/upload")
    public ResponseEntity<?> uploadCustomization(
            @RequestParam("productId") Long productId,
            @RequestParam("image") MultipartFile image,
            @RequestParam(value = "customText", required = false) String customText,
            Authentication authentication) {

        if (authentication == null || !authentication.isAuthenticated()) {
            return ResponseEntity.status(401).body(Map.of("message", "User must be authenticated"));
        }

        String username = authentication.getName();
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found: " + username));

        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new RuntimeException("Product not found with id: " + productId));

        if (!product.getCustomizable()) {
            return ResponseEntity.badRequest().body(Map.of("message", "This product is not customizable"));
        }

        // Upload image to Cloudinary
        String imageUrl = cloudinaryService.uploadImage(image);

        // Save customization details
        Customization customization = customizationService.saveCustomization(user, product, imageUrl, customText);

        Map<String, Object> response = new HashMap<>();
        response.put("message", "Customization uploaded successfully");
        response.put("imageUrl", imageUrl);
        response.put("productId", productId);
        response.put("customText", customText != null ? customText : "");
        response.put("customizationId", customization.getId());

        return ResponseEntity.ok(response);
    }

    // 👑 Admin: View all customization requests
    @GetMapping("/api/admin/customizations")
    public ResponseEntity<List<Customization>> getAllCustomizations() {
        List<Customization> list = customizationService.getAllCustomizations();
        return ResponseEntity.ok(list);
    }

    // 👑 Admin: Update customization status (APPROVE / REJECT)
    @PutMapping("/api/admin/customizations/{id}/status")
    public ResponseEntity<?> updateCustomizationStatus(
            @PathVariable Long id,
            @RequestBody Map<String, String> body) {

        String status = body.get("status");
        if (status == null || status.trim().isEmpty()) {
            return ResponseEntity.badRequest().body(Map.of("message", "Status is required"));
        }

        Customization updated = customizationService.updateStatus(id, status);
        return ResponseEntity.ok(updated);
    }
}
