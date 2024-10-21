package com.santander.address.api.utils;

import com.santander.address.api.exceptions.InternalServerErrorException;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import org.springframework.web.util.UriComponentsBuilder;

import java.util.List;

public class PaginateBase<T> {

    @JsonProperty("page")
    protected PaginatePage<T> page;

    @JsonProperty("links")
    protected PaginateLinks links;

    protected String endpoint;
    protected int size;
    protected int pageNumber;
    protected Page<T> paginatedEntity;

    public static Pageable from(int page, int size) {
        validatePaginationParams(page, size);
        return PageRequest.of(page - 1, size);
    }

    protected String buildFullUri() {
        HttpServletRequest request = getCurrentHttpRequest();
        String requestUrl = request.getRequestURL().toString();
        String queryString = request.getQueryString();

        if (queryString != null) {
            String[] params = queryString.split("&");
            StringBuilder filteredParams = new StringBuilder();

            for (String param : params) {
                if (!param.startsWith("page=") && !param.startsWith("size=")) {
                    if (!filteredParams.isEmpty()) {
                        filteredParams.append("&");
                    }
                    filteredParams.append(param);
                }
            }

            return requestUrl + (!filteredParams.isEmpty() ? "?" + filteredParams : "");
        }

        return requestUrl;
    }

    protected PaginateLinks createLinks() {

        PaginateLinks paginateLinks = new PaginateLinks();

        paginateLinks.setFirst(createLink(endpointWithParams(1, size)));
        paginateLinks.setLast(createLink(endpointWithParams(paginatedEntity.getTotalPages(), size)));
        paginateLinks.setSelf(createLink(endpointWithParams(pageNumber, size)));

        if (paginatedEntity.hasPrevious()) {
            paginateLinks.setPrev(createLink(endpointWithParams(pageNumber - 1, size)));
        }

        if (paginatedEntity.hasNext()) {
            paginateLinks.setNext(createLink(endpointWithParams(pageNumber + 1, size)));
        }

        return paginateLinks;
    }

    protected List<T> processContent() {
        return this.paginatedEntity.getContent();
    }

    protected PaginatePage<T> processPageInfo() {
        return new PaginatePage<>(this.paginatedEntity);
    }

    protected static HttpServletRequest getCurrentHttpRequest() {
        ServletRequestAttributes attrs = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        if (attrs == null) {
            throw new InternalServerErrorException(Message.INTERNAL_SERVER_ERROR);
        }

        return attrs.getRequest();
    }

    private static void validatePaginationParams(int page, int size) {
        if (page < 1 || size < 1) {
            throw new IllegalArgumentException(Message.INVALID_REQUEST);
        }
    }

    private String endpointWithParams(int pageNumber, int size) {
        return UriComponentsBuilder.fromUriString(endpoint)
                .queryParam("page", pageNumber)
                .queryParam("size", size)
                .toUriString();
    }

    private PaginateLink createLink(String href) {
        PaginateLink paginateLink = new PaginateLink();
        paginateLink.setHref(href);

        return paginateLink;
    }
}
