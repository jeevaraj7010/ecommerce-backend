package com.example.Ecomm.controller;

import com.example.Ecomm.entity.User;
import com.example.Ecomm.repository.UserRepository;
import com.example.Ecomm.security.JwtUtil;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;
import org.springframework.security.core.Authentication;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/auth")
@CrossOrigin(origins = "http://localhost:3000")
public class AuthController {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
    private final JwtUtil jwtUtil;

    private Map<String, String> otpStorage = new HashMap<>();

    public AuthController(UserRepository userRepository,
                          PasswordEncoder passwordEncoder,
                          AuthenticationManager authenticationManager,
                          JwtUtil jwtUtil) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.authenticationManager = authenticationManager;
        this.jwtUtil = jwtUtil;
    }

    @PostMapping("/send-otp")
    public ResponseEntity<?> sendOtp(@RequestBody Map<String, String> request) {
        String phone = request.get("phone");

        if (phone == null || phone.isBlank()) {
            return ResponseEntity.badRequest().body("Phone number is required");
        }

        String otp = String.valueOf((int) ((Math.random() * 9000) + 1000));
        otpStorage.put(phone, otp);

        System.out.println("OTP for " + phone + " = " + otp);

        return ResponseEntity.ok("OTP sent successfully");
    }

    @PostMapping("/register")
    public ResponseEntity<?> register(@RequestBody Map<String, String> request) {
        String username = request.get("username");
        String phone = request.get("phone");
        String password = request.get("password");
        String otp = request.get("otp");

        if (username == null || username.isBlank()
                || phone == null || phone.isBlank()
                || password == null || password.isBlank()
                || otp == null || otp.isBlank()) {
            return ResponseEntity.badRequest().body("All fields are required");
        }

        String savedOtp = otpStorage.get(phone);

        if (savedOtp == null) {
            return ResponseEntity.badRequest().body("OTP not sent or expired");
        }

        if (!savedOtp.equals(otp)) {
            return ResponseEntity.badRequest().body("Invalid OTP");
        }

        if (userRepository.findByUsername(username).isPresent()) {
            return ResponseEntity.badRequest().body("Username already exists");
        }

        User user = new User();
        user.setUsername(username);
        user.setPhone(phone);
        user.setPassword(passwordEncoder.encode(password));
        user.setRole("ROLE_USER");

        userRepository.save(user);
        otpStorage.remove(phone);

        return ResponseEntity.ok("User registered successfully");
    }
    
    @GetMapping("/me")
    public ResponseEntity<?> getCurrentUser(Authentication authentication) {
        User user = userRepository.findByUsername(authentication.getName())
                .orElseThrow(() -> new RuntimeException("User not found"));

        Map<String, Object> response = new HashMap<>();
        response.put("username", user.getUsername());
        response.put("phone", user.getPhone());
        response.put("address", user.getAddress());
        response.put("role", user.getRole());

        return ResponseEntity.ok(response);
    }
    
    @PutMapping("/update-address")
    public ResponseEntity<?> updateAddress(
            @RequestBody Map<String, String> request,
            Authentication authentication) {

        User user = userRepository.findByUsername(authentication.getName())
                .orElseThrow(() -> new RuntimeException("User not found"));

        String phone = request.get("phone");
        String address = request.get("address");

        if (phone == null || phone.isBlank() || address == null || address.isBlank()) {
            return ResponseEntity.badRequest().body("Phone and address are required");
        }

        user.setPhone(phone);
        user.setAddress(address);
        userRepository.save(user);

        return ResponseEntity.ok("Address updated successfully");
    }
    
    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody Map<String, String> request) {
        String username = request.get("username");
        String password = request.get("password");

        if (username == null || username.isBlank()
                || password == null || password.isBlank()) {
            return ResponseEntity.badRequest().body("Username and password are required");
        }

        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(username, password)
        );

        String token = jwtUtil.generateToken(username);

        Map<String, String> response = new HashMap<>();
        response.put("token", token);

        return ResponseEntity.ok(response);
    }
}