package com.efforttracker.controller;

import com.efforttracker.model.dto.ApiResponse;
import com.efforttracker.model.dto.UserDtos;
import com.efforttracker.model.entity.User;
import com.efforttracker.model.mapper.UserMapper;
import com.efforttracker.security.UserDetailsImpl;
import com.efforttracker.service.UserService;
import com.efforttracker.security.JwtService;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
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

    // Lấy user hiện tại
    @GetMapping("/me")
    public ResponseEntity<?> getCurrentUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication == null
                || !authentication.isAuthenticated()
                || authentication instanceof AnonymousAuthenticationToken) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(new ApiResponse<>(false, "Chưa đăng nhập!", null));
        }

        UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();
        User user = userService.getById(userDetails.getId());
        UserDtos.UserResponse userResponse = UserMapper.toUserResponse(user);

        return ResponseEntity.ok(new ApiResponse<>(true, "Lấy thông tin user thành công", userResponse));
    }

    // Lấy danh sách tất cả user (ADMIN)
    @GetMapping
    public ResponseEntity<?> getAllUsers() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (!isAdmin(auth)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body(new ApiResponse<>(false, "Không có quyền truy cập!", null));
        }

        List<UserDtos.UserResponse> list = userService.getAll()
                .stream()
                .map(UserMapper::toUserResponse)
                .collect(Collectors.toList());
        return ResponseEntity.ok(new ApiResponse<>(true, "Lấy danh sách thành công", list));
    }

    // Lấy user theo ID
    @GetMapping("/{id}")
    public ResponseEntity<?> getById(@PathVariable String id) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        UserDetailsImpl userDetails = (UserDetailsImpl) auth.getPrincipal();

        if (!isAdmin(auth) && !id.equals(userDetails.getId())) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body(new ApiResponse<>(false, "Không có quyền truy cập!", null));
        }

        User user = userService.getById(id);
        return ResponseEntity.ok(new ApiResponse<>(true, "Lấy thông tin user thành công", UserMapper.toUserResponse(user)));
    }

    // Tạo mới user (ADMIN)
    @PostMapping
    public ResponseEntity<?> create(@RequestBody @Valid UserDtos.CreateUserRequest req) {
        if (!isAdmin(SecurityContextHolder.getContext().getAuthentication())) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body(new ApiResponse<>(false, "Không có quyền truy cập!", null));
        }
        User user = userService.create(req);
        return ResponseEntity.ok(new ApiResponse<>(true, "Tạo user thành công", UserMapper.toUserResponse(user)));
    }

    // Cập nhật user
    @PutMapping("/{id}")
    public ResponseEntity<?> update(@PathVariable String id, @RequestBody @Valid UserDtos.UpdateUserRequest req) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        UserDetailsImpl userDetails = (UserDetailsImpl) auth.getPrincipal();

        if (!isAdmin(auth) && !id.equals(userDetails.getId())) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body(new ApiResponse<>(false, "Không có quyền cập nhật!", null));
        }

        User updated = userService.update(id, req);
        return ResponseEntity.ok(new ApiResponse<>(true, "Cập nhật thành công", UserMapper.toUserResponse(updated)));
    }

    // Xoá user
    @DeleteMapping("/{id}")
    public ResponseEntity<?> delete(@PathVariable String id) {
        if (!isAdmin(SecurityContextHolder.getContext().getAuthentication())) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body(new ApiResponse<>(false, "Không có quyền xoá!", null));
        }
        userService.delete(id);
        return ResponseEntity.ok(new ApiResponse<>(true, "Xoá thành công", null));
    }

    // Helper kiểm tra quyền admin
    private boolean isAdmin(Authentication auth) {
        if (auth == null || !auth.isAuthenticated() || auth instanceof AnonymousAuthenticationToken) {
            return false;
        }
        return auth.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN"));
    }
}

