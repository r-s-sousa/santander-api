package com.santander.address.api.enums;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public enum FilterOperation {

    EQUAL("eq"),
    NOT_EQUAL("neq"),
    GREATER_THAN("gt"),
    GREATER_THAN_OR_EQUAL("gteq"),
    LESS_THAN("lt"),
    LESS_THAN_OR_EQUAL("lteq"),

    IN("in"),
    NOT_IN("nin"),

    LIKE("like"),
    NOT_LIKE("nlike"),

    IS_NOT_NULL("notnull"),
    IS_NULL("isnull");

    private final String operation;

    FilterOperation(String operation) {
        this.operation = operation;
    }

    @Override
    public String toString() {
        return operation;
    }

    public static List<String> getAllOperations() {
        return Arrays.stream(FilterOperation.values())
                .map(FilterOperation::toString)
                .collect(Collectors.toList());
    }

    public static FilterOperation fromString(String operation) {
        return Arrays.stream(FilterOperation.values())
                .filter(op -> op.operation.equals(operation))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("Invalid filter operation: " + operation));
    }
}
