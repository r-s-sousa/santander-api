package com.santander.address.api.utils;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class PaginateLink {

    @JsonProperty("href")
    private String href;
}
