package com.santander.address.api.handlers;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.santander.address.api.enums.ServiceName;
import com.santander.address.api.responses.Address;
import com.santander.address.api.responses.ViaCepResponse;
import com.santander.address.api.responses.ZipServiceChainResponse;
import com.santander.address.api.utils.Message;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

@Component
public class MemoryHandler implements ZipServiceHandler {

    private ZipServiceHandler nextHandler;
    private final ObjectMapper objectMapper;

    private final RedisTemplate<String, Object> redisTemplate;

    public MemoryHandler(RedisTemplate<String, Object> redisTemplate, ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
        this.redisTemplate = redisTemplate;
    }

    @Override
    public ZipServiceChainResponse handleRequest(String zipCode) {
        ZipServiceChainResponse viaCepMemoryCache = getViaCepMemoryCache(zipCode);
        // ... another handlers cached

        if (viaCepMemoryCache == null) {
            return nextHandler != null ? nextHandler.handleRequest(zipCode) : null;
        }

        return viaCepMemoryCache;
    }

    private ZipServiceChainResponse getViaCepMemoryCache(String zipCode) {
        String cacheKey = "viacep-" + zipCode;
        String viaCepCachedCep = (String) redisTemplate.opsForValue().get(cacheKey);

        if (viaCepCachedCep == null) {
            return null;
        }

        try {
            ViaCepResponse viaCepResponse = objectMapper.readValue(viaCepCachedCep, ViaCepResponse.class);

            Address address = Address.builder()
                    .code(viaCepResponse.getCep())
                    .street(viaCepResponse.getLogradouro())
                    .complement(viaCepResponse.getComplemento())
                    .neighborhood(viaCepResponse.getBairro())
                    .city(viaCepResponse.getLocalidade())
                    .state(viaCepResponse.getEstado())
                    .build();

            return ZipServiceChainResponse.builder()
                    .serviceName(ServiceName.MEMORY_VIA_CEP)
                    .externalResponse(viaCepCachedCep)
                    .address(address)
                    .build();
        } catch (Exception e) {
            throw new RuntimeException(Message.FAILED_TO_ENCODE_RESPONSE, e);
        }
    }

    @Override
    public void setNextHandler(ZipServiceHandler nextHandler) {
        this.nextHandler = nextHandler;
    }
}
