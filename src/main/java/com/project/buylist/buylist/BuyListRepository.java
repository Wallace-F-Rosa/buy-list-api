package com.project.buylist.buylist;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

@Repository
public interface BuyListRepository extends JpaRepository<BuyList, Long>, JpaSpecificationExecutor<BuyList> {
    // Additional query methods could go here if needed
}
