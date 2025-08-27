package com.efforttracker.controller;

import com.efforttracker.model.dto.TimeEntryDtos;
import com.efforttracker.model.dto.ApiResponse;
import com.efforttracker.model.entity.TimeEntry;
import com.efforttracker.security.JwtService;
import com.efforttracker.service.TimeEntryService;
import io.swagger.v3.oas.annotations.Parameter;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
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
    @GetMapping("/by-user/{id}")
    @PreAuthorize("hasRole('ADMIN') or #id == authentication.principal.id")
    public ResponseEntity<ApiResponse<List<TimeEntryDtos.TimeEntryResponse>>> byUser(
            @Parameter(description = "ID của user cần lấy TimeEntry", example = "123")
            @PathVariable("id") String id) {

        List<TimeEntryDtos.TimeEntryResponse> list = timeEntryService.byUser(id)
                .stream()
                .map(timeEntryService::toResponse)
                .collect(Collectors.toList());

        return ResponseEntity.ok(new ApiResponse<>(true, "Lấy danh sách TimeEntry thành công!", list));
    }

    // GET /api/time-entries (lấy tất cả của user hiện tại từ token)
    @GetMapping
    @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
    public ResponseEntity<?> getAllEntries(HttpServletRequest request) {
        try {
            // Lấy JWT từ cookie "access_token"
            String token = Arrays.stream(Optional.ofNullable(request.getCookies()).orElse(new Cookie[0]))
                    .filter(c -> "access_token".equals(c.getName()))
                    .findFirst()
                    .map(Cookie::getValue)
                    .orElse(null);

            if (token == null) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                        .body(new ApiResponse<>(false, "Chưa đăng nhập!", null));
            }

            // Giải mã JWT để lấy userId
            String userId = jwtService.extractUserId(token);

            // Lấy entries của user
            List<TimeEntryDtos.TimeEntryResponse> entries = timeEntryService.byUser(userId).stream()
                    .map(timeEntryService::toResponse)
                    .collect(Collectors.toList());

            return ResponseEntity.ok(new ApiResponse<>(true, "Lấy dữ liệu thành công", entries));

        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(new ApiResponse<>(false, "Token không hợp lệ hoặc đã hết hạn!", null));
        }
    }


    // GET /api/time-entries/range?start=2025-01-01&end=2025-01-31
    @GetMapping("/range/{start}/{end}")
    @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
    public ResponseEntity<?> getEntriesInRange(
            HttpServletRequest request,
            @Parameter(description = "Ngày bắt đầu (yyyy-MM-dd)", example = "2025-01-01")
            @PathVariable("start") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate start,

            @Parameter(description = "Ngày kết thúc (yyyy-MM-dd)", example = "2025-01-31")
            @PathVariable("end") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate end
    ) {
        try {
            //Lấy JWT từ cookie
            String token = Arrays.stream(request.getCookies())
                    .filter(c -> "access_token".equals(c.getName()))
                    .findFirst()
                    .map(Cookie::getValue)
                    .orElse(null);

            if (token == null) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                        .body(new ApiResponse<>(false, "Chưa đăng nhập!", null));
            }

            // Lấy userId từ token
            String userId = jwtService.extractUserId(token);

            // Gọi service
            List<TimeEntryDtos.TimeEntryResponse> entries = timeEntryService.findInRange(userId, start, end);

            return ResponseEntity.ok(new ApiResponse<>(true, "Lấy dữ liệu thành công", entries));

        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(new ApiResponse<>(false, "Token không hợp lệ hoặc đã hết hạn!", null));
        }
    }

    // DELETE /api/time-entries/{id}
    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
    public ResponseEntity<Void> deleteEntry(
            @Parameter(description = "ID của TimeEntry cần xóa", example = "abc123")
            @PathVariable("id") String id
    ) {
        timeEntryService.delete(id);
        return ResponseEntity.noContent().build();
    }

    // GET /api/time-entries/analytics/monthly-stats/{start}/{end}
    @GetMapping("/analytics/monthly-stats/{start}/{end}")
    @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
    public ResponseEntity<?> getMonthlyStats(
            @Parameter(description = "Ngày bắt đầu (yyyy-MM-dd)", example = "2025-01-01")
            @PathVariable("start") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate start,

            @Parameter(description = "Ngày kết thúc (yyyy-MM-dd)", example = "2025-01-31")
            @PathVariable("end") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate end,
            HttpServletRequest request
    ) {
        try {
            // Lấy JWT từ cookie "access_token"
            String token = Arrays.stream(Optional.ofNullable(request.getCookies()).orElse(new Cookie[0]))
                    .filter(c -> "access_token".equals(c.getName()))
                    .findFirst()
                    .map(Cookie::getValue)
                    .orElse(null);

            if (token == null) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                        .body(new ApiResponse<>(false, "Chưa đăng nhập!", null));
            }

            // Giải mã JWT để lấy userId
            String userId = jwtService.extractUserId(token);

            // Lấy thống kê
            List<TimeEntryDtos.MonthlyStats> stats = timeEntryService.getMonthlyStats(userId, start, end);

            return ResponseEntity.ok(new ApiResponse<>(true, "Lấy thống kê thành công", stats));

        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(new ApiResponse<>(false, "Token không hợp lệ hoặc đã hết hạn!", null));
        }
    }



    // GET /api/time-entries/analytics/team-stats
    @GetMapping("/analytics/team-stats/{start}/{end}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<TimeEntryDtos.TeamStats>> getTeamStats(
            @Parameter(description = "Ngày bắt đầu (yyyy-MM-dd)", example = "2025-01-01")
            @PathVariable("start") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate start,

            @Parameter(description = "Ngày kết thúc (yyyy-MM-dd)", example = "2025-01-31")
            @PathVariable("end") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate end
    ) {
        return ResponseEntity.ok(timeEntryService.getTeamStats(start, end));
    }
}
