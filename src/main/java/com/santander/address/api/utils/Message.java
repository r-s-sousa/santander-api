package com.santander.address.api.utils;

public class Message {
    public static final String INTERNAL_SERVER_ERROR = "Internal server error";

    public static final String PRODUCT_CONFLICT = "Product already exists";
    public static final String PRODUCT_NOT_FOUND = "Product not found";

    public static final String UNAUTHORIZED = "Unauthorized";
    public static final String INVALID_REQUEST = "Invalid request";
    public static final String NO_ALLOWED_FILTERS_DEFINED = "No allowed filters are defined. Please ensure that 'allowedFilters' is properly configured.";
    public static final String INVALID_FILTER = "Invalid filter: %s with operation %s";
    public static final String FILTER_WITH_EMPTY_VALUE = "Value cannot be empty";

    public static final String VALIDATION_ERROR = "Validation error";

}
