package com.project.buylist.ingredients;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

@Repository
public interface IngredientsRepository
        extends JpaRepository<Ingredient, Long>, JpaSpecificationExecutor<Ingredient> {
    // Find by name
    Ingredient findByName(String name);

    // Find all by store section
    List<Ingredient> findAllByStoreSection(String storeSection);

    // Find by name and store section
    Ingredient findByNameAndStoreSection(String name, String storeSection);
}
