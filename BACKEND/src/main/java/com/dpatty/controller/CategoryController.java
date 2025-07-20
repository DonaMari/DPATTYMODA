package com.dpatty.controller;

import com.dpatty.dto.product.CategoryResponse;
import com.dpatty.model.Category;
import com.dpatty.repository.CategoryRepository;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/categories")
@Tag(name = "Categories", description = "Gestión de categorías")
public class CategoryController {

    @Autowired
    private CategoryRepository categoryRepository;

    @Autowired
    private ModelMapper modelMapper;

    @GetMapping
    @Operation(summary = "Obtener todas las categorías activas")
    public ResponseEntity<List<CategoryResponse>> getAllCategories() {
        List<Category> categories = categoryRepository.findAllActiveOrderBySortOrder();
        List<CategoryResponse> response = categories.stream()
                .map(category -> modelMapper.map(category, CategoryResponse.class))
                .toList();
        return ResponseEntity.ok(response);
    }

    @GetMapping("/root")
    @Operation(summary = "Obtener categorías principales")
    public ResponseEntity<List<CategoryResponse>> getRootCategories() {
        List<Category> categories = categoryRepository.findByParentIsNullAndIsActiveTrueOrderBySortOrder();
        List<CategoryResponse> response = categories.stream()
                .map(category -> modelMapper.map(category, CategoryResponse.class))
                .toList();
        return ResponseEntity.ok(response);
    }

    @GetMapping("/{parentId}/children")
    @Operation(summary = "Obtener subcategorías")
    public ResponseEntity<List<CategoryResponse>> getChildCategories(@PathVariable Long parentId) {
        List<Category> categories = categoryRepository.findByParentIdAndIsActiveTrueOrderBySortOrder(parentId);
        List<CategoryResponse> response = categories.stream()
                .map(category -> modelMapper.map(category, CategoryResponse.class))
                .toList();
        return ResponseEntity.ok(response);
    }
}