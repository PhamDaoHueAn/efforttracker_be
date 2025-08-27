package com.efforttracker.controller;

import com.efforttracker.model.dto.AuthDtos;
import com.efforttracker.model.dto.ApiResponse;
import com.efforttracker.model.dto.UserDtos;
import com.efforttracker.model.entity.User;
import com.efforttracker.model.mapper.UserMapper;
import com.efforttracker.service.AuthService;
import com.efforttracker.service.UserService;
import com.efforttracker.security.JwtService;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.security.authentication.BadCredentialsException;

import java.time.Duration;
import java.util.Arrays;
import java.util.Optional;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;
    private final UserService userService;
    private final JwtService jwtService;





    // ✅ Đăng ký
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

    // Đăng nhập + set cookie
    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody @Valid AuthDtos.LoginRequest req) {
        try {
            AuthDtos.LoginResponse res = authService.login(req);

            // Tạo cookie chứa accessToken
            ResponseCookie cookie = ResponseCookie.from("access_token", res.getAccessToken()) // ⚡ nếu record
                    .httpOnly(true)
                    .secure(false) // để true khi deploy HTTPS
                    .sameSite("Lax") // "Strict" sẽ chặn nhiều trường hợp, "Lax" thân thiện hơn
                    .path("/")
                    .maxAge(Duration.ofHours(1))
                    .build();

            return ResponseEntity.ok()
                    .header(HttpHeaders.SET_COOKIE, cookie.toString())
                    .body(new ApiResponse<>(true, "Đăng nhập thành công!", res));

        } catch (BadCredentialsException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(new ApiResponse<>(false, "Sai email hoặc mật khẩu!", null));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(new ApiResponse<>(false, "Đăng nhập thất bại: " + e.getMessage(), null));
        }
    }


    // ✅ Đăng xuất (xoá cookie)
    @PostMapping("/logout")
    public ResponseEntity<?> logout() {
        ResponseCookie cookie = ResponseCookie.from("access_token", "")
                .httpOnly(true)
                .secure(false)
                .sameSite("Strict")
                .path("/")
                .maxAge(0) // hết hạn ngay lập tức
                .build();

        return ResponseEntity.ok()
                .header(HttpHeaders.SET_COOKIE, cookie.toString())
                .body(new ApiResponse<>(true, "Đăng xuất thành công!", null));
    }
}
