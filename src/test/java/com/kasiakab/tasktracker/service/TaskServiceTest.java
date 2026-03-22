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
import java.util.Map;
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
    void createTask_withNullPriority_shouldDefaultToMedium() {
        TaskRequest requestWithNullPriority = new TaskRequest();
        requestWithNullPriority.setTitle("Test Task");
        requestWithNullPriority.setPriority(null);
        when(taskRepository.save(any(Task.class))).thenReturn(sampleTask);

        TaskResponse response = taskService.createTask(requestWithNullPriority);

        assertThat(response.getPriority()).isEqualTo("MEDIUM");
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
    void updateTask_shouldUpdateAndReturnTask() {
        TaskRequest updateRequest = new TaskRequest();
        updateRequest.setTitle("Updated Title");
        updateRequest.setStatus("IN_PROGRESS");
        updateRequest.setPriority("HIGH");

        Task updatedTask = new Task("Updated Title", "Test Description", "IN_PROGRESS", "HIGH");
        updatedTask.setId("test-id-123");
        updatedTask.setCreatedAt(sampleTask.getCreatedAt());
        updatedTask.setUpdatedAt(LocalDateTime.now());

        when(taskRepository.findById("test-id-123")).thenReturn(Optional.of(sampleTask));
        when(taskRepository.save(any(Task.class))).thenReturn(updatedTask);

        TaskResponse response = taskService.updateTask("test-id-123", updateRequest);

        assertThat(response).isNotNull();
        assertThat(response.getTitle()).isEqualTo("Updated Title");
        assertThat(response.getStatus()).isEqualTo("IN_PROGRESS");
        assertThat(response.getPriority()).isEqualTo("HIGH");
        verify(taskRepository, times(1)).save(any(Task.class));
    }

    @Test
    void updateTask_notFound_shouldThrowException() {
        when(taskRepository.findById("nonexistent")).thenReturn(Optional.empty());

        assertThatThrownBy(() -> taskService.updateTask("nonexistent", sampleRequest))
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("Task not found");
    }

    @Test
    void patchTask_shouldUpdateOnlyProvidedFields() {
        TaskRequest patchRequest = new TaskRequest();
        patchRequest.setStatus("DONE");

        Task patchedTask = new Task("Test Task", "Test Description", "DONE", "MEDIUM");
        patchedTask.setId("test-id-123");
        patchedTask.setCreatedAt(sampleTask.getCreatedAt());
        patchedTask.setUpdatedAt(LocalDateTime.now());

        when(taskRepository.findById("test-id-123")).thenReturn(Optional.of(sampleTask));
        when(taskRepository.save(any(Task.class))).thenReturn(patchedTask);

        TaskResponse response = taskService.patchTask("test-id-123", patchRequest);

        assertThat(response).isNotNull();
        assertThat(response.getStatus()).isEqualTo("DONE");
        assertThat(response.getTitle()).isEqualTo("Test Task");
        verify(taskRepository, times(1)).save(any(Task.class));
    }

    @Test
    void patchTask_withInvalidStatus_shouldThrowException() {
        TaskRequest patchRequest = new TaskRequest();
        patchRequest.setStatus("INVALID_STATUS");

        when(taskRepository.findById("test-id-123")).thenReturn(Optional.of(sampleTask));

        assertThatThrownBy(() -> taskService.patchTask("test-id-123", patchRequest))
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("Invalid status");
    }

    @Test
    void patchTask_withInvalidPriority_shouldThrowException() {
        TaskRequest patchRequest = new TaskRequest();
        patchRequest.setPriority("SUPER_HIGH");

        when(taskRepository.findById("test-id-123")).thenReturn(Optional.of(sampleTask));

        assertThatThrownBy(() -> taskService.patchTask("test-id-123", patchRequest))
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("Invalid priority");
    }

    @Test
    void deleteTask_shouldCallRepository() {
        taskService.deleteTask("test-id-123");

        verify(taskRepository, times(1)).deleteById("test-id-123");
    }

    @Test
    void getTasksByStatus_shouldReturnFilteredTasks() {
        Task inProgressTask = new Task("In Progress Task", "Description", "IN_PROGRESS", "MEDIUM");
        inProgressTask.setId("test-id-456");

        when(taskRepository.findByStatus("IN_PROGRESS")).thenReturn(List.of(inProgressTask));

        List<TaskResponse> responses = taskService.getTasksByStatus("IN_PROGRESS");

        assertThat(responses).hasSize(1);
        assertThat(responses.get(0).getStatus()).isEqualTo("IN_PROGRESS");
    }

    @Test
    void getTasksByPriority_shouldReturnFilteredTasks() {
        Task highPriorityTask = new Task("High Priority Task", "Description", "TODO", "HIGH");
        highPriorityTask.setId("test-id-789");

        when(taskRepository.findByPriority("HIGH")).thenReturn(List.of(highPriorityTask));

        List<TaskResponse> responses = taskService.getTasksByPriority("HIGH");

        assertThat(responses).hasSize(1);
        assertThat(responses.get(0).getPriority()).isEqualTo("HIGH");
    }

    @Test
    void getOverdueTasks_shouldReturnTasksPastDueDate() {
        Task overdueTask = new Task("Overdue Task", "Description", "TODO", "HIGH");
        overdueTask.setId("test-id-overdue");
        overdueTask.setDueDate(LocalDateTime.now().minusDays(1));

        Task doneTask = new Task("Done Task", "Description", "DONE", "LOW");
        doneTask.setId("test-id-done");
        doneTask.setDueDate(LocalDateTime.now().minusDays(1));

        when(taskRepository.findByDueDateBefore(any(LocalDateTime.class)))
                .thenReturn(Arrays.asList(overdueTask, doneTask));

        List<TaskResponse> responses = taskService.getOverdueTasks();

        assertThat(responses).hasSize(1);
        assertThat(responses.get(0).getId()).isEqualTo("test-id-overdue");
    }

    @Test
    void getTaskStats_shouldReturnCorrectCounts() {
        Task todoTask = new Task("Todo", "Desc", "TODO", "MEDIUM");
        Task inProgressTask = new Task("In Progress", "Desc", "IN_PROGRESS", "HIGH");
        Task doneTask1 = new Task("Done 1", "Desc", "DONE", "LOW");
        Task doneTask2 = new Task("Done 2", "Desc", "DONE", "LOW");
        Task cancelledTask = new Task("Cancelled", "Desc", "CANCELLED", "MEDIUM");

        when(taskRepository.findAll()).thenReturn(
                Arrays.asList(todoTask, inProgressTask, doneTask1, doneTask2, cancelledTask)
        );

        Map<String, Long> stats = taskService.getTaskStats();

        assertThat(stats).isNotNull();
        assertThat(stats.get("todo")).isEqualTo(1L);
        assertThat(stats.get("in_progress")).isEqualTo(1L);
        assertThat(stats.get("done")).isEqualTo(2L);
        assertThat(stats.get("cancelled")).isEqualTo(1L);
        assertThat(stats.get("total")).isEqualTo(5L);
    }

}
