package com.efforttracker.controller;

import com.efforttracker.model.dto.AuthDtos;
import com.efforttracker.model.dto.ApiResponse;
import com.efforttracker.service.AuthService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.security.authentication.BadCredentialsException;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    // Đăng ký
    @PostMapping("/register")
    public ResponseEntity<?> register(@RequestBody @Valid AuthDtos.RegisterRequest req) {
        try {
            authService.register(req);
            return ResponseEntity.ok(new ApiResponse<>(true, "Đăng ký thành công!", null));
        } catch (DataIntegrityViolationException e) {
            return ResponseEntity.status(HttpStatus.CONFLICT)
                    .body(new ApiResponse<>(false, "Email đã tồn tại!", null));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(new ApiResponse<>(false, "Đăng ký thất bại: " + e.getMessage(), null));
        }
    }

    // Đăng nhập
    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody @Valid AuthDtos.LoginRequest req) {
        try {
            AuthDtos.LoginResponse res = authService.login(req);
            return ResponseEntity.ok(new ApiResponse<>(true, "Đăng nhập thành công!", res));
        } catch (BadCredentialsException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(new ApiResponse<>(false, "Sai email hoặc mật khẩu!", null));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(new ApiResponse<>(false, "Đăng nhập thất bại: " + e.getMessage(), null));
        }
    }
}

