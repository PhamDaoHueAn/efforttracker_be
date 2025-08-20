package com.efforttracker.controller;

import com.efforttracker.model.dto.AuthDtos;
import com.efforttracker.service.AuthService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.beans.factory.annotation.Autowired;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {
    @Autowired
    private AuthService authService;

    @PostMapping("/register")
    public ResponseEntity<Void> register(@RequestBody @Valid AuthDtos.RegisterRequest req) {
        authService.register(req);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/login")
    public ResponseEntity<AuthDtos.LoginResponse> login(@RequestBody @Valid AuthDtos.LoginRequest req) {
        return ResponseEntity.ok(authService.login(req));
    }
}