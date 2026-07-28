package com.example.Ecomm.service;

import com.example.Ecomm.entity.Customization;
import com.example.Ecomm.entity.Product;
import com.example.Ecomm.entity.User;
import com.example.Ecomm.repository.CustomizationRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CustomizationService {

    @Autowired
    private CustomizationRepository customizationRepository;

    public Customization saveCustomization(User user, Product product, String imageUrl, String customText) {
        Customization customization = new Customization(user, product, imageUrl, customText);
        return customizationRepository.save(customization);
    }

    public List<Customization> getAllCustomizations() {
        return customizationRepository.findAll();
    }

    public Customization getCustomizationById(Long id) {
        return customizationRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Customization request not found"));
    }

    public Customization updateStatus(Long id, String status) {
        Customization customization = getCustomizationById(id);
        if (status != null && !status.trim().isEmpty()) {
            customization.setStatus(status.toUpperCase());
        }
        return customizationRepository.save(customization);
    }
}
