package com.project.buylist.ingredients;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import jakarta.transaction.Transactional;

import java.util.List;
import java.util.Objects;
import java.util.Optional;

@Service
@Transactional
public class IngredientsService {

    private final IngredientsRepository ingredientsRepository;

    @Autowired
    public IngredientsService(IngredientsRepository ingredientsRepository) {
        this.ingredientsRepository = ingredientsRepository;
    }

    public Ingredient createIngredient(Ingredient ingredient) {
        return ingredientsRepository.save(ingredient);
    }

    public Optional<Ingredient> getIngredientById(Long id) {
        return ingredientsRepository.findById(id);
    }

    public Ingredient updateIngredient(Long id, Ingredient ingredient) {
        // ensure the id is set; caller should have verified existence
        ingredient.setId(id);
        return ingredientsRepository.save(ingredient);
    }

    public boolean existsById(Long id) {
        return ingredientsRepository.existsById(id);
    }

    public void deleteIngredient(Long id) {
        ingredientsRepository.deleteById(id);
    }

    public List<Ingredient> search(String name, String storeSection) {
        Specification<Ingredient> spec = null;
        if (name != null) {
            spec = Objects.isNull(spec) ? IngredientsSpecifications.hasName(name)
                    : spec.and(IngredientsSpecifications.hasName(name));
        }
        if (storeSection != null) {
            spec = Objects.isNull(spec) ? IngredientsSpecifications.hasStoreSection(storeSection)
                    : spec.and(IngredientsSpecifications.hasStoreSection(storeSection));
        }

        return Objects.isNull(spec) ? ingredientsRepository.findAll() : ingredientsRepository.findAll(spec);
    }
}
