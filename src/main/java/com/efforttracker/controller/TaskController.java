package com.efforttracker.controller;

import com.efforttracker.model.dto.ApiResponse;
import com.efforttracker.model.dto.TaskDtos;
import com.efforttracker.model.entity.Task;
import com.efforttracker.security.UserDetailsImpl;
import com.efforttracker.service.TaskService;
import io.swagger.v3.oas.annotations.Parameter;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/tasks")
@RequiredArgsConstructor
public class TaskController {

    private final TaskService taskService;

    // Helper lấy user hiện tại
    private UserDetailsImpl getCurrentUser() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null || !auth.isAuthenticated() || auth instanceof AnonymousAuthenticationToken) return null;
        return (UserDetailsImpl) auth.getPrincipal();
    }

    // Lấy tất cả tasks (Admin)
    @GetMapping
    public ResponseEntity<?> getAll() {
        UserDetailsImpl user = getCurrentUser();
        if (user == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(new ApiResponse<>(false, "Chưa đăng nhập!", null));
        }

        List<TaskDtos.TaskResponse> tasks = taskService.getAll();
        return ResponseEntity.ok(new ApiResponse<>(true, "Lấy danh sách Task thành công!", tasks));
    }

    // Lấy tasks của user hiện tại
    @GetMapping("/me")
    public ResponseEntity<?> getMyTasks() {
        UserDetailsImpl user = getCurrentUser();
        if (user == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(new ApiResponse<>(false, "Chưa đăng nhập!", null));
        }

        List<TaskDtos.TaskResponse> tasks = taskService.getByUserId(user.getId());
        return ResponseEntity.ok(new ApiResponse<>(true, "Lấy danh sách Task của bạn thành công!", tasks));
    }

    // Lấy task theo id
    @GetMapping("/{id}")
    public ResponseEntity<?> getById(@Parameter(description = "Task ID") @PathVariable String id) {
        UserDetailsImpl user = getCurrentUser();
        if (user == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(new ApiResponse<>(false, "Chưa đăng nhập!", null));
        }

        TaskDtos.TaskResponse task = taskService.getById(id);
        return ResponseEntity.ok(new ApiResponse<>(true, "Lấy Task thành công!", task));
    }

    // Tạo task
    @PostMapping
    public ResponseEntity<?> create(@RequestBody @Valid TaskDtos.CreateTaskRequest req) {
        UserDetailsImpl user = getCurrentUser();
        if (user == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(new ApiResponse<>(false, "Chưa đăng nhập!", null));
        }

        TaskDtos.TaskResponse task = taskService.create(req);
        return ResponseEntity.ok(new ApiResponse<>(true, "Tạo Task thành công!", task));
    }

    // Tạo task kèm entries
    @PostMapping("/with-entries")
    public ResponseEntity<?> createWithEntries(
            @RequestBody @Valid TaskDtos.CreateTaskWithEntriesRequest req
    ) {
        UserDetailsImpl user = getCurrentUser();
        if (user == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(new ApiResponse<>(false, "Chưa đăng nhập!", null));
        }

        TaskDtos.TaskResponse task = taskService.createWithEntries(req, user.getId());
        return ResponseEntity.ok(new ApiResponse<>(true, "Tạo Task kèm Entries thành công!", task));
    }

    // Cập nhật task
    @PutMapping("/{id}")
    public ResponseEntity<?> update(
            @Parameter(description = "Task ID") @PathVariable String id,
            @RequestBody @Valid TaskDtos.UpdateTaskRequest req
    ) {
        UserDetailsImpl user = getCurrentUser();
        if (user == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(new ApiResponse<>(false, "Chưa đăng nhập!", null));
        }

        TaskDtos.TaskResponse task = taskService.update(id, req);
        return ResponseEntity.ok(new ApiResponse<>(true, "Cập nhật Task thành công!", task));
    }

    // Xóa task
    @DeleteMapping("/{id}")
    public ResponseEntity<?> delete(@Parameter(description = "Task ID") @PathVariable String id) {
        UserDetailsImpl user = getCurrentUser();
        if (user == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(new ApiResponse<>(false, "Chưa đăng nhập!", null));
        }

        taskService.delete(id);
        return ResponseEntity.ok(new ApiResponse<>(true, "Xóa Task thành công!", null));
    }
}
