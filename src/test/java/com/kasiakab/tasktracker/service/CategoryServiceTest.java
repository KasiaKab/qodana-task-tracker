package com.kasiakab.tasktracker.service;

import com.kasiakab.tasktracker.dto.CategoryRequest;
import com.kasiakab.tasktracker.model.Category;
import com.kasiakab.tasktracker.repository.CategoryRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CategoryServiceTest {

    @Mock
    private CategoryRepository categoryRepository;

    @InjectMocks
    private CategoryService categoryService;

    private Category sampleCategory;
    private CategoryRequest sampleRequest;

    @BeforeEach
    void setUp() {
        sampleCategory = new Category("Work", "#FF0000");
        sampleCategory.setId("cat-123");
        sampleCategory.setDescription("Work related tasks");

        sampleRequest = new CategoryRequest();
        sampleRequest.setName("Work");
        sampleRequest.setColor("#FF0000");
        sampleRequest.setDescription("Work related tasks");
    }

    @Test
    void createCategory_shouldCreateAndReturnCategory() {
        when(categoryRepository.existsByName("Work")).thenReturn(false);
        when(categoryRepository.save(any(Category.class))).thenReturn(sampleCategory);

        Category result = categoryService.createCategory(sampleRequest);

        assertThat(result).isNotNull();
        assertThat(result.getName()).isEqualTo("Work");
        assertThat(result.getColor()).isEqualTo("#FF0000");
        verify(categoryRepository, times(1)).save(any(Category.class));
    }

    @Test
    void createCategory_withDefaultColor_whenColorIsNull() {
        sampleRequest.setColor(null);

        Category savedCategory = new Category("Work", "#808080");
        savedCategory.setId("cat-123");

        when(categoryRepository.existsByName("Work")).thenReturn(false);
        when(categoryRepository.save(any(Category.class))).thenReturn(savedCategory);

        Category result = categoryService.createCategory(sampleRequest);

        assertThat(result.getColor()).isEqualTo("#808080");
    }

    @Test
    void createCategory_withEmptyName_shouldThrowException() {
        sampleRequest.setName("");

        assertThatThrownBy(() -> categoryService.createCategory(sampleRequest))
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("Category name cannot be empty");
    }

    @Test
    void createCategory_withNullName_shouldThrowException() {
        sampleRequest.setName(null);

        assertThatThrownBy(() -> categoryService.createCategory(sampleRequest))
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("Category name cannot be empty");
    }

    @Test
    void createCategory_withNameTooLong_shouldThrowException() {
        sampleRequest.setName("A".repeat(51));

        assertThatThrownBy(() -> categoryService.createCategory(sampleRequest))
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("Category name too long");
    }

    @Test
    void createCategory_withExistingName_shouldThrowException() {
        when(categoryRepository.existsByName("Work")).thenReturn(true);

        assertThatThrownBy(() -> categoryService.createCategory(sampleRequest))
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("Category already exists");
    }

    @Test
    void getAllCategories_shouldReturnAllCategories() {
        Category category2 = new Category("Personal", "#00FF00");
        category2.setId("cat-456");

        when(categoryRepository.findAll()).thenReturn(List.of(sampleCategory, category2));

        List<Category> result = categoryService.getAllCategories();

        assertThat(result).hasSize(2);
        assertThat(result).extracting(Category::getName).containsExactlyInAnyOrder("Work", "Personal");
    }

    @Test
    void getCategoryById_shouldReturnCategory() {
        when(categoryRepository.findById("cat-123")).thenReturn(Optional.of(sampleCategory));

        Category result = categoryService.getCategoryById("cat-123");

        assertThat(result).isNotNull();
        assertThat(result.getId()).isEqualTo("cat-123");
        assertThat(result.getName()).isEqualTo("Work");
    }

    @Test
    void getCategoryById_notFound_shouldThrowException() {
        when(categoryRepository.findById("nonexistent")).thenReturn(Optional.empty());

        assertThatThrownBy(() -> categoryService.getCategoryById("nonexistent"))
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("Category not found");
    }

    @Test
    void updateCategory_shouldUpdateAndReturnCategory() {
        CategoryRequest updateRequest = new CategoryRequest();
        updateRequest.setName("Updated Work");
        updateRequest.setColor("#0000FF");

        Category updatedCategory = new Category("Updated Work", "#0000FF");
        updatedCategory.setId("cat-123");

        when(categoryRepository.findById("cat-123")).thenReturn(Optional.of(sampleCategory));
        when(categoryRepository.save(any(Category.class))).thenReturn(updatedCategory);

        Category result = categoryService.updateCategory("cat-123", updateRequest);

        assertThat(result).isNotNull();
        assertThat(result.getName()).isEqualTo("Updated Work");
        assertThat(result.getColor()).isEqualTo("#0000FF");
        verify(categoryRepository, times(1)).save(any(Category.class));
    }

    @Test
    void updateCategory_notFound_shouldThrowException() {
        when(categoryRepository.findById("nonexistent")).thenReturn(Optional.empty());

        assertThatThrownBy(() -> categoryService.updateCategory("nonexistent", sampleRequest))
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("Category not found");
    }

    @Test
    void updateCategory_withEmptyName_shouldThrowException() {
        CategoryRequest updateRequest = new CategoryRequest();
        updateRequest.setName("");

        when(categoryRepository.findById("cat-123")).thenReturn(Optional.of(sampleCategory));

        assertThatThrownBy(() -> categoryService.updateCategory("cat-123", updateRequest))
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("Category name cannot be empty");
    }

    @Test
    void deleteCategory_shouldCallRepository() {
        doNothing().when(categoryRepository).deleteById("cat-123");

        categoryService.deleteCategory("cat-123");

        verify(categoryRepository, times(1)).deleteById("cat-123");
    }

    @Test
    void findByName_shouldReturnCategory() {
        when(categoryRepository.findByName("Work")).thenReturn(Optional.of(sampleCategory));

        Optional<Category> result = categoryService.findByName("Work");

        assertThat(result).isPresent();
        assertThat(result.get().getName()).isEqualTo("Work");
    }

    @Test
    void findByName_notFound_shouldReturnEmpty() {
        when(categoryRepository.findByName("Nonexistent")).thenReturn(Optional.empty());

        Optional<Category> result = categoryService.findByName("Nonexistent");

        assertThat(result).isEmpty();
    }

    @Test
    void incrementTaskCount_shouldIncrementCount() {
        sampleCategory.setTaskCount(2);

        when(categoryRepository.findById("cat-123")).thenReturn(Optional.of(sampleCategory));
        when(categoryRepository.save(any(Category.class))).thenAnswer(invocation -> invocation.getArgument(0));

        categoryService.incrementTaskCount("cat-123");

        verify(categoryRepository, times(1)).save(any(Category.class));
        assertThat(sampleCategory.getTaskCount()).isEqualTo(3);
    }

    @Test
    void incrementTaskCount_whenCategoryNotFound_shouldDoNothing() {
        when(categoryRepository.findById("nonexistent")).thenReturn(Optional.empty());

        categoryService.incrementTaskCount("nonexistent");

        verify(categoryRepository, never()).save(any(Category.class));
    }

    @Test
    void decrementTaskCount_shouldDecrementCount() {
        sampleCategory.setTaskCount(3);

        when(categoryRepository.findById("cat-123")).thenReturn(Optional.of(sampleCategory));
        when(categoryRepository.save(any(Category.class))).thenAnswer(invocation -> invocation.getArgument(0));

        categoryService.decrementTaskCount("cat-123");

        verify(categoryRepository, times(1)).save(any(Category.class));
        assertThat(sampleCategory.getTaskCount()).isEqualTo(2);
    }

    @Test
    void decrementTaskCount_whenCountIsZero_shouldNotGoBelowZero() {
        sampleCategory.setTaskCount(0);

        when(categoryRepository.findById("cat-123")).thenReturn(Optional.of(sampleCategory));
        when(categoryRepository.save(any(Category.class))).thenAnswer(invocation -> invocation.getArgument(0));

        categoryService.decrementTaskCount("cat-123");

        verify(categoryRepository, times(1)).save(any(Category.class));
        assertThat(sampleCategory.getTaskCount()).isEqualTo(0);
    }

    @Test
    void decrementTaskCount_whenCategoryNotFound_shouldDoNothing() {
        when(categoryRepository.findById("nonexistent")).thenReturn(Optional.empty());

        categoryService.decrementTaskCount("nonexistent");

        verify(categoryRepository, never()).save(any(Category.class));
    }

}
