package com.santander.address.api.responses;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@AllArgsConstructor
@Getter
@Setter
public class ErrorResponse {

    @JsonProperty("errors")
    private List<ErrorFieldResponse> errors;

    @JsonProperty("message")
    private String message;

}
