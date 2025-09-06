package com.efforttracker.repository;

import com.efforttracker.model.dto.DailyHoursResponse;
import com.efforttracker.model.entity.Task;
import com.efforttracker.model.entity.TimeEntry;
import com.efforttracker.model.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.util.List;

public interface TimeEntryRepository extends JpaRepository<TimeEntry, String> {
    List<TimeEntry> findByUser(User user);
    List<TimeEntry> findByUserIdOrderByDateDesc(String userId);
    List<TimeEntry> findByUserIdAndDateBetweenOrderByDateAsc(String userId, LocalDate startDate, LocalDate endDate);
    List<TimeEntry> findByDateBetween(LocalDate startDate, LocalDate endDate);
    List<TimeEntry> findByUserIdOrderByCreatedAtDesc(String userId, Pageable pageable);
    List<TimeEntry> findByTask(Task task);
    List<TimeEntry> findByTaskId(String taskId);
    List<TimeEntry> findByUserIdAndDateBetween(String userId, LocalDate start, LocalDate end);
    @Query("SELECT te FROM TimeEntry te JOIN FETCH te.task WHERE te.user.id = :userId AND te.date BETWEEN :start AND :end")
    List<TimeEntry> findByUserIdAndDateBetweenWithTask(@Param("userId") String userId,
                                                       @Param("start") LocalDate start,
                                                       @Param("end") LocalDate end);
    @Query("SELECT new com.efforttracker.model.dto.DailyHoursResponse(t.date, SUM(t.hours)) " +
            "FROM TimeEntry t " +
            "WHERE t.user.id = :userId " +
            "AND EXTRACT(MONTH FROM t.date) = :month " +
            "AND EXTRACT(YEAR FROM t.date) = :year " +
            "GROUP BY t.date ORDER BY t.date ASC")
    List<DailyHoursResponse> findDailyHoursByUserAndMonth(
            @Param("userId") String userId,
            @Param("month") int month,
            @Param("year") int year);


}
