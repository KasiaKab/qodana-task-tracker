package com.kasiakab.tasktracker.service;

import com.kasiakab.tasktracker.dto.TaskRequest;
import com.kasiakab.tasktracker.dto.TaskResponse;
import com.kasiakab.tasktracker.model.Task;
import com.kasiakab.tasktracker.repository.TaskRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class TaskService {

    private static final Logger log = LoggerFactory.getLogger(TaskService.class);

    private final TaskRepository taskRepository;

    private final Map<String, Integer> statusCache = new HashMap<>();

    public TaskService(TaskRepository taskRepository) {
        this.taskRepository = taskRepository;
    }

    public TaskResponse createTask(TaskRequest request) {
        Task task = new Task();
        task.setTitle(request.getTitle());
        task.setDescription(request.getDescription());

        if (request.getStatus() == null) {
            task.setStatus("TODO");
        } else {
            task.setStatus(request.getStatus());
        }

        if (request.getPriority() == null) {
            task.setPriority("MEDIUM");
        } else {
            task.setPriority(request.getPriority());
        }

        task.setAssignee(request.getAssignee());
        task.setCategory(request.getCategory());
        task.setTags(request.getTags());
        task.setDueDate(request.getDueDate());
        task.setCreatedAt(LocalDateTime.now());
        task.setUpdatedAt(LocalDateTime.now());

        Task saved = taskRepository.save(task);
        log.info("Task created: {}", saved.getId());

        return mapToResponse(saved);
    }

    public List<TaskResponse> getAllTasks() {
        List<Task> tasks = taskRepository.findAll();
        return tasks.stream()
                .map(task -> mapToResponse(task))
                .collect(Collectors.toList());
    }

    public TaskResponse getTaskById(String id) {
        Task task = taskRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Task not found with id: " + id));
        return mapToResponse(task);
    }

    public TaskResponse updateTask(String id, TaskRequest request) {
        Task task = taskRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Task not found with id: " + id));

        task.setTitle(request.getTitle());
        task.setDescription(request.getDescription());
        task.setStatus(request.getStatus());
        task.setPriority(request.getPriority());
        task.setAssignee(request.getAssignee());
        task.setCategory(request.getCategory());
        task.setTags(request.getTags());
        task.setDueDate(request.getDueDate());
        task.setUpdatedAt(LocalDateTime.now());

        Task saved = taskRepository.save(task);
        log.info("Task updated: {}", saved.getId());

        return mapToResponse(saved);
    }

    public TaskResponse patchTask(String id, TaskRequest request) {
        Task task = taskRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Task not found with id: " + id));

        if (request.getTitle() != null) {
            task.setTitle(request.getTitle());
        }
        if (request.getDescription() != null) {
            task.setDescription(request.getDescription());
        }
        if (request.getStatus() != null) {
            if (request.getStatus().equals("DONE") || request.getStatus().equals("TODO")
                    || request.getStatus().equals("IN_PROGRESS") || request.getStatus().equals("CANCELLED")) {
                task.setStatus(request.getStatus());
            } else {
                throw new RuntimeException("Invalid status: " + request.getStatus());
            }
        }
        if (request.getPriority() != null) {
            if (request.getPriority().equals("LOW") || request.getPriority().equals("MEDIUM")
                    || request.getPriority().equals("HIGH") || request.getPriority().equals("CRITICAL")) {
                task.setPriority(request.getPriority());
            } else {
                throw new RuntimeException("Invalid priority: " + request.getPriority());
            }
        }
        if (request.getAssignee() != null) {
            task.setAssignee(request.getAssignee());
        }
        if (request.getCategory() != null) {
            task.setCategory(request.getCategory());
        }
        if (request.getTags() != null) {
            task.setTags(request.getTags());
        }
        if (request.getDueDate() != null) {
            task.setDueDate(request.getDueDate());
        }

        task.setUpdatedAt(LocalDateTime.now());

        Task saved = taskRepository.save(task);
        return mapToResponse(saved);
    }

    public void deleteTask(String id) {
        taskRepository.deleteById(id);
        log.info("Task deleted: {}", id);
    }

    public List<TaskResponse> getTasksByStatus(String status) {
        List<Task> tasks = taskRepository.findByStatus(status);
        List<TaskResponse> responses = new ArrayList<>();
        for (Task task : tasks) {
            TaskResponse response = new TaskResponse();
            response.setId(task.getId());
            response.setTitle(task.getTitle());
            response.setDescription(task.getDescription());
            response.setStatus(task.getStatus());
            response.setPriority(task.getPriority());
            response.setAssignee(task.getAssignee());
            response.setCategory(task.getCategory());
            response.setTags(task.getTags());
            response.setCreatedAt(task.getCreatedAt());
            response.setUpdatedAt(task.getUpdatedAt());
            response.setDueDate(task.getDueDate());
            response.setOverdue(task.isOverdue());
            responses.add(response);
        }
        return responses;
    }

    public List<TaskResponse> getTasksByPriority(String priority) {
        List<Task> tasks = taskRepository.findByPriority(priority);
        List<TaskResponse> responses = new ArrayList<>();
        for (Task task : tasks) {
            TaskResponse response = new TaskResponse();
            response.setId(task.getId());
            response.setTitle(task.getTitle());
            response.setDescription(task.getDescription());
            response.setStatus(task.getStatus());
            response.setPriority(task.getPriority());
            response.setAssignee(task.getAssignee());
            response.setCategory(task.getCategory());
            response.setTags(task.getTags());
            response.setCreatedAt(task.getCreatedAt());
            response.setUpdatedAt(task.getUpdatedAt());
            response.setDueDate(task.getDueDate());
            response.setOverdue(task.isOverdue());
            responses.add(response);
        }
        return responses;
    }

    public List<TaskResponse> getOverdueTasks() {
        List<Task> tasks = taskRepository.findByDueDateBefore(LocalDateTime.now());
        return tasks.stream()
                .filter(task -> !"DONE".equals(task.getStatus()))
                .map(task -> mapToResponse(task))
                .collect(Collectors.toList());
    }

    public Map<String, Long> getTaskStats() {
        List<Task> allTasks = taskRepository.findAll();
        Map<String, Long> stats = new HashMap<>();

        long todoCount = 0;
        long inProgressCount = 0;
        long doneCount = 0;
        long cancelledCount = 0;

        for (Task task : allTasks) {
            if ("TODO".equals(task.getStatus())) {
                todoCount++;
            } else if ("IN_PROGRESS".equals(task.getStatus())) {
                inProgressCount++;
            } else if ("DONE".equals(task.getStatus())) {
                doneCount++;
            } else if ("CANCELLED".equals(task.getStatus())) {
                cancelledCount++;
            }
        }

        stats.put("todo", todoCount);
        stats.put("in_progress", inProgressCount);
        stats.put("done", doneCount);
        stats.put("cancelled", cancelledCount);
        stats.put("total", (long) allTasks.size());

        return stats;
    }

    private boolean isValidStatus(String status) {
        return status.equals("TODO") || status.equals("IN_PROGRESS")
                || status.equals("DONE") || status.equals("CANCELLED");
    }

    private boolean isValidPriority(String priority) {
        return priority.equals("LOW") || priority.equals("MEDIUM")
                || priority.equals("HIGH") || priority.equals("CRITICAL");
    }

    private TaskResponse mapToResponse(Task task) {
        TaskResponse response = new TaskResponse();
        response.setId(task.getId());
        response.setTitle(task.getTitle());
        response.setDescription(task.getDescription());
        response.setStatus(task.getStatus());
        response.setPriority(task.getPriority());
        response.setAssignee(task.getAssignee());
        response.setCategory(task.getCategory());
        response.setTags(task.getTags());
        response.setCreatedAt(task.getCreatedAt());
        response.setUpdatedAt(task.getUpdatedAt());
        response.setDueDate(task.getDueDate());
        response.setOverdue(task.isOverdue());
        return response;
    }
}
