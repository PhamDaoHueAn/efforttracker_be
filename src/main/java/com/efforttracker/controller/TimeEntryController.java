package com.efforttracker.controller;

import com.efforttracker.model.dto.DailyHoursResponse;
import com.efforttracker.model.dto.TimeEntryDtos;
import com.efforttracker.model.dto.ApiResponse;
import com.efforttracker.model.entity.TimeEntry;
import com.efforttracker.security.JwtService;
import com.efforttracker.security.UserDetailsImpl;
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
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
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

    // Helper kiểm tra quyền admin
    private boolean isAdmin(Authentication auth) {
        if (auth == null || !auth.isAuthenticated() || auth instanceof AnonymousAuthenticationToken) return false;
        return auth.getAuthorities().stream().anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN"));
    }

    // Helper lấy user hiện tại
    private UserDetailsImpl getCurrentUser() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null || !auth.isAuthenticated() || auth instanceof AnonymousAuthenticationToken) return null;
        return (UserDetailsImpl) auth.getPrincipal();
    }

    // USER hoặc ADMIN đều có thể tạo TimeEntry
    @PostMapping
    public ResponseEntity<?> create(@RequestBody @Valid TimeEntryDtos.CreateTimeEntryRequest req) {
        UserDetailsImpl user = getCurrentUser();
        if (user == null)
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(new ApiResponse<>(false, "Chưa đăng nhập!", null));

        TimeEntry entry = timeEntryService.create(req);
        var response = timeEntryService.toResponse(entry);
        return ResponseEntity.ok(new ApiResponse<>(true, "Tạo TimeEntry thành công!", response));
    }

    // Lấy tất cả entries của user hiện tại
    @GetMapping
    public ResponseEntity<?> getAllEntries() {
        UserDetailsImpl user = getCurrentUser();
        if (user == null)
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(new ApiResponse<>(false, "Chưa đăng nhập!", null));

        List<TimeEntryDtos.TimeEntryResponse> entries = timeEntryService.byUser(user.getId())
                .stream()
                .map(timeEntryService::toResponse)
                .collect(Collectors.toList());

        return ResponseEntity.ok(new ApiResponse<>(true, "Lấy dữ liệu thành công", entries));
    }


    // Lấy entries theo userId (ADMIN có thể xem của bất kỳ ai, USER chỉ của chính mình)
    @GetMapping("/by-user/{id}")
    public ResponseEntity<?> byUser(@Parameter(description = "Nhập id") @PathVariable String id) {
        UserDetailsImpl user = getCurrentUser();
        if (user == null)
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(new ApiResponse<>(false, "Chưa đăng nhập!", null));

        if (!isAdmin(SecurityContextHolder.getContext().getAuthentication()) && !id.equals(user.getId()))
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body(new ApiResponse<>(false, "Không có quyền truy cập!", null));

        List<TimeEntryDtos.TimeEntryResponse> list = timeEntryService.byUser(id)
                .stream()
                .map(timeEntryService::toResponse)
                .collect(Collectors.toList());

        return ResponseEntity.ok(new ApiResponse<>(true, "Lấy danh sách TimeEntry thành công!", list));
    }

    // Lấy entries trong khoảng ngày
    @GetMapping("/range/{start}/{end}")
    public ResponseEntity<?> getEntriesInRange(
            @Parameter(description = "Ngày bắt đầu")
            @PathVariable @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate start,
            @Parameter(description = "Ngày kết thúc")
            @PathVariable @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate end
    ) {
        UserDetailsImpl user = getCurrentUser();
        if (user == null)
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(new ApiResponse<>(false, "Chưa đăng nhập!", null));

        List<TimeEntryDtos.TimeEntryResponse> entries = timeEntryService.findInRange(user.getId(), start, end);
        return ResponseEntity.ok(new ApiResponse<>(true, "Lấy dữ liệu thành công", entries));
    }

    // Xóa entry (USER hoặc ADMIN)
    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteEntry(@Parameter(description = "Nhập id") @PathVariable String id) {
        UserDetailsImpl user = getCurrentUser();
        if (user == null)
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(new ApiResponse<>(false, "Chưa đăng nhập!", null));

        // Optional: kiểm tra quyền ADMIN nếu muốn
        timeEntryService.delete(id);
        return ResponseEntity.ok(new ApiResponse<>(true, "Xóa thành công", null));
    }

    // Thống kê hàng tháng
    @GetMapping("/analytics/monthly-stats/{start}/{end}")
    public ResponseEntity<?> getMonthlyStats(
            @Parameter(description = "Ngày bắt đầu") @PathVariable @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate start,
            @Parameter(description = "Ngày kết thúc") @PathVariable @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate end
    ) {
        UserDetailsImpl user = getCurrentUser();
        if (user == null)
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(new ApiResponse<>(false, "Chưa đăng nhập!", null));

        List<TimeEntryDtos.MonthlyStats> stats = timeEntryService.getMonthlyStats(user.getId(), start, end);
        return ResponseEntity.ok(new ApiResponse<>(true, "Lấy thống kê thành công", stats));
    }

    // Thống kê team (chỉ ADMIN)
    @GetMapping("/analytics/team-stats/{start}/{end}")
    public ResponseEntity<?> getTeamStats(
            @Parameter(description = "Ngày bắt đầu")
            @PathVariable @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate start,
            @Parameter(description = "Ngày kết thúc")
            @PathVariable @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate end
    ) {
        if (!isAdmin(SecurityContextHolder.getContext().getAuthentication()))
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body(new ApiResponse<>(false, "Không có quyền truy cập!", null));

        List<TimeEntryDtos.TeamStats> stats = timeEntryService.getTeamStats(start, end);
        return ResponseEntity.ok(new ApiResponse<>(true, "Lấy thống kê team thành công", stats));
    }

    // Lấy số giờ làm theo từng ngày trong tháng hiện tại
    @GetMapping("/analytics/monthly-hours/{month}/{year}")
    public ResponseEntity<?> getMonthlyHours(
            @Parameter(description = "Tháng (1-12)") @PathVariable int month,
            @Parameter(description = "Năm") @PathVariable int year
    ) {
        UserDetailsImpl user = getCurrentUser();
        if (user == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(new ApiResponse<>(false, "Chưa đăng nhập!", null));
        }

        List<DailyHoursResponse> dailyHours =
                timeEntryService.getMonthlyHours(user.getId(), month, year);

        return ResponseEntity.ok(
                new ApiResponse<>(true, "Lấy thống kê giờ làm theo tháng thành công!", dailyHours)
        );
    }


}

