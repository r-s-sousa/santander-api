package com.santander.address.api.utils;

public class Message {

    public static final String INTERNAL_SERVER_ERROR = "Internal server error";
    public static final String INVALID_REQUEST = "Invalid request";

    public static final String PRODUCT_CONFLICT = "Product already exists";
    public static final String PRODUCT_NOT_FOUND = "Product not found";

    public static final String ZIP_CODE_TRAIL_NOT_FOUND = "Zip code trail not found";
    public static final String ZIP_CODE_NOT_FOUND = "Zip code not found";
    public static final String FAILED_TO_ENCODE_REQUEST = "Failed to encode request to JSON";
    public static final String FAILED_TO_ENCODE_RESPONSE = "Failed to encode response to JSON";
    public static final String RESPONSE_NOT_MATCH = "Response does not match the expected schema";

    public static final String NO_ALLOWED_FILTERS_DEFINED = "No allowed filters are defined. Please ensure that 'allowedFilters' is properly configured.";
    public static final String INVALID_FILTER = "Invalid filter: %s with operation %s";
    public static final String FILTER_WITH_EMPTY_VALUE = "Value cannot be empty";
    public static final String VALIDATION_ERROR = "Validation error";
}
