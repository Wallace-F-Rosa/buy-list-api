package com.project.buylist.ingredients;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;

import java.util.Arrays;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@AutoConfigureTestDatabase
class IngredientsServiceTest {

    @Autowired
    private IngredientsRepository repository;

    @Autowired
    private IngredientsService service;

    @BeforeEach
    void setup() {
        repository.deleteAll();
    }

    @Test
    void createIngredient_savesAndReturnsEntity() {
        Ingredient ingredient = new Ingredient();
        ingredient.setName("Test");
        ingredient.setStoreSection("Test Section");
        ingredient.setUnitOfMeasure("t");

        Ingredient result = service.createIngredient(ingredient);

        assertThat(result.getId()).isNotNull();
        assertThat(repository.findById(result.getId())).isPresent().contains(result);
    }

    @Test
    void getIngredientById_returnsSavedEntity() {
        Ingredient ingredient = new Ingredient();
        ingredient.setName("Foo");
        ingredient.setStoreSection("S1");
        ingredient.setUnitOfMeasure("u");
        ingredient = repository.save(ingredient);

        Optional<Ingredient> result = service.getIngredientById(ingredient.getId());
        assertThat(result).isPresent().contains(ingredient);
    }

    @Test
    void updateIngredient_setsIdAndPersistsChanges() {
        Ingredient original = new Ingredient();
        original.setName("Old");
        original.setStoreSection("Sx");
        original.setUnitOfMeasure("ux");
        original = repository.save(original);

        Ingredient update = new Ingredient();
        update.setName("New");
        // keep other required properties so validation passes
        update.setStoreSection(original.getStoreSection());
        update.setUnitOfMeasure(original.getUnitOfMeasure());

        Ingredient updated = service.updateIngredient(original.getId(), update);
        assertThat(updated.getId()).isEqualTo(original.getId());
        assertThat(updated.getName()).isEqualTo("New");

        assertThat(repository.findById(original.getId())).isPresent().contains(updated);
    }

    @Test
    void existsById_reflectsRepositoryState() {
        Ingredient ingredient = new Ingredient();
        ingredient.setName("X");
        ingredient.setStoreSection("SX");
        ingredient.setUnitOfMeasure("ux");
        ingredient = repository.save(ingredient);

        assertThat(service.existsById(ingredient.getId())).isTrue();
        assertThat(service.existsById(ingredient.getId() + 1)).isFalse();
    }

    @Test
    void deleteIngredient_removesEntity() {
        Ingredient ingredient = new Ingredient();
        ingredient.setName("Y");
        ingredient.setStoreSection("SY");
        ingredient.setUnitOfMeasure("uy");
        ingredient = repository.save(ingredient);

        service.deleteIngredient(ingredient.getId());
        assertThat(repository.findById(ingredient.getId())).isEmpty();
    }

    @Test
    void search_withNoCriteria_returnsAllIngredients() {
        Ingredient a = new Ingredient();
        a.setName("A");
        a.setStoreSection("SA");
        a.setUnitOfMeasure("ua");
        Ingredient b = new Ingredient();
        b.setName("B");
        b.setStoreSection("SB");
        b.setUnitOfMeasure("ub");
        repository.saveAll(Arrays.asList(a, b));

        IngredientsFilterDto filterDto = IngredientsFilterDto.builder()
                .name(null)
                .storeSection(null)
                .page(null)
                .pageSize(null)
                .build();
        Page<Ingredient> result = service.search(filterDto);
        assertThat(result.getContent()).hasSize(2).extracting(Ingredient::getName).containsExactlyInAnyOrder("A", "B");
    }

    @Test
    void search_withName_filtersByName() {
        Ingredient match = new Ingredient();
        match.setName("foo");
        match.setStoreSection("S");
        match.setUnitOfMeasure("u");
        Ingredient other = new Ingredient();
        other.setName("bar");
        other.setStoreSection("S");
        other.setUnitOfMeasure("u");
        repository.saveAll(Arrays.asList(match, other));

        IngredientsFilterDto filterDto = IngredientsFilterDto.builder()
                .name("foo")
                .storeSection(null)
                .page(null)
                .pageSize(null)
                .build();
        Page<Ingredient> result = service.search(filterDto);
        assertThat(result.getContent()).hasSize(1).contains(match);
    }
}
