package com.efforttracker.controller;

import com.efforttracker.model.dto.TimeEntryDtos;
import com.efforttracker.model.dto.ApiResponse;
import com.efforttracker.model.entity.TimeEntry;
import com.efforttracker.service.TimeEntryService;
import io.swagger.v3.oas.annotations.Parameter;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/time-entries")
@RequiredArgsConstructor
public class TimeEntryController {

    private final TimeEntryService timeEntryService;

    // USER hoặc ADMIN đều có thể tạo TimeEntry
    @PostMapping
    @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<TimeEntryDtos.TimeEntryResponse>> create(
            @RequestBody @Valid TimeEntryDtos.CreateTimeEntryRequest req) {
        TimeEntry entry = timeEntryService.create(req);
        var response = timeEntryService.toResponse(entry);
        return ResponseEntity.ok(new ApiResponse<>(true, "Tạo TimeEntry thành công!", response));
    }

    // ADMIN có thể xem của bất kỳ user nào, USER chỉ được xem của chính mình
    @GetMapping("/by-user/{userId}")
    @PreAuthorize("hasRole('ADMIN') or #userId == authentication.principal.id")
    public ResponseEntity<ApiResponse<List<TimeEntryDtos.TimeEntryResponse>>> byUser(
            @Parameter(
                    description = "ID của user cần lấy TimeEntry",
                    required = true,
                    example = "123e4567-e89b-12d3-a456-426614174000"
            )
            @PathVariable String userId) {

        List<TimeEntryDtos.TimeEntryResponse> list = timeEntryService.byUser(userId)
                .stream()
                .map(timeEntryService::toResponse)
                .collect(Collectors.toList());

        return ResponseEntity.ok(new ApiResponse<>(true, "Lấy danh sách TimeEntry thành công!", list));
    }

}
