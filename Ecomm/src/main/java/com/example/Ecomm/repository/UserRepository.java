package com.example.Ecomm.repository;

import com.example.Ecomm.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {

    // 🔐 Login
    Optional<User> findByUsername(String username);

    // 📧 Forgot password
    Optional<User> findByEmail(String email);

    // 🔑 Reset password
    Optional<User> findByResetToken(String token);
}