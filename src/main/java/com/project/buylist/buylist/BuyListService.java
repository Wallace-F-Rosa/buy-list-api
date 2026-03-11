package com.project.buylist.buylist;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.util.Objects;
import java.util.Optional;

@Service
public class BuyListService {

    private final BuyListRepository repository;

    @Autowired
    public BuyListService(BuyListRepository repository) {
        this.repository = repository;
    }

    public BuyList save(BuyList buyList) {
        for (BuyListItem item : buyList.getItems()) {
            item.setBuyList(buyList);
        }
        return repository.save(buyList);
    }

    public Optional<BuyList> getById(Long id) {
        return repository.findById(id);
    }

    public boolean existsById(Long id) {
        return repository.existsById(id);
    }

    public void delete(Long id) {
        repository.deleteById(id);
    }

    public Page<BuyList> search(BuyListFilterDto filter) {
        Specification<BuyList> spec = null;
        if (filter.getName() != null) {
            spec = Objects.isNull(spec) ? BuyListSpecifications.hasName(filter.getName())
                    : spec.and(BuyListSpecifications.hasName(filter.getName()));
        }
        if (filter.getCreatedFrom() != null) {
            spec = Objects.isNull(spec) ? BuyListSpecifications.createdAtAfter(filter.getCreatedFrom())
                    : spec.and(BuyListSpecifications.createdAtAfter(filter.getCreatedFrom()));
        }
        if (filter.getCreatedTo() != null) {
            spec = Objects.isNull(spec) ? BuyListSpecifications.createdAtBefore(filter.getCreatedTo())
                    : spec.and(BuyListSpecifications.createdAtBefore(filter.getCreatedTo()));
        }
        if (filter.getUpdatedFrom() != null) {
            spec = Objects.isNull(spec) ? BuyListSpecifications.updatedAtAfter(filter.getUpdatedFrom())
                    : spec.and(BuyListSpecifications.updatedAtAfter(filter.getUpdatedFrom()));
        }
        if (filter.getUpdatedTo() != null) {
            spec = Objects.isNull(spec) ? BuyListSpecifications.updatedAtBefore(filter.getUpdatedTo())
                    : spec.and(BuyListSpecifications.updatedAtBefore(filter.getUpdatedTo()));
        }
        PageRequest pageRequest = PageRequest.of(
                filter.getPage() != null ? filter.getPage() : 0,
                filter.getPageSize() != null ? filter.getPageSize() : 10);
        return Objects.isNull(spec) ? repository.findAll(pageRequest) : repository.findAll(spec, pageRequest);
    }
}
