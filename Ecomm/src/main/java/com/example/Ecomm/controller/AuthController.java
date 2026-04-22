package com.example.Ecomm.controller;

import com.example.Ecomm.entity.User;
import com.example.Ecomm.repository.UserRepository;
import com.example.Ecomm.security.JwtUtil;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;

import java.time.LocalDateTime;
import java.util.*;

@RestController
@RequestMapping("/auth")
@CrossOrigin("*")
public class AuthController {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;
    private final JavaMailSender mailSender;

    @Autowired
    public AuthController(UserRepository userRepository,
                          PasswordEncoder passwordEncoder,
                          JwtUtil jwtUtil,
                          JavaMailSender mailSender) {

        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtUtil = jwtUtil;
        this.mailSender = mailSender;
    }

    // =========================
    // 📝 REGISTER
    // =========================
    @PostMapping("/register")
    public ResponseEntity<?> register(@RequestBody Map<String, String> request) {

        String username = request.get("username");
        String phone = request.get("phone");
        String email = request.get("email");
        String password = request.get("password");

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

        return ResponseEntity.ok("User registered successfully ✅");
    }

    // =========================
    // 🔐 LOGIN
    // =========================
    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody Map<String, String> request) {

        String username = request.get("username");
        String password = request.get("password");

        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found"));

        if (!passwordEncoder.matches(password, user.getPassword())) {
            return ResponseEntity.badRequest().body("Invalid credentials ❌");
        }

        String token = jwtUtil.generateToken(user.getUsername(), user.getRole());

        Map<String, Object> res = new HashMap<>();
        res.put("token", token);
        res.put("role", user.getRole());

        return ResponseEntity.ok(res);
    }

    // =========================
    // 👤 PROFILE
    // =========================
    @GetMapping("/me")
    public ResponseEntity<?> getProfile(@RequestHeader("Authorization") String authHeader) {

        String token = authHeader.substring(7);
        String username = jwtUtil.extractUsername(token);

        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found"));

        Map<String, Object> data = new HashMap<>();
        data.put("username", user.getUsername());
        data.put("email", user.getEmail());
        data.put("phone", user.getPhone());
        data.put("role", user.getRole());

        return ResponseEntity.ok(data);
    }

    // =========================
    // 📩 SEND OTP
    // =========================
    @PostMapping("/send-otp")
    public ResponseEntity<?> sendOtp(@RequestBody Map<String, String> request) {

        String email = request.get("email");

        Optional<User> optionalUser = userRepository.findByEmail(email);

        if (optionalUser.isEmpty()) {
            return ResponseEntity.badRequest().body("Email not registered ❌");
        }

        User user = optionalUser.get();

        String otp = String.valueOf(new Random().nextInt(900000) + 100000);

        user.setOtp(otp);
        user.setOtpExpiry(LocalDateTime.now().plusMinutes(5));
        userRepository.save(user);

        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(user.getEmail());
        message.setSubject("OTP for Password Reset");
        message.setText("Your OTP is: " + otp + "\nValid for 5 minutes");

        mailSender.send(message);

        return ResponseEntity.ok("OTP sent successfully ✅");
    }

    // =========================
    // 🔐 VERIFY OTP + RESET PASSWORD
    // =========================
    @PostMapping("/verify-otp")
    public ResponseEntity<?> verifyOtp(@RequestBody Map<String, String> request) {

        String email = request.get("email");
        String otp = request.get("otp");
        String newPassword = request.get("password");

        Optional<User> optionalUser = userRepository.findByEmail(email);

        if (optionalUser.isEmpty()) {
            return ResponseEntity.badRequest().body("User not found ❌");
        }

        User user = optionalUser.get();

        if (user.getOtp() == null || !user.getOtp().equals(otp)) {
            return ResponseEntity.badRequest().body("Invalid OTP ❌");
        }

        if (user.getOtpExpiry() == null ||
            user.getOtpExpiry().isBefore(LocalDateTime.now())) {

            return ResponseEntity.badRequest().body("OTP expired ❌");
        }

        user.setPassword(passwordEncoder.encode(newPassword));
        user.setOtp(null);
        user.setOtpExpiry(null);

        userRepository.save(user);

        return ResponseEntity.ok("Password reset successful ✅");
    }

    // =========================
    // 📍 UPDATE ADDRESS
    // =========================
    @PutMapping("/update-address")
    public ResponseEntity<?> updateAddress(
            @RequestHeader("Authorization") String authHeader,
            @RequestBody Map<String, String> request) {

        try {
            String token = authHeader.substring(7);
            String username = jwtUtil.extractUsername(token);

            User user = userRepository.findByUsername(username)
                    .orElseThrow(() -> new RuntimeException("User not found"));

            user.setPhone(request.get("phone"));
            user.setAddress(request.get("address"));

            userRepository.save(user);

            return ResponseEntity.ok("Address updated successfully ✅");

        } catch (Exception e) {
            return ResponseEntity.status(403).body("Unauthorized ❌");
        }
    }
}