package com.example.Ecomm.dto;

import com.example.Ecomm.entity.Product;
import com.example.Ecomm.entity.ProductImage;
import com.example.Ecomm.entity.ProductVariant;

import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

public class ProductCustomerDTO {

    private Long id;
    private String name;
    private String description;
    private double price;
    private int quantity;
    private String category;
    private String imageUrl;
    private Boolean customizable;
    private Boolean couponApplicable;
    private Boolean variantEnabled;
    private List<String> images;
    private List<VariantCustomerDTO> variants;

    private static final List<String> SIZE_ORDER = Arrays.asList("XS", "S", "M", "L", "XL", "XXL", "3XL");

    public ProductCustomerDTO() {
    }

    public static ProductCustomerDTO fromEntity(Product product) {
        ProductCustomerDTO dto = new ProductCustomerDTO();
        dto.setId(product.getId());
        dto.setName(product.getName());
        dto.setDescription(product.getDescription());
        dto.setPrice(product.getPrice());
        dto.setQuantity(product.getQuantity());
        dto.setCategory(product.getCategory());
        dto.setImageUrl(product.getImageUrl());
        dto.setCustomizable(product.getCustomizable());
        dto.setCouponApplicable(product.getCouponApplicable());
        dto.setVariantEnabled(product.getVariantEnabled() != null ? product.getVariantEnabled() : false);

        if (product.getImages() != null && !product.getImages().isEmpty()) {
            dto.setImages(product.getImages().stream()
                    .map(ProductImage::getImageUrl)
                    .collect(Collectors.toList()));
        } else if (product.getImageUrl() != null && !product.getImageUrl().trim().isEmpty()) {
            dto.setImages(List.of(product.getImageUrl()));
        } else {
            dto.setImages(List.of());
        }

        if (product.getVariants() != null && !product.getVariants().isEmpty()) {
            List<VariantCustomerDTO> variantDTOs = product.getVariants().stream()
                    .filter(ProductVariant::isActive)
                    .map(v -> new VariantCustomerDTO(v.getId(), v.getSize(), v.getStockQuantity() > 0))
                    .sorted(Comparator.comparingInt(v -> {
                        int index = SIZE_ORDER.indexOf(v.getSize().toUpperCase());
                        return index != -1 ? index : 999;
                    }))
                    .collect(Collectors.toList());
            dto.setVariants(variantDTOs);
        } else {
            dto.setVariants(List.of());
        }

        return dto;
    }

    // Getters and Setters

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public Boolean getCustomizable() {
        return customizable;
    }

    public void setCustomizable(Boolean customizable) {
        this.customizable = customizable;
    }

    public Boolean getCouponApplicable() {
        return couponApplicable;
    }

    public void setCouponApplicable(Boolean couponApplicable) {
        this.couponApplicable = couponApplicable;
    }

    public Boolean getVariantEnabled() {
        return variantEnabled;
    }

    public void setVariantEnabled(Boolean variantEnabled) {
        this.variantEnabled = variantEnabled;
    }

    public List<String> getImages() {
        return images;
    }

    public void setImages(List<String> images) {
        this.images = images;
    }

    public List<VariantCustomerDTO> getVariants() {
        return variants;
    }

    public void setVariants(List<VariantCustomerDTO> variants) {
        this.variants = variants;
    }
}
