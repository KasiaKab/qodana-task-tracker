package com.kasiakab.tasktracker.model;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;
import java.util.Objects;

@Document(collection = "categories")
public class Category {

    @Id
    private String id;
    private String name;
    private String color;
    private String description;
    private LocalDateTime createdAt;
    private int taskCount;

    public Category() {
    }

    public Category(String name, String color) {
        this.name = name;
        this.color = color;
        this.createdAt = LocalDateTime.now();
        this.taskCount = 0;
    }

    public boolean isValid() {
        if (name == null || name.trim().isEmpty()) {
            return false;
        }
        if (name.length() > 50) {
            return false;
        }
        return true;
    }

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getColor() { return color; }
    public void setColor(String color) { this.color = color; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }

    public int getTaskCount() { return taskCount; }
    public void setTaskCount(int taskCount) { this.taskCount = taskCount; }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Category category = (Category) o;
        return Objects.equals(id, category.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, name);
    }
}
