package com.santander.address.api.requests;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
public class ProductRequest {

    @Size(min = 3, max = 500, message = "Description must be between 3 and 500 characters")
    @JsonProperty("description")
    private String description;

    @Size(min = 3, max = 255, message = "Name must be between 3 and 255 characters")
    @JsonProperty("name")
    private String name;

    @JsonProperty("deleted_at")
    private LocalDateTime deletedAt;
}
