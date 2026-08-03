package com.example.Ecomm.controller;

import com.example.Ecomm.entity.Address;
import com.example.Ecomm.service.AddressService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/profile/address")
@CrossOrigin(origins = "*")
public class AddressController {

    private final AddressService addressService;

    public AddressController(AddressService addressService) {
        this.addressService = addressService;
    }

    // 🏠 GET saved addresses for logged-in user
    @GetMapping
    public ResponseEntity<List<Address>> getUserAddresses(Authentication authentication) {
        return ResponseEntity.ok(addressService.getUserAddresses(authentication.getName()));
    }

    // ➕ Add new address
    @PostMapping
    public ResponseEntity<?> createAddress(@RequestBody Address address, Authentication authentication) {
        try {
            Address created = addressService.createAddress(authentication.getName(), address);
            return ResponseEntity.ok(created);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("message", e.getMessage()));
        }
    }

    // ✏️ Update address
    @PutMapping("/{id}")
    public ResponseEntity<?> updateAddress(@PathVariable Long id, @RequestBody Address address, Authentication authentication) {
        try {
            Address updated = addressService.updateAddress(id, authentication.getName(), address);
            return ResponseEntity.ok(updated);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("message", e.getMessage()));
        }
    }

    // ⭐ Set default address
    @PutMapping("/{id}/default")
    public ResponseEntity<?> setDefaultAddress(@PathVariable Long id, Authentication authentication) {
        try {
            Address updated = addressService.setDefaultAddress(id, authentication.getName());
            return ResponseEntity.ok(updated);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("message", e.getMessage()));
        }
    }

    // 🗑️ Delete address
    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteAddress(@PathVariable Long id, Authentication authentication) {
        try {
            addressService.deleteAddress(id, authentication.getName());
            return ResponseEntity.ok(Map.of("message", "Address deleted successfully"));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("message", e.getMessage()));
        }
    }
}
