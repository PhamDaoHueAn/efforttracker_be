package com.efforttracker.controller;

import com.efforttracker.model.dto.TimeEntryDtos;
import com.efforttracker.model.dto.ApiResponse;
import com.efforttracker.model.entity.TimeEntry;
import com.efforttracker.security.JwtService;
import com.efforttracker.security.UserDetailsImpl;
import com.efforttracker.service.TimeEntryService;
import io.swagger.v3.oas.annotations.Parameter;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/time-entries")
@RequiredArgsConstructor
public class TimeEntryController {

    private final TimeEntryService timeEntryService;
    private final JwtService jwtService;

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
            @PathVariable String userId) {

        List<TimeEntryDtos.TimeEntryResponse> list = timeEntryService.byUser(userId)
                .stream()
                .map(timeEntryService::toResponse)
                .collect(Collectors.toList());

        return ResponseEntity.ok(new ApiResponse<>(true, "Lấy danh sách TimeEntry thành công!", list));
    }

    // GET /api/time-entries
    @GetMapping
    @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
    public ResponseEntity<List<TimeEntryDtos.TimeEntryResponse>> getAllEntries(
            @RequestHeader("Authorization") String authHeader
    ) {
        String token = authHeader.replace("Bearer ", "");
        String userId = jwtService.extractUserId(token);

        List<TimeEntryDtos.TimeEntryResponse> entries = timeEntryService.byUser(userId).stream()
                .map(timeEntryService::toResponse)
                .collect(Collectors.toList());

        return ResponseEntity.ok(entries);
    }

    // GET /api/time-entries/range?start=2025-01-01&end=2025-01-31
    @GetMapping("/range")
    @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
    public ResponseEntity<List<TimeEntryDtos.TimeEntryResponse>> getEntriesInRange(
            @RequestHeader("Authorization") String authHeader,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate start,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate end
    ) {
        String token = authHeader.replace("Bearer ", "");
        String userId = jwtService.extractUserId(token);

        return ResponseEntity.ok(timeEntryService.findInRange(userId, start, end));
    }

    // DELETE /api/time-entries/{id}
    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
    public ResponseEntity<Void> deleteEntry(
            @PathVariable String id
    ) {
        timeEntryService.delete(id);
        return ResponseEntity.noContent().build();
    }

    // GET /api/time-entries/analytics/monthly-stats?start=2025-01-01&end=2025-12-31
    @GetMapping("/analytics/monthly-stats")
    @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
    public ResponseEntity<List<TimeEntryDtos.MonthlyStats>> getMonthlyStats(
            @RequestHeader("Authorization") String authHeader,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate start,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate end
    ) {
        String token = authHeader.replace("Bearer ", "");
        String userId = jwtService.extractUserId(token);

        return ResponseEntity.ok(timeEntryService.getMonthlyStats(userId, start, end));
    }


    // GET /api/time-entries/analytics/team-stats?start=2025-01-01&end=2025-12-31
    @GetMapping("/analytics/team-stats")
    @PreAuthorize("hasRole('ADMIN')") // chỉ admin mới cần team stats
    public ResponseEntity<List<TimeEntryDtos.TeamStats>> getTeamStats(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate start,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate end
    ) {
        return ResponseEntity.ok(timeEntryService.getTeamStats(start, end));
    }
}

