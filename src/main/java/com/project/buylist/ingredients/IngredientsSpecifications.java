package com.project.buylist.ingredients;

import org.springframework.data.jpa.domain.Specification;

public class IngredientsSpecifications {
    public static Specification<IngredientsEntity> hasName(String name) {
        return (root, query, cb) -> name == null ? null : cb.equal(root.get("name"), name);
    }

    public static Specification<IngredientsEntity> hasStoreSection(String storeSection) {
        return (root, query, cb) -> storeSection == null ? null : cb.equal(root.get("storeSection"), storeSection);
    }
}
