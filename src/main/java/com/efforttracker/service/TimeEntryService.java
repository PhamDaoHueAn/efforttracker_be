package com.efforttracker.service;

import com.efforttracker.exception.ResourceNotFoundException;
import com.efforttracker.model.dto.TimeEntryDtos;
import com.efforttracker.model.entity.TimeEntry;
import com.efforttracker.model.entity.User;
import com.efforttracker.repository.TimeEntryRepository;
import com.efforttracker.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

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

    public List<TimeEntry> byUser(String userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy User với id = " + userId));

        return timeEntryRepository.findByUser(user);
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
