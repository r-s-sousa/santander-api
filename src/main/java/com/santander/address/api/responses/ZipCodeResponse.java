package com.santander.address.api.responses;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@Setter
@Builder
public class ZipCodeResponse {

    @JsonProperty("id")
    private UUID id;

    @JsonProperty("address")
    private Address address;

    @JsonProperty("zip_code")
    private String zipCode;

    @JsonProperty("service_name")
    private String serviceName;

    @JsonProperty("request")
    private String request;

    @JsonProperty("external_response")
    private String externalResponse;

    @JsonProperty("response")
    private String response;

    @JsonProperty("created_at")
    private LocalDateTime createdAt;
}
