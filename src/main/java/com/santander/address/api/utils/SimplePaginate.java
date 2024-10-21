package com.santander.address.api.utils;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import org.springframework.data.domain.Page;

import java.util.List;

@Getter
public class SimplePaginate<T> extends PaginateBase<T> {

    @JsonProperty("content")
    private final List<T> content;

    private SimplePaginate(Page<T> paginatedEntity) {
        this.size = paginatedEntity.getSize();
        this.pageNumber = paginatedEntity.getNumber() + 1;
        this.paginatedEntity = paginatedEntity;
        this.content = processContent();
        this.endpoint = buildFullUri();
        this.page = processPageInfo();
        this.links = createLinks();
    }

    public static <T> SimplePaginate<T> from(Page<T> paginatedEntity) {
        return new SimplePaginate<>(paginatedEntity);
    }
}
