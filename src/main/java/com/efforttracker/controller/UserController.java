package com.efforttracker.controller;

import com.efforttracker.model.dto.ApiResponse;
import com.efforttracker.model.dto.UserDtos;
import com.efforttracker.model.entity.User;
import com.efforttracker.model.mapper.UserMapper;
import com.efforttracker.service.UserService;
import com.efforttracker.security.JwtService;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import io.swagger.v3.oas.annotations.Parameter;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;
    private final JwtService jwtService;

    // ✅ Lấy user hiện tại (dùng cookie thay vì Authorization header)
    @GetMapping("/me")
    public ResponseEntity<?> getCurrentUser(HttpServletRequest request) {
        try {
            // Lấy JWT từ cookie "access_token"
            String token = Arrays.stream(Optional.ofNullable(request.getCookies()).orElse(new Cookie[0]))
                    .filter(c -> "access_token".equals(c.getName()))
                    .findFirst()
                    .map(Cookie::getValue)
                    .orElse(null);

            // Nếu không có token => chưa đăng nhập
            if (token == null) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                        .body(new ApiResponse<>(false, "Chưa đăng nhập!", null));
            }

            // Giải mã JWT để lấy userId
            String userId = jwtService.extractUserId(token);

            // Truy vấn DB lấy user
            User user = userService.getById(userId);
            if (user == null) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(new ApiResponse<>(false, "Không tìm thấy user!", null));
            }

            // Convert sang DTO để trả về FE
            UserDtos.UserResponse userResponse = UserMapper.toUserResponse(user);


            return ResponseEntity.ok(
                    new ApiResponse<>(true, "Lấy thông tin user thành công", userResponse)
            );

        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(new ApiResponse<>(false, "Token không hợp lệ hoặc đã hết hạn!", null));
        }
    }

    // Lấy danh sách tất cả user (chỉ ADMIN)
    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<UserDtos.UserResponse>> getAll() {
        List<UserDtos.UserResponse> list = userService.getAll()
                .stream()
                .map(UserMapper::toUserResponse)
                .collect(Collectors.toList());
        return ResponseEntity.ok(list);
    }

    // Lấy user theo ID
    @GetMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN') or #id == principalId")
    public ResponseEntity<UserDtos.UserResponse> getById(
            @Parameter(description = "ID của user cần lấy", example = "123")
            @PathVariable("id") String id

    ) {

        User user = userService.getById(id);
        return ResponseEntity.ok(UserMapper.toUserResponse(user));
    }

    // Tạo mới user (chỉ ADMIN)
    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<UserDtos.UserResponse> create(
            @RequestBody @Valid UserDtos.CreateUserRequest req) {
        User user = userService.create(req);
        return ResponseEntity.ok(UserMapper.toUserResponse(user));
    }

    // Cập nhật user
    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN') or #id == principalId")
    public ResponseEntity<UserDtos.UserResponse> update(
            @Parameter(description = "ID của user cần cập nhật", example = "123")
            @PathVariable("id") String id,
            @RequestBody @Valid UserDtos.UpdateUserRequest req) {


        User updated = userService.update(id, req);
        return ResponseEntity.ok(UserMapper.toUserResponse(updated));
    }

    // Xoá user
    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> delete(
            @Parameter(description = "ID của user cần xoá", example = "123")
            @PathVariable("id") String id) {
        userService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
