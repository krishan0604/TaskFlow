package com.taskapp.taskservice.dto;

import com.taskapp.taskservice.entity.TaskStatus;
import java.time.LocalDateTime;
import java.util.UUID;

public record TaskResponse(
        UUID id,
        String title,
        String description,
        TaskStatus status,
        UUID createdBy,
        String createdByUsername,
        LocalDateTime createdAt,
        LocalDateTime updatedAt,
        Long version
) {}
