package com.santander.address.api.handlers;

import com.santander.address.api.responses.ZipServiceChainResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class ZipServiceChain {

    private final MemoryHandler memoryHandler;

    @Autowired
    public ZipServiceChain(MemoryHandler memoryHandler, ViaCepHandler viaCepHandler) {
        this.memoryHandler = memoryHandler;
        memoryHandler.setNextHandler(viaCepHandler);
    }

    public ZipServiceChainResponse search(String zipCode) {
        return memoryHandler.handleRequest(zipCode);
    }
}
