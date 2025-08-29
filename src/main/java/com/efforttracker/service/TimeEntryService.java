package com.efforttracker.service;

import com.efforttracker.exception.ResourceNotFoundException;
import com.efforttracker.model.dto.DailyHoursResponse;
import com.efforttracker.model.dto.TimeEntryDtos;
import com.efforttracker.model.entity.TimeEntry;
import com.efforttracker.model.entity.User;
import com.efforttracker.repository.TimeEntryRepository;
import com.efforttracker.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;


import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class TimeEntryService {

    private final TimeEntryRepository timeEntryRepository;
    private final UserRepository userRepository;

    public TimeEntry create(TimeEntryDtos.CreateTimeEntryRequest req) {
        User user = userRepository.findById(req.getUserId())
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy User với id = " + req.getUserId()));

        TimeEntry e = TimeEntry.builder()
                .user(user)
                .date(req.getDate())
                .hours(req.getHours() != null ? java.math.BigDecimal.valueOf(req.getHours()) : null)
                .description(req.getDescription())
                .earnings(java.math.BigDecimal.ZERO) // earnings mặc định 0
                .build();

        return timeEntryRepository.save(e);
    }

    public List<TimeEntryDtos.TimeEntryResponse> findInRange(String userId, LocalDate from, LocalDate to) {
        return timeEntryRepository.findByUserIdAndDateBetweenOrderByDateAsc(userId, from, to)
                .stream().map(this::toResponse).collect(Collectors.toList());
    }

    public List<TimeEntry> byUser(String userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy User với id = " + userId));

        return timeEntryRepository.findByUser(user);
    }

    public List<TimeEntryDtos.TimeEntryResponse> findAllWithLimit(String userId, int limit) {
        return timeEntryRepository.findByUserIdOrderByCreatedAtDesc(
                userId,
                PageRequest.of(0, limit)
        ).stream().map(this::toResponse).collect(Collectors.toList());
    }


    public void delete(String id) {
        if (!timeEntryRepository.existsById(id)) {
            throw new ResourceNotFoundException("Không tìm thấy TimeEntry với id = " + id);
        }
        timeEntryRepository.deleteById(id);
    }


    public List<TimeEntryDtos.MonthlyStats> getMonthlyStats(String userId, LocalDate start, LocalDate end) {
        return timeEntryRepository.findByUserIdAndDateBetween(userId, start, end).stream()
                .collect(Collectors.groupingBy(
                        e -> e.getDate().getMonthValue(),
                        Collectors.summingDouble(e -> e.getEarnings().doubleValue())
                ))
                .entrySet().stream()
                .map(e -> new TimeEntryDtos.MonthlyStats(e.getKey(), e.getValue()))
                .collect(Collectors.toList());
    }


    public List<TimeEntryDtos.TeamStats> getTeamStats(LocalDate start, LocalDate end) {
        return timeEntryRepository.findByDateBetween(start, end).stream()
                .collect(Collectors.groupingBy(
                        e -> e.getUser().getId(),
                        Collectors.summingDouble(e -> e.getEarnings().doubleValue())
                ))
                .entrySet().stream()
                .map(e -> new TimeEntryDtos.TeamStats(e.getKey(), e.getValue()))
                .collect(Collectors.toList());
    }

    public List<DailyHoursResponse> getMonthlyHours(String userId, int month, int year) {
        return timeEntryRepository.findDailyHoursByUserAndMonth(userId, month, year);
    }

    public TimeEntryDtos.TimeEntryResponse toResponse(TimeEntry e) {
        if (e == null) return null;

        TimeEntryDtos.TimeEntryResponse dto = new TimeEntryDtos.TimeEntryResponse();
        dto.setId(e.getId());
        dto.setUserId(e.getUser() != null ? e.getUser().getId() : null);
        dto.setDate(e.getDate());
        dto.setHours(e.getHours() != null ? e.getHours().doubleValue() : null);
        dto.setDescription(e.getDescription());
        dto.setEarnings(e.getEarnings() != null ? e.getEarnings().doubleValue() : null);
        dto.setCreatedAt(e.getCreatedAt());
        dto.setUpdatedAt(e.getUpdatedAt());
        return dto;
    }
}
