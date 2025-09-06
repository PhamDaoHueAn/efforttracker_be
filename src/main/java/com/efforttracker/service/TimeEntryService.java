package com.efforttracker.service;

import com.efforttracker.exception.ResourceNotFoundException;
import com.efforttracker.model.dto.DailyHoursResponse;
import com.efforttracker.model.dto.TaskWithEntriesResponse;
import com.efforttracker.model.dto.TaskWithEntriesResponse.TimeEntrySummary;
import com.efforttracker.model.dto.TimeEntryDtos;
import com.efforttracker.model.entity.Task;
import com.efforttracker.model.entity.TimeEntry;
import com.efforttracker.model.entity.User;
import com.efforttracker.repository.TaskRepository;
import com.efforttracker.repository.TimeEntryRepository;
import com.efforttracker.repository.UserRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class TimeEntryService {

    private final TimeEntryRepository timeEntryRepository;
    private final UserRepository userRepository;
    private final TaskRepository taskRepository;

    // ----------------- CRUD -----------------
    public TimeEntry create(TimeEntryDtos.CreateTimeEntryRequest req) {
        User user = userRepository.findById(req.getUserId())
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy User với id = " + req.getUserId()));

        Task task = taskRepository.findById(req.getTaskId())
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy Task với id = " + req.getTaskId()));

        TimeEntry e = TimeEntry.builder()
                .user(user)
                .task(task)
                .date(req.getDate())
                .hours(req.getHours())
                .description(req.getDescription())
                .earnings(BigDecimal.ZERO) // earnings mặc định 0
                .build();

        return timeEntryRepository.save(e);
    }

    public TimeEntry update(String id, TimeEntryDtos.UpdateTimeEntryRequest req) {
        TimeEntry existing = timeEntryRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy TimeEntry với id = " + id));

        if (req.getHours() != null) existing.setHours(req.getHours());
        if (req.getDescription() != null) existing.setDescription(req.getDescription());
        if (req.getDate() != null) existing.setDate(req.getDate());


        return timeEntryRepository.save(existing);
    }



    public List<TimeEntry> updateEntriesForTask(String taskId, List<TimeEntryDtos.UpdateTimeEntryRequest> requests) {
        Task task = taskRepository.findById(taskId)
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy Task với id = " + taskId));

        // Lấy toàn bộ time entry của task
        List<TimeEntry> existingEntries = timeEntryRepository.findByTask(task);

        // Map theo id để tiện cập nhật
        Map<String, TimeEntry> entryMap = existingEntries.stream()
                .collect(Collectors.toMap(TimeEntry::getId, e -> e));

        List<TimeEntry> updated = new ArrayList<>();
        for (TimeEntryDtos.UpdateTimeEntryRequest req : requests) {
            TimeEntry entry = entryMap.get(req.getId());
            if (entry == null) {
                throw new ResourceNotFoundException("Không tìm thấy TimeEntry với id = " + req.getId());
            }

            // Update fields
            entry.setDate(req.getDate());
            entry.setHours(req.getHours());
            entry.setDescription(req.getDescription());
            updated.add(entry);
        }

        return timeEntryRepository.saveAll(updated);
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
                        Collectors.reducing(BigDecimal.ZERO,
                                TimeEntry::getEarnings,
                                BigDecimal::add)
                ))
                .entrySet().stream()
                .map(e -> new TimeEntryDtos.MonthlyStats(e.getKey(), e.getValue()))
                .collect(Collectors.toList());
    }

    public List<TimeEntryDtos.TeamStats> getTeamStats(LocalDate start, LocalDate end) {
        return timeEntryRepository.findByDateBetween(start, end).stream()
                .collect(Collectors.groupingBy(
                        e -> e.getUser().getId(),
                        Collectors.reducing(BigDecimal.ZERO,
                                TimeEntry::getEarnings,
                                BigDecimal::add)
                ))
                .entrySet().stream()
                .map(e -> new TimeEntryDtos.TeamStats(e.getKey(), e.getValue()))
                .collect(Collectors.toList());
    }

    public List<DailyHoursResponse> getMonthlyHours(String userId, int month, int year) {
        return timeEntryRepository.findDailyHoursByUserAndMonth(userId, month, year);
    }



    public List<TaskWithEntriesResponse> getTasksWithEntries(String userId, LocalDate start, LocalDate end) {
        List<TimeEntry> entries = timeEntryRepository.findByUserIdAndDateBetweenWithTask(userId, start, end)
                .stream()
                .filter(te -> te.getTask() != null)
                .toList();

        return entries.stream()
                .collect(Collectors.groupingBy(TimeEntry::getTask))
                .entrySet().stream()
                .map(e -> {
                    Task task = e.getKey();
                    List<TimeEntrySummary> summaries = e.getValue().stream()
                            .map(te -> new TimeEntrySummary(te.getDate(), te.getHours(), te.getDescription()))
                            .toList();

                    BigDecimal totalHours = e.getValue().stream()
                            .map(TimeEntry::getHours)
                            .reduce(BigDecimal.ZERO, BigDecimal::add);

                    return new TaskWithEntriesResponse(
                            task.getId(),
                            task.getName(),
                            task.getStatus(),
                            totalHours,
                            summaries
                    );
                })
                .toList();
    }


    public TimeEntryDtos.TimeEntryResponse toResponse(TimeEntry e) {
        if (e == null) return null;

        TimeEntryDtos.TimeEntryResponse dto = new TimeEntryDtos.TimeEntryResponse();
        dto.setId(e.getId());
        dto.setUserId(e.getUser() != null ? e.getUser().getId() : null);
        dto.setTaskId(e.getTask() != null ? e.getTask().getId() : null); //
        dto.setDate(e.getDate());
        dto.setHours(e.getHours());
        dto.setDescription(e.getDescription());
        dto.setEarnings(e.getEarnings());
        dto.setCreatedAt(e.getCreatedAt());
        dto.setUpdatedAt(e.getUpdatedAt());
        return dto;
    }

}
