package com.project.buylist.ingredients;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

@Repository
public interface IngredientsRepository
        extends JpaRepository<IngredientsEntity, Long>, JpaSpecificationExecutor<IngredientsEntity> {
    // Find by name
    IngredientsEntity findByName(String name);

    // Find all by store section
    List<IngredientsEntity> findAllByStoreSection(String storeSection);

    // Find by name and store section
    IngredientsEntity findByNameAndStoreSection(String name, String storeSection);
}
