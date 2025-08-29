package com.efforttracker.model.dto;

import jakarta.validation.constraints.NotNull;
import java.time.LocalDate;
import java.time.OffsetDateTime;

public class TimeEntryDtos {
  public static class TimeEntryResponse {
    private String id;
    private String userId;
    private LocalDate date;
    private Double hours;
    private String description;
    private Double earnings;
    private OffsetDateTime createdAt;
    private OffsetDateTime updatedAt;

    public TimeEntryResponse() {}

    public TimeEntryResponse(String id, String userId, LocalDate date, Double hours, String description,
                             Double earnings, OffsetDateTime createdAt, OffsetDateTime updatedAt) {
      this.id = id;
      this.userId = userId;
      this.date = date;
      this.hours = hours;
      this.description = description;
      this.earnings = earnings;
      this.createdAt = createdAt;
      this.updatedAt = updatedAt;
    }

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getUserId() { return userId; }
    public void setUserId(String userId) { this.userId = userId; }

    public LocalDate getDate() { return date; }
    public void setDate(LocalDate date) { this.date = date; }

    public Double getHours() { return hours; }
    public void setHours(Double hours) { this.hours = hours; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public Double getEarnings() { return earnings; }
    public void setEarnings(Double earnings) { this.earnings = earnings; }

    public OffsetDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(OffsetDateTime createdAt) { this.createdAt = createdAt; }

    public OffsetDateTime getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(OffsetDateTime updatedAt) { this.updatedAt = updatedAt; }
  }

  public static class CreateTimeEntryRequest {
    @NotNull
    private String userId;
    @NotNull
    private LocalDate date;
    @NotNull
    private Double hours;
    @NotNull
    private String description;

    public String getUserId() { return userId; }
    public void setUserId(String userId) { this.userId = userId; }

    public LocalDate getDate() { return date; }
    public void setDate(LocalDate date) { this.date = date; }

    public Double getHours() { return hours; }
    public void setHours(Double hours) { this.hours = hours; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
  }

  public static class UpdateTimeEntryRequest {
    private Double hours;
    private String description;

    public Double getHours() { return hours; }
    public void setHours(Double hours) { this.hours = hours; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
  }
  public static class MonthlyStats {
    private int month;
    private double totalEarnings;

    public MonthlyStats(int month, double totalEarnings) {
      this.month = month;
      this.totalEarnings = totalEarnings;
    }
  }
  public static class TeamStats {
    private String userId;
    private double totalEarnings;

    public TeamStats(String userId, double totalEarnings) {
      this.userId = userId;
      this.totalEarnings = totalEarnings;
    }
  }



}