package com.example.Ecomm.controller;

import com.example.Ecomm.entity.User;
import com.example.Ecomm.repository.UserRepository;
import com.example.Ecomm.security.JwtUtil;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.*;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;

import java.time.LocalDateTime;
import java.util.*;

@RestController
@RequestMapping("/auth")
public class AuthController {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
    private final JwtUtil jwtUtil;
    private final JavaMailSender mailSender;

    @Autowired
    public AuthController(UserRepository userRepository,
                          PasswordEncoder passwordEncoder,
                          AuthenticationManager authenticationManager,
                          JwtUtil jwtUtil,
                          JavaMailSender mailSender) {

        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.authenticationManager = authenticationManager;
        this.jwtUtil = jwtUtil;
        this.mailSender = mailSender;
    }

    // =========================
    // 📝 REGISTER (NO OTP)
    // =========================
    @PostMapping("/register")
    public ResponseEntity<?> register(@RequestBody Map<String, String> request) {

        String username = request.get("username");
        String phone = request.get("phone");
        String email = request.get("email");
        String password = request.get("password");

        // ✅ FIXED
        if (userRepository.findByUsername(username).isPresent()) {
            return ResponseEntity.badRequest().body("Username already exists");
        }

        if (userRepository.findByEmail(email).isPresent()) {
            return ResponseEntity.badRequest().body("Email already registered");
        }

        User user = new User();
        user.setUsername(username);
        user.setPhone(phone);
        user.setEmail(email);
        user.setPassword(passwordEncoder.encode(password));
        user.setRole("ROLE_USER");

        userRepository.save(user);

        return ResponseEntity.ok("User registered successfully");
    }
    // =========================
    // 🔐 LOGIN
    // =========================
    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody Map<String, String> request) {

        String username = request.get("username");
        String password = request.get("password");

        try {
            authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(username, password)
            );
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Invalid credentials");
        }

        String token = jwtUtil.generateToken(username);

        return ResponseEntity.ok(Collections.singletonMap("token", token));
    }

    // =========================
    // 👤 PROFILE
    // =========================
    @GetMapping("/me")
    public ResponseEntity<?> getProfile(@RequestHeader("Authorization") String authHeader) {

        String token = authHeader.substring(7);
        String username = jwtUtil.extractUsername(token);

        Optional<User> optionalUser = userRepository.findByUsername(username);

        if (optionalUser.isEmpty()) {
            return ResponseEntity.badRequest().body("User not found");
        }

        User user = optionalUser.get();

        Map<String, Object> data = new HashMap<>();
        data.put("username", user.getUsername());
        data.put("phone", user.getPhone());
        data.put("email", user.getEmail());
        data.put("role", user.getRole());

        return ResponseEntity.ok(data);
    }

    // =========================
    // 📧 FORGOT PASSWORD (EMAIL)
    // =========================
    @PostMapping("/forgot-password")
    public ResponseEntity<?> forgotPassword(@RequestBody Map<String, String> request) {

        String email = request.get("email");

        // ✅ Validate input
        if (email == null || email.isBlank()) {
            return ResponseEntity.badRequest().body("Email is required");
        }

        // ✅ Use Optional properly
        Optional<User> optionalUser = userRepository.findByEmail(email);

        if (optionalUser.isEmpty()) {
            // 🔐 Security: do NOT reveal user existence
            return ResponseEntity.ok("If this email exists, a reset link has been sent");
        }

        User user = optionalUser.get();

        // 🔐 Generate token
        String token = UUID.randomUUID().toString();

        user.setResetToken(token);
        user.setTokenExpiry(LocalDateTime.now().plusMinutes(10));
        userRepository.save(user);

        // 🔗 Reset link
        String link = "https://ecommerce-frontend-h3as.vercel.app/reset-password?token=" + token;

        // 📧 Send email
        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(email);
        message.setSubject("Reset Password");
        message.setText("Click here to reset your password:\n" + link);

        mailSender.send(message);

        return ResponseEntity.ok("If this email exists, a reset link has been sent");
    }
    @GetMapping("/health")
    public String health() {
        return "OK";
    }

 @PutMapping("/update-address")
 public ResponseEntity<?> updateAddress(
         @RequestHeader("Authorization") String authHeader,
         @RequestBody Map<String, String> request) {

     try {
         String token = authHeader.substring(7);
         String username = jwtUtil.extractUsername(token);

         Optional<User> optionalUser = userRepository.findByUsername(username);

         if (optionalUser.isEmpty()) {
             return ResponseEntity.badRequest().body("User not found");
         }

         User user = optionalUser.get();

         user.setPhone(request.get("phone"));
         user.setAddress(request.get("address"));

         userRepository.save(user);

         return ResponseEntity.ok("Address updated successfully");

     } catch (Exception e) {
         return ResponseEntity.status(403).body("Unauthorized");
     }
 }
    // =========================
    // 🔁 RESET PASSWORD
    // =========================
    @PostMapping("/reset-password")
    public ResponseEntity<?> resetPassword(@RequestBody Map<String, String> request) {

        String token = request.get("token");
        String newPassword = request.get("password");

        Optional<User> optionalUser = userRepository.findByResetToken(token);

        if (optionalUser.isEmpty()) {
            return ResponseEntity.badRequest().body("Invalid token");
        }

        User user = optionalUser.get();

        if (user.getTokenExpiry() == null || user.getTokenExpiry().isBefore(LocalDateTime.now())) {
            return ResponseEntity.badRequest().body("Token expired");
        }

        user.setPassword(passwordEncoder.encode(newPassword));

        // 🔐 clear token after use
        user.setResetToken(null);
        user.setTokenExpiry(null);

        userRepository.save(user);

        return ResponseEntity.ok("Password updated successfully");
    }
}