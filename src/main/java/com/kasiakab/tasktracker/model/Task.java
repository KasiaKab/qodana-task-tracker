package com.kasiakab.tasktracker.model;

import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;
import java.util.List;


@Setter
@Getter
@Document(collection = "tasks")
public class Task {

    @Id
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
    private String createdBy;
    private String notes;

    public Task() {
    }

    public Task(String title, String description, String status, String priority) {
        this.title = title;
        this.description = description;
        this.status = status;
        this.priority = priority;
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
    }

    public boolean isOverdue() {
        if (dueDate == null) {
            return false;
        }
        if (status != null && status.equals("DONE")) {
            return false;
        }
        return dueDate.isBefore(LocalDateTime.now());
    }

    public String getFullDescription() {
        return title + " - " + description + " [" + status + "]";
    }

    @Override
    public String toString() {
        return "Task{" +
                "id='" + id + '\'' +
                ", title='" + title + '\'' +
                ", status='" + status + '\'' +
                '}';
    }
}
