package com.santander.address.api.controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.santander.address.api.entities.ZipCode;
import com.santander.address.api.requests.ZipCodeRequest;
import com.santander.address.api.responses.Address;
import com.santander.address.api.responses.ZipCodeResponse;
import com.santander.address.api.services.ZipCodeService;
import com.santander.address.api.utils.DynamicFilter;
import com.santander.address.api.utils.FilterItem;
import com.santander.address.api.utils.Message;
import com.santander.address.api.utils.Paginate;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URI;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@AllArgsConstructor
@RequestMapping("/v1")
@RestController
@Tag(name = "zip-codes", description = "Zip codes operations")
public class ZipCodeController {

    private final ZipCodeService zipCodeService;
    private final ObjectMapper objectMapper;

    @PostMapping(path = "/zip-codes/search")
    public ResponseEntity<ZipCodeResponse> search(@RequestBody @Valid ZipCodeRequest zipCodeRequest,
                                                  UriComponentsBuilder uriComponentsBuilder) {
        ZipCode zipCode = zipCodeService.search(zipCodeRequest);
        Address address = getAddress(zipCode);
        ZipCodeResponse zipCodeResponse = ZipCodeResponse.builder()
                .id(zipCode.getId())
                .zipCode(zipCode.getZipCode())
                .serviceName(zipCode.getServiceName())
                .request(zipCode.getRequest())
                .externalResponse(zipCode.getExternalResponse())
                .response(zipCode.getResponse())
                .address(address)
                .createdAt(zipCode.getCreatedAt())
                .build();

        URI uri = uriComponentsBuilder.path("/v1/zip-codes/{id}").buildAndExpand(zipCode.getId()).toUri();
        return ResponseEntity.created(uri).body(zipCodeResponse);
    }

    @GetMapping(path = "/zip-codes-service-logs")
    public ResponseEntity<Paginate<ZipCode, ZipCodeResponse>> findAll(@RequestParam(defaultValue = "1") int page,
                                                                      @RequestParam(defaultValue = "10") int size,
                                                                      @RequestParam(defaultValue = "") Map<String, String> params) {
        List<FilterItem> filters = DynamicFilter.parseFilters(params);
        Page<ZipCode> zipCodes = zipCodeService.findAll(page, size, filters);
        List<ZipCodeResponse> zipCodeResponses = new ArrayList<>();

        zipCodes.forEach(zipCode -> {
            Address address = getAddress(zipCode);
            ZipCodeResponse zipCodeResponse = ZipCodeResponse.builder()
                    .id(zipCode.getId())
                    .address(address)
                    .zipCode(zipCode.getZipCode())
                    .serviceName(zipCode.getServiceName())
                    .request(zipCode.getRequest())
                    .response(zipCode.getResponse())
                    .externalResponse(zipCode.getExternalResponse())
                    .createdAt(zipCode.getCreatedAt())
                    .build();
            zipCodeResponses.add(zipCodeResponse);
        });

        return ResponseEntity.ok(Paginate.from(zipCodes, zipCodeResponses));
    }

    @GetMapping(path = "/zip-codes-service-logs/{id}")
    public ResponseEntity<ZipCodeResponse> findById(@PathVariable UUID id) {
        ZipCode zipCode = zipCodeService.findById(id);
        Address address = getAddress(zipCode);
        ZipCodeResponse zipCodeResponse = ZipCodeResponse.builder()
                .id(zipCode.getId())
                .zipCode(zipCode.getZipCode())
                .serviceName(zipCode.getServiceName())
                .address(address)
                .request(zipCode.getRequest())
                .response(zipCode.getResponse())
                .externalResponse(zipCode.getExternalResponse())
                .createdAt(zipCode.getCreatedAt())
                .build();

        return ResponseEntity.ok(zipCodeResponse);
    }

    private Address getAddress(ZipCode zipCode) {
        Address address;

        try {
            address = objectMapper.readValue(zipCode.getResponse(), Address.class);
        } catch (Exception e) {
            throw new RuntimeException(Message.FAILED_TO_ENCODE_RESPONSE, e);
        }

        return address;
    }
}
