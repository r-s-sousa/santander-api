package com.santander.address.api.requests;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ZipCodeRequest {

    @Size(min = 8, max = 8, message = "Zip code must be between 8 numbers")
    @Pattern(regexp = "[0-9]+", message = "Zip code must be only numbers")
    @JsonProperty("zip_code")
    private String zipCode;
}
