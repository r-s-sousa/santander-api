package com.santander.address.api.handlers;

import com.santander.address.api.responses.ZipServiceChainResponse;

public interface ZipServiceHandler {

    ZipServiceChainResponse handleRequest(String zipCode);

    void setNextHandler(ZipServiceHandler nextHandler);
}
