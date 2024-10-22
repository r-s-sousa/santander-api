package com.santander.address.api.responses;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class Address {

    @JsonProperty("zip_code")
    private String code;

    @JsonProperty("street")
    private String street;

    @JsonProperty("complement")
    private String complement;

    @JsonProperty("neighborhood")
    private String neighborhood;

    @JsonProperty("city")
    private String city;

    @JsonProperty("state")
    private String state;
}
