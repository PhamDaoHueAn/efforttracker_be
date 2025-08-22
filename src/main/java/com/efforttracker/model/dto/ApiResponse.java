package com.efforttracker.model.dto;

import lombok.*;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ApiResponse<T> {
    private boolean success;
    private String message;
    private T data; // thêm data để chứa kết quả trả về
}
