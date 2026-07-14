package com_abertamente_cms.service;

import com_abertamente_cms.domain.Category;
import com_abertamente_cms.dto.category.CategoryRequest;
import com_abertamente_cms.dto.category.CategoryResponse;
import com_abertamente_cms.exception.ResourceNotFoundException;
import com_abertamente_cms.repository.CategoryRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
public class CategoryService {

    private final CategoryRepository categoryRepository;

    public CategoryService(CategoryRepository categoryRepository) {
        this.categoryRepository = categoryRepository;
    }

    @Transactional(readOnly = true)
    public Page<CategoryResponse> findAll(Pageable pageable) {
        return categoryRepository.findAll(pageable).map(CategoryResponse::fromEntity);
    }

    @Transactional(readOnly = true)
    public CategoryResponse findById(UUID id) {
        Category category = categoryRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Categoria não encontrada."));
        return CategoryResponse.fromEntity(category);
    }

    @Transactional
    public CategoryResponse create(CategoryRequest request) {
        if (categoryRepository.findByName(request.name()).isPresent()) {
            throw new IllegalArgumentException("Categoria com mesmo nome já existe.");
        }

        if (request.slug() != null && !request.slug().isBlank() && categoryRepository.findBySlug(request.slug()).isPresent()) {
            throw new IllegalArgumentException("Categoria com mesmo slug já existe.");
        }

        Category category = new Category(request.name(), request.slug(), request.description());
        category = categoryRepository.save(category);
        return CategoryResponse.fromEntity(category);
    }

    @Transactional
    public CategoryResponse update(UUID id, CategoryRequest request) {
        Category category = categoryRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Categoria não encontrada."));

        if (!category.getName().equals(request.name()) && categoryRepository.findByName(request.name()).isPresent()) {
            throw new IllegalArgumentException("Nome da categoria já está em uso.");
        }

        if (request.slug() != null && !request.slug().isBlank() && !java.util.Objects.equals(category.getSlug(), request.slug()) && categoryRepository.findBySlug(request.slug()).isPresent()) {
            throw new IllegalArgumentException("Slug da categoria já está em uso.");
        }

        category.setName(request.name());
        category.setSlug(request.slug());
        category.setDescription(request.description());

        return CategoryResponse.fromEntity(categoryRepository.save(category));
    }

    @Transactional
    public void delete(UUID id) {
        Category category = categoryRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Categoria não encontrada."));
        
        categoryRepository.delete(category); // Graças ao @SQLDelete, isso fará o Soft Delete
    }
}
