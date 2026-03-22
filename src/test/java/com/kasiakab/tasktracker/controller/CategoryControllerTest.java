package com.kasiakab.tasktracker.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.kasiakab.tasktracker.dto.CategoryRequest;
import com.kasiakab.tasktracker.model.Category;
import com.kasiakab.tasktracker.service.CategoryService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(CategoryController.class)
class CategoryControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private CategoryService categoryService;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void createCategory_shouldReturn201() throws Exception {
        CategoryRequest request = new CategoryRequest();
        request.setName("Work");
        request.setColor("#FF0000");

        Category category = new Category("Work", "#FF0000");
        category.setId("cat-1");

        when(categoryService.createCategory(any(CategoryRequest.class))).thenReturn(category);

        MvcResult result = mockMvc.perform(post("/api/categories")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andReturn();

        Category response = objectMapper.readValue(result.getResponse().getContentAsString(), Category.class);
        assertThat(response.getId()).isEqualTo("cat-1");
        assertThat(response.getName()).isEqualTo("Work");
        assertThat(response.getColor()).isEqualTo("#FF0000");
    }

    @Test
    void createCategory_withoutName_shouldReturn400() throws Exception {
        CategoryRequest request = new CategoryRequest();

        MvcResult result = mockMvc.perform(post("/api/categories")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andReturn();

        assertThat(result.getResponse().getStatus()).isEqualTo(400);
    }

    @Test
    void getAllCategories_shouldReturnList() throws Exception {
        Category cat1 = new Category("Work", "#FF0000");
        cat1.setId("cat-1");

        Category cat2 = new Category("Personal", "#00FF00");
        cat2.setId("cat-2");

        when(categoryService.getAllCategories()).thenReturn(List.of(cat1, cat2));

        MvcResult result = mockMvc.perform(get("/api/categories"))
                .andExpect(status().isOk())
                .andReturn();

        List<?> categories = objectMapper.readValue(result.getResponse().getContentAsString(), List.class);
        assertThat(categories).hasSize(2);
    }

    @Test
    void getCategoryById_shouldReturnCategory() throws Exception {
        Category category = new Category("Work", "#FF0000");
        category.setId("cat-1");

        when(categoryService.getCategoryById("cat-1")).thenReturn(category);

        MvcResult result = mockMvc.perform(get("/api/categories/cat-1"))
                .andExpect(status().isOk())
                .andReturn();

        Category response = objectMapper.readValue(result.getResponse().getContentAsString(), Category.class);
        assertThat(response.getId()).isEqualTo("cat-1");
        assertThat(response.getName()).isEqualTo("Work");
    }

    @Test
    void updateCategory_shouldReturnUpdatedCategory() throws Exception {
        CategoryRequest request = new CategoryRequest();
        request.setName("Updated Work");
        request.setColor("#0000FF");

        Category updated = new Category("Updated Work", "#0000FF");
        updated.setId("cat-1");

        when(categoryService.updateCategory(eq("cat-1"), any(CategoryRequest.class))).thenReturn(updated);

        MvcResult result = mockMvc.perform(put("/api/categories/cat-1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andReturn();

        Category response = objectMapper.readValue(result.getResponse().getContentAsString(), Category.class);
        assertThat(response.getId()).isEqualTo("cat-1");
        assertThat(response.getName()).isEqualTo("Updated Work");
        assertThat(response.getColor()).isEqualTo("#0000FF");
    }

    @Test
    void deleteCategory_shouldReturn204() throws Exception {
        doNothing().when(categoryService).deleteCategory("cat-1");

        MvcResult result = mockMvc.perform(delete("/api/categories/cat-1"))
                .andExpect(status().isNoContent())
                .andReturn();

        assertThat(result.getResponse().getStatus()).isEqualTo(204);
    }

}
