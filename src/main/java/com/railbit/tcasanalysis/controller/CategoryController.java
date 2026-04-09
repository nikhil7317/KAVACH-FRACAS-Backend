package com.railbit.tcasanalysis.controller;

import com.railbit.tcasanalysis.DTO.CategoryDTO;
import com.railbit.tcasanalysis.entity.Category;
import com.railbit.tcasanalysis.service.CategoryService;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/tcasapi/categories")
@CrossOrigin(origins = "*")   // adjust origin in production
@RequiredArgsConstructor
public class CategoryController {

    private final CategoryService categoryService;

    // GET /api/categories → fetch all categories with severity
    @GetMapping
    public ResponseEntity<List<Category>> getAll() {
        return ResponseEntity.ok(categoryService.getAllCategories());
    }

    // GET /api/categories/{id} → fetch single category
    @GetMapping("/{id}")
    public ResponseEntity<Category> getById(@PathVariable Long id) {
        return ResponseEntity.ok(categoryService.getCategoryById(id));
    }

    // POST /api/categories → create new category
    // Body: { "name": "NEW_CATEGORY", "severityId": 2 }
    @PostMapping
    public ResponseEntity<Category> create(@RequestBody CategoryDTO dto) {
        return ResponseEntity.ok(categoryService.createCategory(dto));
    }

    // PUT /api/categories/{id} → update name and/or severity
    // Body: { "name": "UPDATED_NAME", "severityId": 3 }
    @PutMapping("/{id}")
    public ResponseEntity<Category> update(@PathVariable Long id, @RequestBody CategoryDTO dto) {
        return ResponseEntity.ok(categoryService.updateCategory(id, dto));
    }

    // DELETE /api/categories/{id} → delete a category
    @DeleteMapping("/{id}")
    public ResponseEntity<String> delete(@PathVariable Long id) {
        categoryService.deleteCategory(id);
        return ResponseEntity.ok("Category deleted successfully");
    }
}