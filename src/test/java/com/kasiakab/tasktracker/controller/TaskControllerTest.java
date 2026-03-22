package com.kasiakab.tasktracker.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.kasiakab.tasktracker.dto.TaskRequest;
import com.kasiakab.tasktracker.dto.TaskResponse;
import com.kasiakab.tasktracker.service.TaskService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.bean.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Arrays;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
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

        mockMvc.perform(post("/api/tasks")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value("abc-123"))
                .andExpect(jsonPath("$.title").value("New Task"));
    }

    @Test
    void createTask_withoutTitle_shouldReturn400() throws Exception {
        TaskRequest request = new TaskRequest();

        mockMvc.perform(post("/api/tasks")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
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

        mockMvc.perform(get("/api/tasks"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(2));
    }

    @Test
    void getTaskById_shouldReturnTask() throws Exception {
        TaskResponse response = new TaskResponse();
        response.setId("abc-123");
        response.setTitle("Found Task");

        when(taskService.getTaskById("abc-123")).thenReturn(response);

        mockMvc.perform(get("/api/tasks/abc-123"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.title").value("Found Task"));
    }

}
