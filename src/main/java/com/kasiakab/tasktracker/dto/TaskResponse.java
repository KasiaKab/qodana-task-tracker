package com.kasiakab.tasktracker.dto;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.List;

@Setter
@Getter
public class TaskResponse {

    private String id;
    private String title;
    private String description;
    private String status;
    private String priority;
    private String assignee;
    private String category;
    private List<String> tags;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private LocalDateTime dueDate;
    private boolean overdue;

    public TaskResponse() {
    }

}
