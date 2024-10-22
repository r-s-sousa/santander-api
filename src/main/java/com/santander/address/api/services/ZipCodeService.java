package com.santander.address.api.services;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.santander.address.api.handlers.ZipServiceChain;
import com.santander.address.api.configurations.AllowedFilters;
import com.santander.address.api.entities.ZipCode;
import com.santander.address.api.exceptions.BadRequestException;
import com.santander.address.api.exceptions.NotFoundException;
import com.santander.address.api.repositories.ZipCodeRepository;
import com.santander.address.api.requests.ZipCodeRequest;
import com.santander.address.api.responses.ZipServiceChainResponse;
import com.santander.address.api.specifications.GenericSpecification;
import com.santander.address.api.utils.FilterItem;
import com.santander.address.api.utils.Message;
import com.santander.address.api.utils.Paginate;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@AllArgsConstructor
@Service
public class ZipCodeService {

    private final ZipServiceChain zipServiceChain;
    private final ZipCodeRepository zipCodeRepository;
    private final ObjectMapper objectMapper;

    public Page<ZipCode> findAll(Integer page, Integer size, List<FilterItem> filters) {
        Pageable pageable = Paginate.from(page, size);
        GenericSpecification<ZipCode> specification = GenericSpecification.from(filters, AllowedFilters.zipCode());
        return zipCodeRepository.findAll(specification, pageable);
    }

    public ZipCode findById(UUID id) {
        return zipCodeRepository.findById(id).orElseThrow(() -> new NotFoundException(Message.ZIP_CODE_TRAIL_NOT_FOUND));
    }

    @Transactional
    public ZipCode search(ZipCodeRequest zipCodeRequest) {
        if (zipCodeRequest.getZipCode() == null) {
            throw new BadRequestException(Message.INVALID_REQUEST);
        }

        ZipServiceChainResponse zipServiceChainResponse = zipServiceChain.search(zipCodeRequest.getZipCode());

        if (zipServiceChainResponse == null) {
            throw new NotFoundException(Message.ZIP_CODE_NOT_FOUND);
        }

        String requestJson;

        try {
            requestJson = objectMapper.writeValueAsString(zipCodeRequest);
        } catch (Exception e) {
            throw new RuntimeException(Message.FAILED_TO_ENCODE_REQUEST, e);
        }

        String addressJson;

        try {
            addressJson = objectMapper.writeValueAsString(zipServiceChainResponse.getAddress());
        } catch (Exception e) {
            throw new RuntimeException(Message.FAILED_TO_ENCODE_RESPONSE, e);
        }

        ZipCode zipCode = ZipCode.builder()
                .zipCode(zipCodeRequest.getZipCode())
                .serviceName(zipServiceChainResponse.getServiceName().toString())
                .request(requestJson)
                .externalResponse(zipServiceChainResponse.getExternalResponse())
                .response(addressJson)
                .createdAt(LocalDateTime.now())
                .build();

        return zipCodeRepository.save(zipCode);
    }
}
