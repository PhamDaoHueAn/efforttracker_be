package com.efforttracker.model.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;
import java.math.BigDecimal;
import java.time.*;

@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
@Entity @Table(name = "time_entries")
public class TimeEntry {
  @Id
  @Column(name = "id", nullable = false, updatable = false)
  private String id; // UUID dạng String

  @ManyToOne(optional = false)
  @JoinColumn(name = "user_id")
  private User user;

  @Column(name = "date", nullable = false)
  private LocalDate date;

  @Column(name = "hours", nullable = false, precision = 5, scale = 2)
  private BigDecimal hours;

  @Column(name = "description", nullable = false)
  private String description;

  @Column(name = "earnings", nullable = false, precision = 10, scale = 2)
  private BigDecimal earnings;

  @CreationTimestamp
  @Column(name = "created_at")
  private OffsetDateTime createdAt;

  @UpdateTimestamp
  @Column(name = "updated_at")
  private OffsetDateTime updatedAt;
}