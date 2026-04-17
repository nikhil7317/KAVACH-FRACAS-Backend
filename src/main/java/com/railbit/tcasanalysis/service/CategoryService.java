package com.railbit.tcasanalysis.service;

import com.railbit.tcasanalysis.DTO.CategoryDTO;
import com.railbit.tcasanalysis.entity.Category;
import com.railbit.tcasanalysis.entity.Severity;
import com.railbit.tcasanalysis.repository.CategoryRepository;
import com.railbit.tcasanalysis.repository.SeverityRepository;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class CategoryService {

    private final CategoryRepository categoryRepository;
    private final SeverityRepository severityRepository;

    // GET ALL
    public List<Category> getAllCategories() {
        return categoryRepository.findAll();
    }

    // GET BY ID
    public Category getCategoryById(Long id) {
        return categoryRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Category not found with id: " + id));
    }

    // CREATE
    public Category createCategory(CategoryDTO dto) {
        if (categoryRepository.existsByName(dto.getName())) {
            throw new RuntimeException("Category name already exists: " + dto.getName());
        }
        Severity severity = severityRepository.findById(dto.getSeverityId())
                .orElseThrow(() -> new RuntimeException("Severity not found with id: " + dto.getSeverityId()));

        Category category = new Category();
        category.setName(dto.getName());
        category.setSeverity(severity);
        return categoryRepository.save(category);
    }

    // UPDATE (name and/or severity)
    public Category updateCategory(Long id, CategoryDTO dto) {
        Category category = getCategoryById(id);

        if (dto.getName() != null && !dto.getName().isBlank()) {
            category.setName(dto.getName());
        }
        if (dto.getSeverityId() != null) {
            Severity severity = severityRepository.findById(dto.getSeverityId())
                    .orElseThrow(() -> new RuntimeException("Severity not found with id: " + dto.getSeverityId()));
            category.setSeverity(severity);
        }
        return categoryRepository.save(category);
    }

    // DELETE
    public void deleteCategory(Long id) {
        if (!categoryRepository.existsById(id)) {
            throw new RuntimeException("Category not found with id: " + id);
        }
        categoryRepository.deleteById(id);
    }
}