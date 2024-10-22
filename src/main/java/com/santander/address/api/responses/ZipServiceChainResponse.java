package com.santander.address.api.responses;

import com.santander.address.api.enums.ServiceName;
import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ZipServiceChainResponse {

    private ServiceName serviceName;
    private String externalResponse;
    private Address address;
}
