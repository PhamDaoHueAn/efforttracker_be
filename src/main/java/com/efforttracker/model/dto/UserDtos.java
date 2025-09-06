package com.efforttracker.model.dto;

import java.math.BigDecimal;
import java.time.OffsetDateTime;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public class UserDtos {
  public static class UserResponse {
    private String id;
    private String email;
    private String firstName;
    private String lastName;
    private String role;
    private BigDecimal hourlyRate;
    private String notes;
    private OffsetDateTime createdAt;
    private OffsetDateTime updatedAt;

    public UserResponse() {}

    public UserResponse(String id, String email, String firstName, String lastName, String role, BigDecimal hourlyRate, String notes, OffsetDateTime createdAt, OffsetDateTime updatedAt) {
      this.id = id;
      this.email = email;
      this.firstName = firstName;
      this.lastName = lastName;
      this.role = role;
      this.hourlyRate = hourlyRate;
      this.notes = notes;
      this.createdAt = createdAt;
      this.updatedAt = updatedAt;
    }

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getFirstName() { return firstName; }
    public void setFirstName(String firstName) { this.firstName = firstName; }

    public String getLastName() { return lastName; }
    public void setLastName(String lastName) { this.lastName = lastName; }

    public String getRole() { return role; }
    public void setRole(String role) { this.role = role; }

    public BigDecimal getHourlyRate() { return hourlyRate; }
    public void setHourlyRate(BigDecimal hourlyRate) { this.hourlyRate = hourlyRate; }

    public String getNotes() { return notes; }
    public void setNotes(String notes) { this.notes = notes; }

    public OffsetDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(OffsetDateTime createdAt) { this.createdAt = createdAt; }

    public OffsetDateTime getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(OffsetDateTime updatedAt) { this.updatedAt = updatedAt; }
  }

  public static class CreateUserRequest {
    @Email
    private String email;
    @NotBlank
    private String password;
    private String firstName;
    private String lastName;
    private String role;
    private String notes;
    @NotNull
    private BigDecimal hourlyRate;

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }

    public String getFirstName() { return firstName; }
    public void setFirstName(String firstName) { this.firstName = firstName; }

    public String getLastName() { return lastName; }
    public void setLastName(String lastName) { this.lastName = lastName; }

    public String getRole() { return role; }
    public void setRole(String role) { this.role = role; }

    public BigDecimal getHourlyRate() { return hourlyRate; }
    public void setHourlyRate(BigDecimal hourlyRate) { this.hourlyRate = hourlyRate; }

    public String getNotes() { return notes; }
    public void setNotes(String notes) { this.notes = notes; }
  }

  public static class UpdateUserRequest {
    private String firstName;
    private String lastName;
    private String role;
    private BigDecimal hourlyRate;
    private String notes;

    public String getFirstName() { return firstName; }
    public void setFirstName(String firstName) { this.firstName = firstName; }

    public String getLastName() { return lastName; }
    public void setLastName(String lastName) { this.lastName = lastName; }

    public String getRole() { return role; }
    public void setRole(String role) { this.role = role; }

    public BigDecimal getHourlyRate() { return hourlyRate; }
    public void setHourlyRate(BigDecimal hourlyRate) { this.hourlyRate = hourlyRate; }

    public String getNotes() { return notes; }
    public void setNotes(String notes) { this.notes = notes; }
  }
}