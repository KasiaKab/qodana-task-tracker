package com.kasiakab.tasktracker.model;

import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;
import java.util.Objects;

@Setter
@Getter
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
        return name.length() <= 50;
    }

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
