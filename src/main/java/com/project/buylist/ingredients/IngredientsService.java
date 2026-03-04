package com.project.buylist.ingredients;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.jaxb.SpringDataJaxb.OrderDto;
import org.springframework.data.domain.jaxb.SpringDataJaxb.SortDto;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import jakarta.transaction.Transactional;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

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

    public Page<Ingredient> search(IngredientsFilterDto filter) {
        Specification<Ingredient> spec = null;
        if (filter.getName() != null) {
            spec = Objects.isNull(spec) ? IngredientsSpecifications.hasName(filter.getName())
                    : spec.and(IngredientsSpecifications.hasName(filter.getName()));
        }
        if (filter.getStoreSection() != null) {
            spec = Objects.isNull(spec) ? IngredientsSpecifications.hasStoreSection(filter.getStoreSection())
                    : spec.and(IngredientsSpecifications.hasStoreSection(filter.getStoreSection()));
        }

        PageRequest pageRequest = PageRequest.of(
                filter.getPage() != null ? filter.getPage() : 0,
                filter.getPageSize() != null ? filter.getPageSize() : 10);

        return Objects.isNull(spec) ? ingredientsRepository.findAll(pageRequest)
                : ingredientsRepository.findAll(spec, pageRequest);
    }
}
