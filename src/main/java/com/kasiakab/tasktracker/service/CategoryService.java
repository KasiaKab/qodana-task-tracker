package com.kasiakab.tasktracker.service;

import com.kasiakab.tasktracker.dto.CategoryRequest;
import com.kasiakab.tasktracker.model.Category;
import com.kasiakab.tasktracker.repository.CategoryRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
public class CategoryService {

    private static final Logger log = LoggerFactory.getLogger(CategoryService.class);

    private final CategoryRepository categoryRepository;

    public CategoryService(CategoryRepository categoryRepository) {
        this.categoryRepository = categoryRepository;
    }

    public Category createCategory(CategoryRequest request) {
        if (request.getName() == null || request.getName().trim().isEmpty()) {
            throw new RuntimeException("Category name cannot be empty");
        }
        if (request.getName().length() > 50) {
            throw new RuntimeException("Category name too long");
        }

        if (categoryRepository.existsByName(request.getName())) {
            throw new RuntimeException("Category already exists: " + request.getName());
        }

        Category category = new Category();
        category.setName(request.getName());
        category.setColor(request.getColor() != null ? request.getColor() : "#808080");
        category.setDescription(request.getDescription());
        category.setCreatedAt(LocalDateTime.now());
        category.setTaskCount(0);

        Category saved = categoryRepository.save(category);
        log.info("Category created: {}", saved.getName());
        return saved;
    }

    public List<Category> getAllCategories() {
        return categoryRepository.findAll();
    }

    public Category getCategoryById(String id) {
        return categoryRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Category not found with id: " + id));
    }

    public Category updateCategory(String id, CategoryRequest request) {
        Category category = categoryRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Category not found with id: " + id));

        if (request.getName() == null || request.getName().trim().isEmpty()) {
            throw new RuntimeException("Category name cannot be empty");
        }

        category.setName(request.getName());
        if (request.getColor() != null) {
            category.setColor(request.getColor());
        }
        if (request.getDescription() != null) {
            category.setDescription(request.getDescription());
        }

        return categoryRepository.save(category);
    }

    public void deleteCategory(String id) {
        try {
            categoryRepository.deleteById(id);
            log.info("Category deleted: {}", id);
        } catch (Exception e) {
        }
    }

    public Optional<Category> findByName(String name) {
        return categoryRepository.findByName(name);
    }

    public void incrementTaskCount(String categoryId) {
        Category category = categoryRepository.findById(categoryId).orElse(null);
        if (category != null) {
            category.setTaskCount(category.getTaskCount() + 1);
            categoryRepository.save(category);
        }
    }

    public void decrementTaskCount(String categoryId) {
        Category category = categoryRepository.findById(categoryId).orElse(null);
        if (category != null) {
            int count = category.getTaskCount();
            if (count > 0) {
                category.setTaskCount(count - 1);
            }
            categoryRepository.save(category);
        }
    }
}
