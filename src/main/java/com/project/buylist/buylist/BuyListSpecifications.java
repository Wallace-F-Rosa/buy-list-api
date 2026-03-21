package com.project.buylist.buylist;

import java.time.LocalDateTime;

import org.springframework.data.jpa.domain.Specification;

public class BuyListSpecifications {
    public static Specification<BuyList> hasName(String name) {
        return (root, query, cb) -> name == null ? null : cb.equal(root.get("name"), name);
    }

    public static Specification<BuyList> createdAt(LocalDateTime createdAt) {
        return (root, query, cb) -> cb.equal(root.get("createdAt"), createdAt);
    }

    public static Specification<BuyList> createdAtAfter(LocalDateTime from) {
        return (root, query, cb) -> cb.greaterThanOrEqualTo(root.get("createdAt"), from);
    }

    public static Specification<BuyList> createdAtBefore(LocalDateTime to) {
        return (root, query, cb) -> cb.lessThanOrEqualTo(root.get("createdAt"), to);
    }

    public static Specification<BuyList> updatedAt(LocalDateTime updatedAt) {
        return (root, query, cb) -> cb.equal(root.get("updatedAt"), updatedAt);
    }

    public static Specification<BuyList> updatedAtAfter(LocalDateTime from) {
        return (root, query, cb) -> cb.greaterThanOrEqualTo(root.get("updatedAt"), from);
    }

    public static Specification<BuyList> updatedAtBefore(LocalDateTime to) {
        return (root, query, cb) -> cb.lessThanOrEqualTo(root.get("updatedAt"), to);
    }
}
