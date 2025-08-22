package com.efforttracker.controller;

import com.efforttracker.model.dto.UserDtos;
import com.efforttracker.model.entity.User;
import com.efforttracker.service.UserService;
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

    // Lấy danh sách tất cả user (chỉ ADMIN mới được xem)
    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<UserDtos.UserResponse>> getAll() {
        List<UserDtos.UserResponse> list = userService.getAll()
                .stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
        return ResponseEntity.ok(list);
    }

    // Lấy user theo ID
    // ADMIN có thể xem bất kỳ, USER chỉ được xem chính mình
    @GetMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN') or #id == authentication.name")
    public ResponseEntity<UserDtos.UserResponse> getById(
            @Parameter(description = "User ID", example = "123e4567-e89b-12d3-a456-426614174000")
            @PathVariable("id") String id) {
        return ResponseEntity.ok(toResponse(userService.getById(id)));
    }

    // Tạo mới user (chỉ admin)
    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<UserDtos.UserResponse> create(
            @RequestBody @Valid UserDtos.CreateUserRequest req) {
        User user = userService.create(req);
        return ResponseEntity.ok(toResponse(user));
    }

    // Cập nhật user theo ID
    // ADMIN cập nhật bất kỳ, USER chỉ được cập nhật chính mình
    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN') or #id == authentication.name")
    public ResponseEntity<UserDtos.UserResponse> update(
            @Parameter(description = "User ID", example = "123e4567-e89b-12d3-a456-426614174000")
            @PathVariable("id") String id,
            @RequestBody @Valid UserDtos.UpdateUserRequest req) {
        User user = userService.update(id, req);
        return ResponseEntity.ok(toResponse(user));
    }

    // Xoá user theo ID (chỉ admin)
    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> delete(
            @Parameter(description = "User ID", example = "123e4567-e89b-12d3-a456-426614174000")
            @PathVariable("id") String id) {
        userService.delete(id);
        return ResponseEntity.noContent().build();
    }

    // Chuyển entity sang DTO
    private UserDtos.UserResponse toResponse(User user) {
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
