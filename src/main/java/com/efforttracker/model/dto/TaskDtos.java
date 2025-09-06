package com.efforttracker.model.dto;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.util.List;

import jakarta.validation.constraints.NotBlank;

public class TaskDtos {

  public static class TaskResponse {
    private String id;
    private String name;
    private String description;
    private String status;
    private LocalDate startDate;
    private LocalDate dueDate;
    private OffsetDateTime createdAt;
    private OffsetDateTime updatedAt;

    public TaskResponse() {}

    public TaskResponse(String id, String name, String description, String status,
                        LocalDate startDate, LocalDate dueDate,
                        OffsetDateTime createdAt, OffsetDateTime updatedAt) {
      this.id = id;
      this.name = name;
      this.description = description;
      this.status = status;
      this.startDate = startDate;
      this.dueDate = dueDate;
      this.createdAt = createdAt;
      this.updatedAt = updatedAt;
    }

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public LocalDate getStartDate() { return startDate; }
    public void setStartDate(LocalDate startDate) { this.startDate = startDate; }

    public LocalDate getDueDate() { return dueDate; }
    public void setDueDate(LocalDate dueDate) { this.dueDate = dueDate; }

    public OffsetDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(OffsetDateTime createdAt) { this.createdAt = createdAt; }

    public OffsetDateTime getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(OffsetDateTime updatedAt) { this.updatedAt = updatedAt; }
  }

  public static class CreateTaskRequest {
    @NotBlank
    private String name;
    private String description;
    private String status;
    private LocalDate startDate;
    private LocalDate dueDate;

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public LocalDate getStartDate() { return startDate; }
    public void setStartDate(LocalDate startDate) { this.startDate = startDate; }

    public LocalDate getDueDate() { return dueDate; }
    public void setDueDate(LocalDate dueDate) { this.dueDate = dueDate; }
  }

  public static class CreateTaskWithEntriesRequest {

    private String name;
    private String description;
    private String status;
    private LocalDate startDate;
    private LocalDate dueDate;
    private List<TimeEntryDto> entries;

    // getters/setters

    public String getName() {
      return name;
    }

    public void setName(String name) {
      this.name = name;
    }

    public List<TimeEntryDto> getEntries() {
      return entries;
    }

    public void setEntries(List<TimeEntryDto> entries) {
      this.entries = entries;
    }

    public LocalDate getDueDate() {
      return dueDate;
    }

    public void setDueDate(LocalDate dueDate) {
      this.dueDate = dueDate;
    }

    public LocalDate getStartDate() {
      return startDate;
    }

    public void setStartDate(LocalDate startDate) {
      this.startDate = startDate;
    }

    public String getStatus() {
      return status;
    }

    public void setStatus(String status) {
      this.status = status;
    }

    public String getDescription() {
      return description;
    }

    public void setDescription(String description) {
      this.description = description;
    }


    public static class TimeEntryDto {
      private LocalDate date;
      private BigDecimal hours;
      private String description;
      // getters/setters

      public LocalDate getDate() {
        return date;
      }

      public void setDate(LocalDate date) {
        this.date = date;
      }

      public BigDecimal getHours() {
        return hours;
      }

      public void setHours(BigDecimal hours) {
        this.hours = hours;
      }

      public String getDescription() {
        return description;
      }

      public void setDescription(String description) {
        this.description = description;
      }

    }
  }

  public static class UpdateTaskRequest {
    private String name;
    private String description;
    private String status;
    private LocalDate startDate;
    private LocalDate dueDate;

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public LocalDate getStartDate() { return startDate; }
    public void setStartDate(LocalDate startDate) { this.startDate = startDate; }

    public LocalDate getDueDate() { return dueDate; }
    public void setDueDate(LocalDate dueDate) { this.dueDate = dueDate; }
  }
}
