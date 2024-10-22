package com.santander.address.api.enums;

public enum ServiceName {

    MEMORY_VIA_CEP("memory-via-cep"),
    VIA_CEP("via-cep");

    private final String serviceName;

    ServiceName(String serviceName) {
        this.serviceName = serviceName;
    }

    @Override
    public String toString() {
        return serviceName;
    }
}
