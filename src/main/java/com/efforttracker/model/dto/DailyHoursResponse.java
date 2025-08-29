package com.efforttracker.model.dto;

import java.math.BigDecimal;
import java.time.LocalDate;

public class DailyHoursResponse {
    private LocalDate date;
    private BigDecimal hours;

    public DailyHoursResponse(LocalDate date, BigDecimal hours) {
        this.date = date;
        this.hours = hours;
    }

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
}
