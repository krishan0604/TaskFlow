package com.taskapp.taskservice.dto;

import com.taskapp.taskservice.entity.TaskStatus;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record CreateTaskRequest(
        @NotBlank(message = "Title is required")
        @Size(max = 255, message = "Title must be at most 255 characters")
        String title,

        @Size(max = 1000, message = "Description must be at most 1000 characters")
        String description,

        TaskStatus status
) {
    public CreateTaskRequest {
        if (status == null) {
            status = TaskStatus.PENDING;
        }
    }
}
