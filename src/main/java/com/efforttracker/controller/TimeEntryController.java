package com.efforttracker.controller;

import com.efforttracker.model.dto.TimeEntryDtos;
import com.efforttracker.service.TimeEntryService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/time-entries")
@RequiredArgsConstructor
public class TimeEntryController {
    @Autowired
    private TimeEntryService timeEntryService;

    // USER hoặc ADMIN đều có thể tạo TimeEntry
    @PostMapping
    @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
    public ResponseEntity<TimeEntryDtos.TimeEntryResponse> create(
            @RequestBody @Valid TimeEntryDtos.CreateTimeEntryRequest req) {
        return ResponseEntity.ok(timeEntryService.toResponse(timeEntryService.create(req)));
    }

    // Chỉ ADMIN mới có thể xem tất cả của người khác
    @GetMapping("/by-user/{userId}")
    @PreAuthorize("hasRole('ADMIN') or #userId == authentication.principal.user.id")
    public ResponseEntity<List<TimeEntryDtos.TimeEntryResponse>> byUser(@PathVariable String userId) {
        List<TimeEntryDtos.TimeEntryResponse> list =
                timeEntryService.byUser(userId).stream()
                        .map(timeEntryService::toResponse)
                        .collect(Collectors.toList());
        return ResponseEntity.ok(list);
    }
}
