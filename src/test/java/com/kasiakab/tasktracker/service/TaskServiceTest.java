package com.kasiakab.tasktracker.service;

import com.kasiakab.tasktracker.dto.TaskRequest;
import com.kasiakab.tasktracker.dto.TaskResponse;
import com.kasiakab.tasktracker.model.Task;
import com.kasiakab.tasktracker.repository.TaskRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class TaskServiceTest {

    @Mock
    private TaskRepository taskRepository;

    @InjectMocks
    private TaskService taskService;

    private Task sampleTask;
    private TaskRequest sampleRequest;

    @BeforeEach
    void setUp() {
        sampleTask = new Task("Test Task", "Test Description", "TODO", "MEDIUM");
        sampleTask.setId("test-id-123");
        sampleTask.setCreatedAt(LocalDateTime.now());
        sampleTask.setUpdatedAt(LocalDateTime.now());

        sampleRequest = new TaskRequest();
        sampleRequest.setTitle("Test Task");
        sampleRequest.setDescription("Test Description");
    }

    @Test
    void createTask_shouldCreateAndReturnTask() {
        when(taskRepository.save(any(Task.class))).thenReturn(sampleTask);

        TaskResponse response = taskService.createTask(sampleRequest);

        assertThat(response).isNotNull();
        assertThat(response.getTitle()).isEqualTo("Test Task");
        assertThat(response.getStatus()).isEqualTo("TODO");
        verify(taskRepository, times(1)).save(any(Task.class));
    }

    @Test
    void createTask_withNullStatus_shouldDefaultToTodo() {
        sampleRequest.setStatus(null);
        when(taskRepository.save(any(Task.class))).thenReturn(sampleTask);

        TaskResponse response = taskService.createTask(sampleRequest);

        assertThat(response.getStatus()).isEqualTo("TODO");
    }

    @Test
    void getAllTasks_shouldReturnAllTasks() {
        Task task2 = new Task("Task 2", "Desc 2", "IN_PROGRESS", "HIGH");
        task2.setId("test-id-456");
        when(taskRepository.findAll()).thenReturn(Arrays.asList(sampleTask, task2));

        List<TaskResponse> responses = taskService.getAllTasks();

        assertThat(responses).hasSize(2);
    }

    @Test
    void getTaskById_shouldReturnTask() {
        when(taskRepository.findById("test-id-123")).thenReturn(Optional.of(sampleTask));

        TaskResponse response = taskService.getTaskById("test-id-123");

        assertThat(response).isNotNull();
        assertThat(response.getId()).isEqualTo("test-id-123");
    }

    @Test
    void getTaskById_notFound_shouldThrowException() {
        when(taskRepository.findById("nonexistent")).thenReturn(Optional.empty());

        assertThatThrownBy(() -> taskService.getTaskById("nonexistent"))
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("Task not found");
    }

    @Test
    void deleteTask_shouldCallRepository() {
        taskService.deleteTask("test-id-123");

        verify(taskRepository, times(1)).deleteById("test-id-123");
    }

}
