package com.taskapp.taskservice.service;

import com.taskapp.taskservice.dto.CreateTaskRequest;
import com.taskapp.taskservice.dto.TaskResponse;
import com.taskapp.taskservice.dto.UpdateTaskRequest;
import com.taskapp.taskservice.entity.Task;
import com.taskapp.taskservice.repository.TaskRepository;
import com.taskapp.taskservice.security.CustomUserDetails;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class TaskService {

    private final TaskRepository taskRepository;

    private TaskResponse mapToResponse(Task task) {
        return new TaskResponse(
                task.getId(),
                task.getTitle(),
                task.getDescription(),
                task.getStatus(),
                task.getCreatedBy(),
                task.getCreatedByUsername(),
                task.getCreatedAt(),
                task.getUpdatedAt(),
                task.getVersion()
        );
    }

    @Transactional
    public TaskResponse createTask(CreateTaskRequest request, CustomUserDetails userDetails) {
        LocalDateTime now = LocalDateTime.now();
        Task task = Task.builder()
                .title(request.title())
                .description(request.description())
                .status(request.status())
                .createdBy(UUID.fromString(userDetails.getUserId()))
                .createdByUsername(userDetails.getUsername())
                .createdAt(now)
                .updatedAt(now)
                .build();
        return mapToResponse(taskRepository.save(task));
    }

    public List<TaskResponse> getAllTasks(CustomUserDetails userDetails) {
        List<Task> tasks;
        if ("ADMIN".equals(userDetails.getRole())) {
            tasks = taskRepository.findAll();
        } else {
            tasks = taskRepository.findByCreatedBy(UUID.fromString(userDetails.getUserId()));
        }
        return tasks.stream().map(this::mapToResponse).collect(Collectors.toList());
    }

    public TaskResponse getTask(UUID id, CustomUserDetails userDetails) {
        Task task = taskRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Task not found"));
        checkOwnership(task, userDetails);
        return mapToResponse(task);
    }

    @Transactional
    public TaskResponse updateTask(UUID id, UpdateTaskRequest request, CustomUserDetails userDetails) {
        Task task = taskRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Task not found"));
        checkOwnership(task, userDetails);

        if (request.title() != null && !request.title().isBlank()) {
            task.setTitle(request.title());
        }
        if (request.description() != null) {
            task.setDescription(request.description());
        }
        if (request.status() != null) {
            task.setStatus(request.status());
        }
        task.setUpdatedAt(LocalDateTime.now());

        return mapToResponse(taskRepository.save(task));
    }

    @Transactional
    public void deleteTask(UUID id, CustomUserDetails userDetails) {
        Task task = taskRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Task not found"));
        checkOwnership(task, userDetails);
        taskRepository.delete(task);
    }

    private void checkOwnership(Task task, CustomUserDetails userDetails) {
        if (!"ADMIN".equals(userDetails.getRole())) {
            if (!task.getCreatedBy().toString().equals(userDetails.getUserId())) {
                throw new AccessDeniedException("You do not have permission to access this task");
            }
        }
    }
}