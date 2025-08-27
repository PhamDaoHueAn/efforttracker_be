package com.efforttracker.repository;

import com.efforttracker.model.entity.TimeEntry;
import com.efforttracker.model.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.domain.Pageable;
import java.time.LocalDate;
import java.util.List;

public interface TimeEntryRepository extends JpaRepository<TimeEntry, String> {
    List<TimeEntry> findByUser(User user);
    List<TimeEntry> findByUserIdOrderByDateDesc(String userId);
    List<TimeEntry> findByUserIdAndDateBetweenOrderByDateAsc(String userId, LocalDate startDate, LocalDate endDate);
    List<TimeEntry> findByDateBetween(LocalDate startDate, LocalDate endDate);
    List<TimeEntry> findByUserIdOrderByCreatedAtDesc(String userId, Pageable pageable);
    List<TimeEntry> findByUserIdAndDateBetween(String userId, LocalDate start, LocalDate end);
}
