package com.efforttracker.service;

import com.efforttracker.model.dto.TimeEntryDtos;
import com.efforttracker.model.entity.TimeEntry;
import com.efforttracker.model.entity.User;
import com.efforttracker.repository.TimeEntryRepository;
import com.efforttracker.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

@Service
@RequiredArgsConstructor
public class TimeEntryService {
    @Autowired
    private TimeEntryRepository timeEntryRepository;
    @Autowired
    private UserRepository userRepository;

    public TimeEntry create(TimeEntryDtos.CreateTimeEntryRequest req) {
        User user = userRepository.findById(req.getUserId())
            .orElseThrow(() -> new IllegalArgumentException("User not found"));
        TimeEntry e = TimeEntry.builder()
                .user(user)
                .date(req.getDate())
                .hours(req.getHours() != null ? java.math.BigDecimal.valueOf(req.getHours()) : null)
                .description(req.getDescription())
                .earnings(java.math.BigDecimal.ZERO)
                .build();
        return timeEntryRepository.save(e);
    }

    public List<TimeEntry> byUser(String userId) {
        User user = userRepository.findById(userId)
            .orElseThrow(() -> new IllegalArgumentException("User not found"));
        return timeEntryRepository.findByUser(user);
    }

    public TimeEntryDtos.TimeEntryResponse toResponse(TimeEntry e) {
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