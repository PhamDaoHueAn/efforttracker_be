package com.efforttracker.repository;

import com.efforttracker.model.entity.Task;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.util.List;

public interface TaskRepository extends JpaRepository<Task, String> {

    // Tìm task theo trạng thái
    List<Task> findByStatus(String status);

    // Tìm task theo deadline
    List<Task> findByDueDate(LocalDate dueDate);

    @Query("SELECT DISTINCT t FROM Task t " +
            "LEFT JOIN FETCH t.timeEntries te " +
            "LEFT JOIN FETCH te.user u " +
            "WHERE u.id = :userId")
    List<Task> findByUserIdWithAll(@Param("userId") String userId);

}
