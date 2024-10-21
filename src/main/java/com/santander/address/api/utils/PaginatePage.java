package com.santander.address.api.utils;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import org.springframework.data.domain.Page;

@Getter
public class PaginatePage<T> {

    @JsonProperty("number")
    private final int number;

    @JsonProperty("size")
    private final int size;

    @JsonProperty("total_pages")
    private final int totalPages;

    @JsonProperty("total_elements")
    private final long totalElements;

    public PaginatePage(Page<T> paginatedEntity) {
        this.number = paginatedEntity.getNumber() + 1;
        this.size = paginatedEntity.getNumberOfElements();
        this.totalElements = paginatedEntity.getTotalElements();
        this.totalPages = paginatedEntity.getTotalPages();
    }
}
