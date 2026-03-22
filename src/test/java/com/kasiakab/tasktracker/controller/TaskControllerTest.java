package com.kasiakab.tasktracker.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.kasiakab.tasktracker.dto.TaskRequest;
import com.kasiakab.tasktracker.dto.TaskResponse;
import com.kasiakab.tasktracker.service.TaskService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(TaskController.class)
class TaskControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private TaskService taskService;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void createTask_shouldReturn201() throws Exception {
        TaskRequest request = new TaskRequest();
        request.setTitle("New Task");
        request.setDescription("Description");

        TaskResponse response = new TaskResponse();
        response.setId("abc-123");
        response.setTitle("New Task");
        response.setStatus("TODO");

        when(taskService.createTask(any(TaskRequest.class))).thenReturn(response);

        MvcResult result = mockMvc.perform(post("/api/tasks")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andReturn();

        TaskResponse taskResponse = objectMapper.readValue(result.getResponse().getContentAsString(), TaskResponse.class);
        assertThat(taskResponse.getId()).isEqualTo("abc-123");
        assertThat(taskResponse.getTitle()).isEqualTo("New Task");
        assertThat(taskResponse.getStatus()).isEqualTo("TODO");
    }

    @Test
    void createTask_withoutTitle_shouldReturn400() throws Exception {
        TaskRequest request = new TaskRequest();

        MvcResult result = mockMvc.perform(post("/api/tasks")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andReturn();

        assertThat(result.getResponse().getStatus()).isEqualTo(400);
    }

    @Test
    void getAllTasks_shouldReturnList() throws Exception {
        TaskResponse task1 = new TaskResponse();
        task1.setId("1");
        task1.setTitle("Task 1");

        TaskResponse task2 = new TaskResponse();
        task2.setId("2");
        task2.setTitle("Task 2");

        when(taskService.getAllTasks()).thenReturn(Arrays.asList(task1, task2));

        MvcResult result = mockMvc.perform(get("/api/tasks"))
                .andExpect(status().isOk())
                .andReturn();

        List<?> tasks = objectMapper.readValue(result.getResponse().getContentAsString(), List.class);
        assertThat(tasks).hasSize(2);
    }

    @Test
    void getTaskById_shouldReturnTask() throws Exception {
        TaskResponse response = new TaskResponse();
        response.setId("abc-123");
        response.setTitle("Found Task");

        when(taskService.getTaskById("abc-123")).thenReturn(response);

        MvcResult result = mockMvc.perform(get("/api/tasks/abc-123"))
                .andExpect(status().isOk())
                .andReturn();

        TaskResponse taskResponse = objectMapper.readValue(result.getResponse().getContentAsString(), TaskResponse.class);
        assertThat(taskResponse.getId()).isEqualTo("abc-123");
        assertThat(taskResponse.getTitle()).isEqualTo("Found Task");
    }

    @Test
    void updateTask_shouldReturnUpdatedTask() throws Exception {
        TaskRequest request = new TaskRequest();
        request.setTitle("Updated Task");
        request.setStatus("IN_PROGRESS");

        TaskResponse response = new TaskResponse();
        response.setId("abc-123");
        response.setTitle("Updated Task");
        response.setStatus("IN_PROGRESS");

        when(taskService.updateTask(eq("abc-123"), any(TaskRequest.class))).thenReturn(response);

        MvcResult result = mockMvc.perform(put("/api/tasks/abc-123")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andReturn();

        TaskResponse taskResponse = objectMapper.readValue(result.getResponse().getContentAsString(), TaskResponse.class);
        assertThat(taskResponse.getId()).isEqualTo("abc-123");
        assertThat(taskResponse.getTitle()).isEqualTo("Updated Task");
        assertThat(taskResponse.getStatus()).isEqualTo("IN_PROGRESS");
    }

    @Test
    void patchTask_shouldReturnPatchedTask() throws Exception {
        TaskRequest request = new TaskRequest();
        request.setTitle("Patched Title");

        TaskResponse response = new TaskResponse();
        response.setId("abc-123");
        response.setTitle("Patched Title");
        response.setStatus("TODO");

        when(taskService.patchTask(eq("abc-123"), any(TaskRequest.class))).thenReturn(response);

        MvcResult result = mockMvc.perform(patch("/api/tasks/abc-123")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andReturn();

        TaskResponse taskResponse = objectMapper.readValue(result.getResponse().getContentAsString(), TaskResponse.class);
        assertThat(taskResponse.getId()).isEqualTo("abc-123");
        assertThat(taskResponse.getTitle()).isEqualTo("Patched Title");
    }

    @Test
    void deleteTask_shouldReturn204() throws Exception {
        doNothing().when(taskService).deleteTask("abc-123");

        MvcResult result = mockMvc.perform(delete("/api/tasks/abc-123"))
                .andExpect(status().isNoContent())
                .andReturn();

        assertThat(result.getResponse().getStatus()).isEqualTo(204);
    }

    @Test
    void getByStatus_shouldReturnFilteredTasks() throws Exception {
        TaskResponse task = new TaskResponse();
        task.setId("1");
        task.setTitle("Todo Task");
        task.setStatus("TODO");

        when(taskService.getTasksByStatus("TODO")).thenReturn(List.of(task));

        MvcResult result = mockMvc.perform(get("/api/tasks/by-status/TODO"))
                .andExpect(status().isOk())
                .andReturn();

        List<?> tasks = objectMapper.readValue(result.getResponse().getContentAsString(), List.class);
        assertThat(tasks).hasSize(1);
    }

    @Test
    void getByPriority_shouldReturnFilteredTasks() throws Exception {
        TaskResponse task = new TaskResponse();
        task.setId("1");
        task.setTitle("High Priority Task");
        task.setPriority("HIGH");

        when(taskService.getTasksByPriority("HIGH")).thenReturn(List.of(task));

        MvcResult result = mockMvc.perform(get("/api/tasks/byPriority/HIGH"))
                .andExpect(status().isOk())
                .andReturn();

        List<?> tasks = objectMapper.readValue(result.getResponse().getContentAsString(), List.class);
        assertThat(tasks).hasSize(1);
    }

    @Test
    void getOverdueTasks_shouldReturnOverdueTasks() throws Exception {
        TaskResponse task = new TaskResponse();
        task.setId("1");
        task.setTitle("Overdue Task");
        task.setOverdue(true);

        when(taskService.getOverdueTasks()).thenReturn(List.of(task));

        MvcResult result = mockMvc.perform(get("/api/tasks/overdue"))
                .andExpect(status().isOk())
                .andReturn();

        List<?> tasks = objectMapper.readValue(result.getResponse().getContentAsString(), List.class);
        assertThat(tasks).hasSize(1);
    }

    @Test
    void getStats_shouldReturnTaskStats() throws Exception {
        Map<String, Long> stats = Map.of(
                "todo", 2L,
                "in_progress", 1L,
                "done", 3L,
                "cancelled", 0L,
                "total", 6L
        );

        when(taskService.getTaskStats()).thenReturn(stats);

        MvcResult result = mockMvc.perform(get("/api/tasks/stats"))
                .andExpect(status().isOk())
                .andReturn();

        @SuppressWarnings("unchecked")
        Map<String, Object> responseStats = objectMapper.readValue(result.getResponse().getContentAsString(), Map.class);
        assertThat(responseStats).containsKey("todo");
        assertThat(responseStats).containsKey("total");
        assertThat(((Number) responseStats.get("total")).longValue()).isEqualTo(6L);
    }

}
