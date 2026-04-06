package com.project.buylist.buylist;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

@Repository
public interface BuyListRepository extends JpaRepository<BuyList, Long>, JpaSpecificationExecutor<BuyList> {

    Optional<BuyList> findByIdAndUserId(Long id, String userId);
    // Additional query methods could go here if needed

    boolean existsByIdAndUserId(Long id, String userId);
}
