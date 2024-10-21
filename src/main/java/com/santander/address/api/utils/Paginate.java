package com.santander.address.api.utils;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import org.springframework.data.domain.Page;

import java.util.List;

@Getter
public class Paginate<T, U> extends PaginateBase<T> {

    @JsonProperty("content")
    private final List<U> content;

    private Paginate(Page<T> paginatedEntity, List<U> entityToResponse) {
        this.size = paginatedEntity.getSize();
        this.pageNumber = paginatedEntity.getNumber() + 1;
        this.paginatedEntity = paginatedEntity;
        this.content = entityToResponse;
        this.endpoint = buildFullUri();
        this.page = processPageInfo();
        this.links = createLinks();
    }

    public static <T, U> Paginate<T, U> from(Page<T> paginatedEntity, List<U> entityToResponse) {
        return new Paginate<>(paginatedEntity, entityToResponse);
    }
}
