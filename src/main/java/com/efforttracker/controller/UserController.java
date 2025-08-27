package com.efforttracker.controller;

import com.efforttracker.model.dto.UserDtos;
import com.efforttracker.model.entity.User;
import com.efforttracker.model.mapper.UserMapper;
import com.efforttracker.service.UserService;
import com.efforttracker.security.JwtService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import io.swagger.v3.oas.annotations.Parameter;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;
    private final JwtService jwtService;

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

    // Lấy user hiện tại từ token hoặc theo ID
    @GetMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN') or #id == principalId")
    public ResponseEntity<UserDtos.UserResponse> getById(
            @PathVariable("id") String id,
            @RequestHeader("Authorization") String authHeader
    ) {
        String token = authHeader.replace("Bearer ", "");
        String principalId = jwtService.extractUserId(token);

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
            @PathVariable("id") String id,
            @RequestHeader("Authorization") String authHeader,
            @RequestBody @Valid UserDtos.UpdateUserRequest req) {

        String token = authHeader.replace("Bearer ", "");
        String principalId = jwtService.extractUserId(token);

        User updated = userService.update(id, req);
        return ResponseEntity.ok(UserMapper.toUserResponse(updated));
    }

    // Xoá user (chỉ ADMIN)
    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> delete(@PathVariable("id") String id) {
        userService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
