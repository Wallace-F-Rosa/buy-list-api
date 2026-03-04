package com.project.buylist.ingredients;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class IngredientsFilterDto {
    private String name;
    private String storeSection;
    private Integer page;
    private Integer pageSize;
    private String sortBy;
    private String sortDirection;
}
