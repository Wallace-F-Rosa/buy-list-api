package com.project.buylist.ingredients;

import org.springframework.beans.factory.annotation.Autowired;
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

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/ingredient")
public class IngredientsController {

    @Autowired
    private IngredientsService ingredientsService;

    // Create
    @PostMapping("")
    public ResponseEntity<IngredientsEntity> createIngredient(@Validated @RequestBody IngredientsEntity ingredient) {
        IngredientsEntity saved = ingredientsService.createIngredient(ingredient);
        return ResponseEntity.ok(saved);
    }

    // Read by id
    @GetMapping("/{id}")
    public ResponseEntity<IngredientsEntity> getIngredientById(@PathVariable("id") Long id) {
        Optional<IngredientsEntity> found = ingredientsService.getIngredientById(id);
        return found.map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.notFound().build());
    }

    // Update
    @PutMapping("/{id}")
    public ResponseEntity<?> updateIngredient(@PathVariable("id") Long id,
            @Validated @RequestBody IngredientsEntity ingredient) {
        return ingredientsService.getIngredientById(id)
                .map(existing -> {
                    IngredientsEntity updated = ingredientsService.updateIngredient(id, ingredient);
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
    public ResponseEntity<List<IngredientsEntity>> getByFields(
            @RequestParam(name = "name", required = false) String name,
            @RequestParam(name = "storeSection", required = false) String storeSection) {
        List<IngredientsEntity> results = ingredientsService.search(name, storeSection);
        return ResponseEntity.ok(results);
    }
}
