package com.santander.address.api.responses;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@AllArgsConstructor
@Getter
@Setter
public class ErrorFieldResponse {

    @JsonProperty("field")
    private String field;

    @JsonProperty("message")
    private String message;

}
