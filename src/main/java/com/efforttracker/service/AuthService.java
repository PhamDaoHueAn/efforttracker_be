package com.efforttracker.service;

import com.efforttracker.model.dto.AuthDtos;
import com.efforttracker.model.dto.UserDtos;
import com.efforttracker.model.entity.User;
import com.efforttracker.repository.UserRepository;
import com.efforttracker.security.JwtService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;

    public void register(AuthDtos.RegisterRequest req) {
        if (userRepository.existsByEmail(req.getEmail())) {
            throw new IllegalArgumentException("Email already exists");
        }

        String role = (req.getRole() != null && !req.getRole().isBlank())
                ? req.getRole().toUpperCase()
                : "USER"; // mặc định USER nếu không nhập

        User user = User.builder()
                .email(req.getEmail())
                .password(passwordEncoder.encode(req.getPassword()))
                .firstName(req.getFirstName())
                .lastName(req.getLastName())
                .role(role)   // gán role ở đây
                .hourlyRate(BigDecimal.ZERO)
                .build();

        userRepository.save(user);
    }

    public AuthDtos.LoginResponse login(AuthDtos.LoginRequest req) {
        User user = userRepository.findByEmail(req.getEmail())
                .orElseThrow(() -> new IllegalArgumentException("User not found"));

        if (!passwordEncoder.matches(req.getPassword(), user.getPassword())) {
            throw new IllegalArgumentException("Invalid credentials");
        }

        // ✅ Generate JWT bằng JwtService
        String token = jwtService.generateToken(user.getEmail());

        return new AuthDtos.LoginResponse(
                token,
                "Bearer",
                3600, // seconds, hoặc lấy từ config
                toUserResponse(user)
        );
    }

    private UserDtos.UserResponse toUserResponse(User user) {
        UserDtos.UserResponse dto = new UserDtos.UserResponse();
        dto.setId(user.getId());
        dto.setEmail(user.getEmail());
        dto.setFirstName(user.getFirstName());
        dto.setLastName(user.getLastName());
        dto.setRole(user.getRole());
        dto.setHourlyRate(user.getHourlyRate());
        dto.setNotes(user.getNotes());
        return dto;
    }

}
