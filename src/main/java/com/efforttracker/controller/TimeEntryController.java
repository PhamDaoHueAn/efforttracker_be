package com.efforttracker.controller;

import com.efforttracker.model.dto.DailyHoursResponse;
import com.efforttracker.model.dto.TaskWithEntriesResponse;
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

        // ép userId trong request = user hiện tại (tránh fake userId)
        req.setUserId(user.getId());

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


    @GetMapping("/me")
    public ResponseEntity<?> getMyEntries() {
        UserDetailsImpl user = getCurrentUser();
        if (user == null)
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(new ApiResponse<>(false, "Chưa đăng nhập!", null));

        List<TimeEntryDtos.TimeEntryResponse> list = timeEntryService.byUser(user.getId())
                .stream()
                .map(timeEntryService::toResponse)
                .collect(Collectors.toList());

        return ResponseEntity.ok(new ApiResponse<>(true, "Lấy danh sách TimeEntry thành công!", list));
    }


    @PutMapping("/{id}")
    public ResponseEntity<?> updateEntry(
            @PathVariable String id,
            @RequestBody @Valid TimeEntryDtos.UpdateTimeEntryRequest req
    ) {
        UserDetailsImpl user = getCurrentUser();
        if (user == null)
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(new ApiResponse<>(false, "Chưa đăng nhập!", null));

        TimeEntry updated = timeEntryService.update(id, req);
        var response = timeEntryService.toResponse(updated);
        return ResponseEntity.ok(new ApiResponse<>(true, "Cập nhật TimeEntry thành công!", response));
    }

    @PutMapping("/task/{taskId}/bulk-update")
    public ResponseEntity<?> bulkUpdate(
            @PathVariable String taskId,
            @RequestBody List<TimeEntryDtos.UpdateTimeEntryRequest> requests
    ) {
        UserDetailsImpl user = getCurrentUser();
        if (user == null)
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(new ApiResponse<>(false, "Chưa đăng nhập!", null));

        List<TimeEntry> updated = timeEntryService.updateEntriesForTask(taskId, requests);
        var responses = updated.stream().map(timeEntryService::toResponse).toList();

        return ResponseEntity.ok(new ApiResponse<>(true, "Bulk update thành công!", responses));
    }

    // Lấy entries trong khoảng ngày
    @GetMapping("/range/{start}/{end}")
    public ResponseEntity<?> getEntriesInRange(
            @PathVariable @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate start,
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
    public ResponseEntity<?> deleteEntry(@PathVariable String id) {
        UserDetailsImpl user = getCurrentUser();
        if (user == null)
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(new ApiResponse<>(false, "Chưa đăng nhập!", null));

        timeEntryService.delete(id);
        return ResponseEntity.ok(new ApiResponse<>(true, "Xóa thành công", null));
    }

    // Thống kê hàng tháng (theo user hiện tại)
    @GetMapping("/analytics/monthly-stats/{start}/{end}")
    public ResponseEntity<?> getMonthlyStats(
            @PathVariable @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate start,
            @PathVariable @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate end
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
            @PathVariable @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate start,
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
            @PathVariable int month,
            @PathVariable int year
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

    // Lấy danh sách Task + TimeEntries của user trong khoảng ngày
    @GetMapping("/analytics/tasks-with-entries/{start}/{end}")
    public ResponseEntity<?> getTasksWithEntries(
            @PathVariable @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate start,
            @PathVariable @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate end
    ) {
        UserDetailsImpl user = getCurrentUser();
        if (user == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(new ApiResponse<>(false, "Chưa đăng nhập!", null));
        }

        List<TaskWithEntriesResponse> result =
                timeEntryService.getTasksWithEntries(user.getId(), start, end);

        return ResponseEntity.ok(
                new ApiResponse<>(true, "Lấy danh sách task kèm time entries thành công!", result)
        );
    }
}


