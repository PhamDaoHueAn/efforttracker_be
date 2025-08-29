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
            throw new IllegalArgumentException("Email đã tồn tại");
        }

        String role = (req.getRole() != null && !req.getRole().isBlank())
                ? req.getRole().toUpperCase()
                : "USER"; // mặc định USER nếu không nhập

        User user = User.builder()
                .email(req.getEmail())
                .password(passwordEncoder.encode(req.getPassword()))
                .firstName(req.getFirstName())
                .lastName(req.getLastName())
                .role(role)
                .hourlyRate(req.getHourlyRate())
                .notes(req.getNotes())
                .build();

        userRepository.save(user);
    }

    public AuthDtos.LoginResponse login(AuthDtos.LoginRequest req) {
        User user = userRepository.findByEmail(req.getEmail())
                .orElseThrow(() -> new IllegalArgumentException("Tài khoản không tồn tại"));

        if (!passwordEncoder.matches(req.getPassword(), user.getPassword())) {
            throw new IllegalArgumentException("Sai mật khẩu");
        }

        String token = jwtService.generateToken(user.getId(), user.getRole());

        return new AuthDtos.LoginResponse(
                token,
                "Bearer",
                3600, // giây
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
