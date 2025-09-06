package com.efforttracker.model.dto;

import jakarta.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.OffsetDateTime;

public class TimeEntryDtos {

  // Dữ liệu trả về cho FE
  public static class TimeEntryResponse {
    private String id;
    private String userId;
    private String taskId;
    private LocalDate date;
    private BigDecimal hours;
    private String description;
    private BigDecimal earnings;
    private OffsetDateTime createdAt;
    private OffsetDateTime updatedAt;

    public TimeEntryResponse() {}

    public TimeEntryResponse(String id, String userId, String taskId, LocalDate date,
                             BigDecimal hours, String description,
                             BigDecimal earnings, OffsetDateTime createdAt, OffsetDateTime updatedAt) {
      this.id = id;
      this.userId = userId;
      this.taskId = taskId;
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

    public String getTaskId() { return taskId; }
    public void setTaskId(String taskId) { this.taskId = taskId; }

    public LocalDate getDate() { return date; }
    public void setDate(LocalDate date) { this.date = date; }

    public BigDecimal getHours() { return hours; }
    public void setHours(BigDecimal hours) { this.hours = hours; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public BigDecimal getEarnings() { return earnings; }
    public void setEarnings(BigDecimal earnings) { this.earnings = earnings; }

    public OffsetDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(OffsetDateTime createdAt) { this.createdAt = createdAt; }

    public OffsetDateTime getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(OffsetDateTime updatedAt) { this.updatedAt = updatedAt; }
  }

  // Request tạo mới
  public static class CreateTimeEntryRequest {
    @NotNull
    private String userId;
    private String taskId;
    @NotNull
    private LocalDate date;
    @NotNull
    private BigDecimal hours;
    @NotNull
    private String description;

    public String getUserId() { return userId; }
    public void setUserId(String userId) { this.userId = userId; }

    public String getTaskId() { return taskId; }
    public void setTaskId(String taskId) { this.taskId = taskId; }

    public LocalDate getDate() { return date; }
    public void setDate(LocalDate date) { this.date = date; }

    public BigDecimal getHours() { return hours; }
    public void setHours(BigDecimal hours) { this.hours = hours; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
  }

  // Request cập nhật
  public static class UpdateTimeEntryRequest {
    @NotNull
    private String id;

    private LocalDate date;
    private BigDecimal hours;
    private String description;
    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public LocalDate getDate() { return date; }
    public void setDate(LocalDate date) { this.date = date; }

    public BigDecimal getHours() { return hours; }
    public void setHours(BigDecimal hours) { this.hours = hours; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

  }


  // DTO cho thống kê theo tháng
  public static class MonthlyStats {
    private int month;
    private BigDecimal totalEarnings;

    public MonthlyStats(int month, BigDecimal totalEarnings) {
      this.month = month;
      this.totalEarnings = totalEarnings;
    }

    public int getMonth() { return month; }
    public void setMonth(int month) { this.month = month; }

    public BigDecimal getTotalEarnings() { return totalEarnings; }
    public void setTotalEarnings(BigDecimal totalEarnings) { this.totalEarnings = totalEarnings; }
  }

  // DTO cho thống kê theo user
  public static class TeamStats {
    private String userId;
    private BigDecimal totalEarnings;

    public TeamStats(String userId, BigDecimal totalEarnings) {
      this.userId = userId;
      this.totalEarnings = totalEarnings;
    }

    public String getUserId() { return userId; }
    public void setUserId(String userId) { this.userId = userId; }

    public BigDecimal getTotalEarnings() { return totalEarnings; }
    public void setTotalEarnings(BigDecimal totalEarnings) { this.totalEarnings = totalEarnings; }
  }
}
