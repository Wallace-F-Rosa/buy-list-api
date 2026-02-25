package com.project.buylist.ingredients;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.data.jpa.domain.Specification;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

class IngredientsServiceTest {

    @Mock
    private IngredientsRepository repository;

    @InjectMocks
    private IngredientsService service;

    @BeforeEach
    void setup() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void createIngredient_delegatesToRepository() {
        IngredientsEntity ingredient = new IngredientsEntity();
        ingredient.setName("Test");

        when(repository.save(ingredient)).thenReturn(ingredient);

        IngredientsEntity result = service.createIngredient(ingredient);

        assertThat(result).isSameAs(ingredient);
        verify(repository).save(ingredient);
    }

    @Test
    void getIngredientById_returnsOptional() {
        IngredientsEntity ingredient = new IngredientsEntity();
        when(repository.findById(1L)).thenReturn(Optional.of(ingredient));

        Optional<IngredientsEntity> result = service.getIngredientById(1L);
        assertThat(result).isPresent().contains(ingredient);
    }

    @Test
    void updateIngredient_setsIdAndSaves() {
        IngredientsEntity ingredient = new IngredientsEntity();
        when(repository.save(any())).thenAnswer(invocation -> invocation.getArgument(0));

        IngredientsEntity updated = service.updateIngredient(5L, ingredient);
        assertThat(updated.getId()).isEqualTo(5L);
        verify(repository).save(ingredient);
    }

    @Test
    void existsById_delegates() {
        when(repository.existsById(10L)).thenReturn(true);
        assertThat(service.existsById(10L)).isTrue();
    }

    @Test
    void deleteIngredient_delegates() {
        service.deleteIngredient(3L);
        verify(repository).deleteById(3L);
    }

    @Test
    void search_withNoCriteria_returnAll() {
        List<IngredientsEntity> all = Arrays.asList(new IngredientsEntity());
        when(repository.findAll()).thenReturn(all);
        List<IngredientsEntity> result = service.search(null, null);
        assertThat(result).isSameAs(all);
    }

    @Test
    void search_withName_buildsSpecification() {
        IngredientsEntity ing = new IngredientsEntity();
        List<IngredientsEntity> all = Arrays.asList(ing);
        when(repository.findAll(any(Specification.class))).thenReturn(all);

        List<IngredientsEntity> result = service.search("foo", null);
        assertThat(result).isSameAs(all);
        // verify that spec passed contains the name predicate by capturing
        ArgumentCaptor<Specification<IngredientsEntity>> captor = ArgumentCaptor.forClass(Specification.class);
        verify(repository).findAll(captor.capture());
        Specification<IngredientsEntity> spec = captor.getValue();
        assertThat(spec).isNotNull();
    }
}
