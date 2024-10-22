package com.santander.address.api.handlers;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.santander.address.api.enums.ServiceName;
import com.santander.address.api.responses.Address;
import com.santander.address.api.responses.ViaCepResponse;
import com.santander.address.api.responses.ZipServiceChainResponse;
import com.santander.address.api.utils.Message;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.time.Duration;

@Component
public class ViaCepHandler implements ZipServiceHandler {

    private ZipServiceHandler nextHandler;
    private final RedisTemplate<String, Object> redisTemplate;
    private final ObjectMapper objectMapper;

    public ViaCepHandler(RedisTemplate<String, Object> redisTemplate, ObjectMapper objectMapper) {
        this.redisTemplate = redisTemplate;
        this.objectMapper = objectMapper;
    }

    @Override
    public ZipServiceChainResponse handleRequest(String zipCode) {
        String apiUrl = "https://viacep.com.br/ws/" + zipCode + "/json/";

        RestTemplate restTemplate = new RestTemplate();
        String response = restTemplate.getForObject(apiUrl, String.class);

        if (response == null) {
            return nextHandler != null ? nextHandler.handleRequest(zipCode) : null;
        }

        try {
            ViaCepResponse viaCepResponse = objectMapper.readValue(response, ViaCepResponse.class);

            if (!isValidResponse(viaCepResponse)) {
                logError(Message.RESPONSE_NOT_MATCH + response);
            }

            String cacheKey = "viacep-" + zipCode;
            int MAX_CACHE_TIME_IN_MINUTES = 5;
            redisTemplate.opsForValue().set(cacheKey, response, Duration.ofMinutes(MAX_CACHE_TIME_IN_MINUTES));

            Address address = Address.builder()
                    .code(viaCepResponse.getCep())
                    .street(viaCepResponse.getLogradouro())
                    .complement(viaCepResponse.getComplemento())
                    .neighborhood(viaCepResponse.getBairro())
                    .city(viaCepResponse.getLocalidade())
                    .state(viaCepResponse.getEstado())
                    .build();

            return ZipServiceChainResponse.builder()
                    .serviceName(ServiceName.VIA_CEP)
                    .address(address)
                    .externalResponse(response)
                    .build();
        } catch (Exception e) {
            throw new RuntimeException(Message.FAILED_TO_ENCODE_RESPONSE, e);
        }
    }

    private boolean isValidResponse(ViaCepResponse response) {
        return response.getCep() != null && !response.getCep().isEmpty();
    }

    private void logError(String message) {
        System.err.println(message);
    }

    @Override
    public void setNextHandler(ZipServiceHandler nextHandler) {
        this.nextHandler = nextHandler;
    }
}
