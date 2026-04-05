package com.taskapp.taskservice.controller;

import com.taskapp.taskservice.dto.CreateTaskRequest;
import com.taskapp.taskservice.dto.TaskResponse;
import com.taskapp.taskservice.dto.UpdateTaskRequest;
import com.taskapp.taskservice.security.CustomUserDetails;
import com.taskapp.taskservice.service.TaskService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/tasks")
@RequiredArgsConstructor
public class TaskController {

    private final TaskService taskService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public TaskResponse createTask(
            @Valid @RequestBody CreateTaskRequest request,
            @AuthenticationPrincipal CustomUserDetails userDetails
    ) {
        return taskService.createTask(request, userDetails);
    }

    @GetMapping
    public List<TaskResponse> getAllTasks(@AuthenticationPrincipal CustomUserDetails userDetails) {
        return taskService.getAllTasks(userDetails);
    }

    @GetMapping("/{id}")
    public TaskResponse getTask(
            @PathVariable UUID id,
            @AuthenticationPrincipal CustomUserDetails userDetails
    ) {
        return taskService.getTask(id, userDetails);
    }

    @PutMapping("/{id}")
    public TaskResponse updateTask(
            @PathVariable UUID id,
            @Valid @RequestBody UpdateTaskRequest request,
            @AuthenticationPrincipal CustomUserDetails userDetails
    ) {
        return taskService.updateTask(id, request, userDetails);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteTask(
            @PathVariable UUID id,
            @AuthenticationPrincipal CustomUserDetails userDetails
    ) {
        taskService.deleteTask(id, userDetails);
    }
}
