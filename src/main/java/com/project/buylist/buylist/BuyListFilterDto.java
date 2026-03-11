package com.project.buylist.buylist;

import java.time.LocalDateTime;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class BuyListFilterDto {
    private String name;
    private LocalDateTime createdFrom;
    private LocalDateTime createdTo;
    private LocalDateTime updatedFrom;
    private LocalDateTime updatedTo;
    private Integer page;
    private Integer pageSize;
    private String sortBy;
    private String sortDirection;
}
