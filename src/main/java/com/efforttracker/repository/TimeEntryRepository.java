package com.efforttracker.repository;

import com.efforttracker.model.entity.TimeEntry;
import com.efforttracker.model.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.List;

public interface TimeEntryRepository extends JpaRepository<TimeEntry, String> {

    // Lấy tất cả time entries theo user
    List<TimeEntry> findByUser(User user);

    // Lấy time entries theo user trong khoảng ngày
    List<TimeEntry> findByUserAndDateBetween(User user, LocalDate start, LocalDate end);
}
