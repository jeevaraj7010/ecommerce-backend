package com.example.Ecomm.service;

import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

@Service
public class DeliveryAvailabilityService {

    // Sample restricted/blacklisted pincodes if any (otherwise open for deliverable 6-digit pincodes)
    private static final Set<String> UNCHECKED_PINCODES = Set.of("000000", "999999");

    public Map<String, Object> checkPincodeDelivery(String pincode) {
        Map<String, Object> result = new HashMap<>();

        if (pincode == null || !pincode.matches("^\\d{6}$")) {
            result.put("available", false);
            result.put("message", "Invalid 6-digit pincode format");
            return result;
        }

        if (UNCHECKED_PINCODES.contains(pincode)) {
            result.put("available", false);
            result.put("message", "Sorry, delivery is unavailable in this area");
            return result;
        }

        // Deliverable: 3 to 5 days calculation
        LocalDate deliveryDate = LocalDate.now().plusDays(4);
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("EEEE, d MMM");
        String formattedDate = deliveryDate.format(formatter);

        result.put("available", true);
        result.put("pincode", pincode);
        result.put("estimatedDays", "3-5 business days");
        result.put("estimatedDeliveryDate", formattedDate);
        result.put("shippingFee", "FREE");
        result.put("message", "✓ Delivery available • Deliver by " + formattedDate);

        return result;
    }
}
