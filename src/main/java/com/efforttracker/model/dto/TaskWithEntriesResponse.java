package com.efforttracker.model.dto;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

public class TaskWithEntriesResponse {

    private String taskId;
    private String name;
    private String status;
    private BigDecimal totalHours;
    private List<TimeEntrySummary> timeEntries;

    // Constructor
    public TaskWithEntriesResponse(String taskId, String name, String status,
                                   BigDecimal totalHours, List<TimeEntrySummary> timeEntries) {
        this.taskId = taskId;
        this.name = name;
        this.status = status;
        this.totalHours = totalHours;
        this.timeEntries = timeEntries;
    }

    // Getters & Setters
    public String getTaskId() { return taskId; }
    public void setTaskId(String taskId) { this.taskId = taskId; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public BigDecimal getTotalHours() { return totalHours; }
    public void setTotalHours(BigDecimal totalHours) { this.totalHours = totalHours; }

    public List<TimeEntrySummary> getTimeEntries() { return timeEntries; }
    public void setTimeEntries(List<TimeEntrySummary> timeEntries) { this.timeEntries = timeEntries; }

    // Inner DTO: log theo ngày
    public static class TimeEntrySummary {
        private LocalDate date;
        private BigDecimal hours;
        private String description;

        public TimeEntrySummary(LocalDate date, BigDecimal hours, String description) {
            this.date = date;
            this.hours = hours;
            this.description = description;
        }

        public LocalDate getDate() { return date; }
        public void setDate(LocalDate date) { this.date = date; }

        public BigDecimal getHours() { return hours; }
        public void setHours(BigDecimal hours) { this.hours = hours; }

        public String getDescription() { return description; }
        public void setDescription(String description) { this.description = description; }
    }
}
