package com.santander.address.api.utils;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class PaginateLinks {

    @JsonProperty("self")
    private PaginateLink self;

    @JsonProperty("first")
    private PaginateLink first;

    @JsonProperty("last")
    private PaginateLink last;

    @JsonProperty("next")
    private PaginateLink next;

    @JsonProperty("prev")
    private PaginateLink prev;
}
