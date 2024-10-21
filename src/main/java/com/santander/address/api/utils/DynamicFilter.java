package com.santander.address.api.utils;

import com.santander.address.api.enums.FilterOperation;
import jakarta.persistence.criteria.Path;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public class DynamicFilter {

    private static final DateTimeFormatter DATE_TIME_FORMATTER = DateTimeFormatter.ISO_DATE_TIME;
    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ISO_LOCAL_DATE;

    public static List<FilterItem> parseFilters(Map<String, String> params) {
        List<FilterItem> filters = new ArrayList<>();
        params.forEach((key, value) -> {
            String[] parts = key.split("[\\[\\]]"); // split fieldName[condition]
            if (parts.length == 2) {
                String fieldName = convertSnakeToCamel(parts[0]);
                String operationString = parts[1];
                FilterOperation filterOperation = FilterOperation.fromString(operationString);
                filters.add(new FilterItem(fieldName, filterOperation, value));
            }
        });

        return filters;
    }

    public static String convertSnakeToCamel(String snakeCase) {
        StringBuilder camelCase = new StringBuilder();
        boolean nextUpperCase = false;

        for (char c : snakeCase.toCharArray()) {
            if (c == '_') {
                nextUpperCase = true;
            } else if (nextUpperCase) {
                camelCase.append(Character.toUpperCase(c));
                nextUpperCase = false;
            } else {
                camelCase.append(c);
            }
        }

        return camelCase.toString();
    }

    public static String convertCamelToSnake(String camelCase) {
        StringBuilder snakeCase = new StringBuilder();

        for (char c : camelCase.toCharArray()) {
            if (Character.isUpperCase(c)) {
                snakeCase.append('_').append(Character.toLowerCase(c));
            } else {
                snakeCase.append(c);
            }
        }

        return snakeCase.toString();
    }

    public static boolean isNumberField(Path path) {
        Class<?> fieldType = path.getJavaType();
        return Number.class.isAssignableFrom(fieldType) || fieldType.isPrimitive();
    }

    public static boolean isIntegerField(Path path) {
        Class<?> fieldType = path.getJavaType();
        return fieldType.equals(Integer.class) || fieldType.equals(int.class);
    }

    public static boolean isLongField(Path path) {
        Class<?> fieldType = path.getJavaType();
        return fieldType.equals(Long.class) || fieldType.equals(long.class);
    }

    public static boolean isFloatField(Path path) {
        Class<?> fieldType = path.getJavaType();
        return fieldType.equals(Float.class) || fieldType.equals(float.class);
    }

    public static boolean isDoubleField(Path path) {
        Class<?> fieldType = path.getJavaType();
        return fieldType.equals(Double.class) || fieldType.equals(double.class);
    }

    public static boolean isBooleanField(Path path) {
        Class<?> fieldType = path.getJavaType();
        return fieldType.equals(Boolean.class) || fieldType.equals(boolean.class);
    }

    public static boolean isDateTimeField(Path path) {
        return path.getJavaType().equals(LocalDateTime.class);
    }

    public static boolean isUUIDField(Path path) {
        return path.getJavaType().equals(UUID.class);
    }

    public static boolean isStringField(Path path) {
        return path.getJavaType().equals(String.class);
    }

    public static LocalDateTime convertToLocalDateTime(String value) {
        return LocalDateTime.parse(value, DATE_TIME_FORMATTER);
    }

    public static LocalDate convertToLocalDate(String value) {
        return LocalDate.parse(value, DATE_FORMATTER);
    }

    public static void addIdFilters(List<AllowedFilter> filters, boolean acceptNullOperations, String... fieldNames) {
        for (String fieldName : fieldNames) {
            filters.add(new AllowedFilter(fieldName, FilterOperation.EQUAL));
            filters.add(new AllowedFilter(fieldName, FilterOperation.NOT_EQUAL));

            if (acceptNullOperations) {
                addNullOperations(filters, fieldName);
            }
        }
    }

    public static void addStringFilters(List<AllowedFilter> filters, boolean acceptNullOperations, String... fieldNames) {
        for (String fieldName : fieldNames) {
            filters.add(new AllowedFilter(fieldName, FilterOperation.EQUAL));
            filters.add(new AllowedFilter(fieldName, FilterOperation.NOT_EQUAL));
            filters.add(new AllowedFilter(fieldName, FilterOperation.LIKE));
            filters.add(new AllowedFilter(fieldName, FilterOperation.NOT_LIKE));
            filters.add(new AllowedFilter(fieldName, FilterOperation.IN));
            filters.add(new AllowedFilter(fieldName, FilterOperation.NOT_IN));

            if (acceptNullOperations) {
                addNullOperations(filters, fieldName);
            }
        }
    }

    public static void addBooleanFilters(List<AllowedFilter> filters, boolean acceptNullOperations, String... fieldNames) {
        for (String fieldName : fieldNames) {
            filters.add(new AllowedFilter(fieldName, FilterOperation.EQUAL));
            filters.add(new AllowedFilter(fieldName, FilterOperation.NOT_EQUAL));

            if (acceptNullOperations) {
                addNullOperations(filters, fieldName);
            }
        }
    }

    public static void addDateFilters(List<AllowedFilter> filters, boolean acceptNullOperations, String... fieldNames) {
        for (String fieldName : fieldNames) {
            filters.add(new AllowedFilter(fieldName, FilterOperation.EQUAL));
            filters.add(new AllowedFilter(fieldName, FilterOperation.GREATER_THAN));
            filters.add(new AllowedFilter(fieldName, FilterOperation.GREATER_THAN_OR_EQUAL));
            filters.add(new AllowedFilter(fieldName, FilterOperation.LESS_THAN));
            filters.add(new AllowedFilter(fieldName, FilterOperation.LESS_THAN_OR_EQUAL));

            if (acceptNullOperations) {
                addNullOperations(filters, fieldName);
            }
        }
    }

    public static void addNumberOperations(List<AllowedFilter> filters, boolean acceptNullOperations, String... fieldNames) {
        for (String fieldName : fieldNames) {
            filters.add(new AllowedFilter(fieldName, FilterOperation.EQUAL));
            filters.add(new AllowedFilter(fieldName, FilterOperation.GREATER_THAN));
            filters.add(new AllowedFilter(fieldName, FilterOperation.GREATER_THAN_OR_EQUAL));
            filters.add(new AllowedFilter(fieldName, FilterOperation.LESS_THAN));
            filters.add(new AllowedFilter(fieldName, FilterOperation.LESS_THAN_OR_EQUAL));

            if (acceptNullOperations) {
                addNullOperations(filters, fieldName);
            }
        }
    }

    public static void addNullOperations(List<AllowedFilter> filters, String fieldName) {
        filters.add(new AllowedFilter(fieldName, FilterOperation.IS_NULL));
        filters.add(new AllowedFilter(fieldName, FilterOperation.IS_NOT_NULL));
    }
}
