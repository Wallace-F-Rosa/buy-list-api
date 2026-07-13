package com.project.buylist.ingredients;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import java.util.Optional;

@RestController
@RequestMapping("/api/ingredient")
// TODO: add swagger docs
public class IngredientsController {

    @Autowired
    private IngredientsService ingredientsService;

    // Create
    @PostMapping("")
    public ResponseEntity<Ingredient> createIngredient(@Validated @RequestBody Ingredient ingredient) {
        Ingredient saved = ingredientsService.createIngredient(ingredient);
        return ResponseEntity.ok(saved);
    }

    // Read by id
    @GetMapping("/{id}")
    public ResponseEntity<Ingredient> getIngredientById(@PathVariable("id") Long id) {
        Optional<Ingredient> found = ingredientsService.getIngredientById(id);
        return found.map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.notFound().build());
    }

    // Update
    @PutMapping("/{id}")
    public ResponseEntity<?> updateIngredient(@PathVariable("id") Long id,
            @Validated @RequestBody Ingredient ingredient) {
        return ingredientsService.getIngredientById(id)
                .map(existing -> {
                    Ingredient updated = ingredientsService.updateIngredient(id, ingredient);
                    return ResponseEntity.ok(updated);
                })
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    // Delete
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteIngredient(@PathVariable("id") Long id) {
        if (ingredientsService.existsById(id)) {
            ingredientsService.deleteIngredient(id);
            return ResponseEntity.noContent().build();
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    // Unified search endpoint using query parameters
    @GetMapping("")
    public ResponseEntity<Page<Ingredient>> search(
            @RequestParam(name = "name", required = false) String name,
            @RequestParam(name = "storeSection", required = false) String storeSection,
            @RequestParam(name = "page", required = true, defaultValue = "0") int page,
            @RequestParam(name = "page_size", required = true, defaultValue = "10") @Min(value = 1, message = "page_size must be a positive integer") @Max(value = 100, message = "page_size must be between 1 and 100") int pageSize) {
        IngredientsFilterDto filterDto = IngredientsFilterDto.builder()
                .name(name)
                .storeSection(storeSection)
                .page(page)
                .pageSize(pageSize)
                .build();
        Page<Ingredient> results = ingredientsService.search(filterDto);
        return ResponseEntity.ok(results);
    }
}
