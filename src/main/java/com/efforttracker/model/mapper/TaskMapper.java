package com.efforttracker.model.mapper;

import com.efforttracker.model.dto.TaskDtos;
import com.efforttracker.model.entity.Task;

public class TaskMapper {

    public static TaskDtos.TaskResponse toTaskResponse(Task task) {
        if (task == null) return null;
        return new TaskDtos.TaskResponse(
                task.getId(),
                task.getName(),
                task.getDescription(),
                task.getStatus(),
                task.getStartDate(),
                task.getDueDate(),
                task.getCreatedAt(),
                task.getUpdatedAt()
        );
    }
}
