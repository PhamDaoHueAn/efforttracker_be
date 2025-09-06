package com.efforttracker.model.mapper;

import com.efforttracker.model.dto.TimeEntryDtos;
import com.efforttracker.model.entity.TimeEntry;
import com.efforttracker.model.entity.User;
import java.math.BigDecimal;

public class TimeEntryMapper {

    public static TimeEntryDtos.TimeEntryResponse toTimeEntryResponse(TimeEntry entry) {
        if (entry == null) return null;
        TimeEntryDtos.TimeEntryResponse dto = new TimeEntryDtos.TimeEntryResponse();
        dto.setId(entry.getId());
        dto.setUserId(entry.getUser() != null ? entry.getUser().getId() : null);
        dto.setDate(entry.getDate());
        dto.setHours(entry.getHours());
        dto.setDescription(entry.getDescription());
        dto.setEarnings(entry.getEarnings());
        dto.setCreatedAt(entry.getCreatedAt());
        dto.setUpdatedAt(entry.getUpdatedAt());
        return dto;
    }

    public static TimeEntry toTimeEntryEntity(TimeEntryDtos.CreateTimeEntryRequest dto, User user) {
        if (dto == null) return null;
        TimeEntry entry = new TimeEntry();
        entry.setUser(user);
        entry.setDate(dto.getDate());
        entry.setHours(dto.getHours());
        entry.setDescription(dto.getDescription());
        // earnings có thể tính toán hoặc set mặc định
        entry.setEarnings(BigDecimal.ZERO);
        return entry;
    }
}