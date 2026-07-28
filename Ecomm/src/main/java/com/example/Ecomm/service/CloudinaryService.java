package com.example.Ecomm.service;

import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Base64;
import java.util.Map;

@Service
public class CloudinaryService {

    @Autowired
    private Cloudinary cloudinary;

    public String uploadImage(MultipartFile file) {
        if (file == null || file.isEmpty()) {
            throw new IllegalArgumentException("File cannot be empty");
        }

        try {
            Map uploadResult = cloudinary.uploader().upload(file.getBytes(), ObjectUtils.emptyMap());
            if (uploadResult != null && uploadResult.containsKey("secure_url")) {
                return uploadResult.get("secure_url").toString();
            } else if (uploadResult != null && uploadResult.containsKey("url")) {
                return uploadResult.get("url").toString();
            }
        } catch (Exception e) {
            // Fallback for unconfigured or offline Cloudinary environment
            System.err.println("Cloudinary upload fallback triggered: " + e.getMessage());
            try {
                String base64 = Base64.getEncoder().encodeToString(file.getBytes());
                String contentType = file.getContentType() != null ? file.getContentType() : "image/png";
                return "data:" + contentType + ";base64," + base64;
            } catch (IOException ioException) {
                throw new RuntimeException("Failed to process image file", ioException);
            }
        }

        throw new RuntimeException("Could not obtain URL from Cloudinary upload");
    }
}
