package com.kasiakab.tasktracker.repository;

import com.kasiakab.tasktracker.model.Task;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface TaskRepository extends MongoRepository<Task, String> {

    List<Task> findByStatus(String status);

    List<Task> findByPriority(String priority);

    List<Task> findByAssignee(String assignee);

    List<Task> findByCategory(String category);

    List<Task> findByStatusAndPriority(String status, String priority);

    List<Task> findByCreatedAtBetween(LocalDateTime start, LocalDateTime end);

    List<Task> findByTagsContaining(String tag);

    List<Task> findByDueDateBefore(LocalDateTime date);

    long countByStatus(String status);
}
