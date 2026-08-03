package com.example.Ecomm.controller;

import com.example.Ecomm.service.DeliveryAvailabilityService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/location/pincode")
@CrossOrigin(origins = "*")
public class PincodeController {

    private final DeliveryAvailabilityService availabilityService;
    private final RestTemplate restTemplate;

    public PincodeController(DeliveryAvailabilityService availabilityService) {
        this.availabilityService = availabilityService;
        this.restTemplate = new RestTemplate();
    }

    @GetMapping("/{pincode}")
    public ResponseEntity<Map<String, Object>> getPincodeLocation(@PathVariable String pincode) {
        Map<String, Object> response = new HashMap<>();

        if (pincode == null || !pincode.matches("^\\d{6}$")) {
            response.put("available", false);
            response.put("message", "Invalid 6-digit pincode");
            return ResponseEntity.badRequest().body(response);
        }

        // Get availability info
        Map<String, Object> availMap = availabilityService.checkPincodeDelivery(pincode);
        response.putAll(availMap);

        // Try calling India Post Pincode API for city/district/state
        try {
            String url = "https://api.postalpincode.in/pincode/" + pincode;
            List<Map<String, Object>> apiResponse = restTemplate.getForObject(url, List.class);

            if (apiResponse != null && !apiResponse.isEmpty()) {
                Map<String, Object> firstObj = apiResponse.get(0);
                String status = (String) firstObj.get("Status");
                List<Map<String, Object>> postOffices = (List<Map<String, Object>>) firstObj.get("PostOffice");

                if ("Success".equalsIgnoreCase(status) && postOffices != null && !postOffices.isEmpty()) {
                    Map<String, Object> po = postOffices.get(0);
                    response.put("city", po.get("Name") != null ? po.get("Name") : po.get("District"));
                    response.put("district", po.get("District"));
                    response.put("state", po.get("State"));
                }
            }
        } catch (Exception e) {
            // Fallback gracefully if external API is unreachable
            System.err.println("Pincode API lookup error: " + e.getMessage());
        }

        return ResponseEntity.ok(response);
    }
}
