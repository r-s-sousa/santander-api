package com.santander.address.api.specifications;

import com.santander.address.api.enums.FilterOperation;
import com.santander.address.api.exceptions.BadRequestException;
import com.santander.address.api.utils.AllowedFilter;
import com.santander.address.api.utils.DynamicFilter;
import com.santander.address.api.utils.FilterItem;
import com.santander.address.api.utils.Message;
import jakarta.persistence.criteria.*;
import lombok.NonNull;
import org.springframework.data.jpa.domain.Specification;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

public class GenericSpecification<T> implements Specification<T> {

    protected final List<FilterItem> filterItems;
    protected final List<Predicate> predicates;
    protected CriteriaBuilder criteriaBuilder;
    protected Root<T> root;
    protected final List<AllowedFilter> allowedFilters;

    protected GenericSpecification(List<FilterItem> filterItems, List<AllowedFilter> allowedFilters) {
        this.predicates = new ArrayList<>();
        this.filterItems = filterItems;
        this.allowedFilters = allowedFilters;
    }

    public static <T> GenericSpecification<T> from(List<FilterItem> filterItems, List<AllowedFilter> allowedFilters) {
        return new GenericSpecification<>(filterItems, allowedFilters);
    }

    @Override
    public Predicate toPredicate(@NonNull Root<T> root, CriteriaQuery<?> query, CriteriaBuilder criteriaBuilder) {
        predicates.clear();

        this.criteriaBuilder = criteriaBuilder;
        this.root = root;

        verifyFiltersInAllowedFilters();
        processFilterElements();

        return criteriaBuilder.and(predicates.toArray(new Predicate[0]));
    }

    protected void verifyFiltersInAllowedFilters() {
        if (allowedFilters.isEmpty()) {
            throw new IllegalArgumentException(Message.NO_ALLOWED_FILTERS_DEFINED);
        }

        for (FilterItem filterItem : filterItems) {
            boolean isValid = allowedFilters.stream()
                    .anyMatch(allowedFilter -> allowedFilter.getKey().equals(filterItem.getKey()) &&
                            allowedFilter.getOperation().equals(filterItem.getOperation()));

            if (!isValid) {
                throw new IllegalArgumentException(String.format(Message.INVALID_FILTER, filterItem.getKey(), filterItem.getOperation()));
            }
        }
    }

    protected void processFilterElements() {
        for (FilterItem filterItem : filterItems) {
            processFilterElement(filterItem);
        }
    }

    protected void processFilterElement(FilterItem filterItem) {
        String[] keys = filterItem.getKey().split("\\.");
        Path path;

        if (keys.length == 2) {
            String tableToJoin = keys[0];
            String columnName = keys[1];
            Join<Object, Object> join = root.join(tableToJoin); // join with desired entity
            path = join.get(columnName);
        } else {
            path = root.get(filterItem.getKey());
        }

        if (filterItem.getValue().toString().isEmpty()) {
            throw new IllegalArgumentException(Message.FILTER_WITH_EMPTY_VALUE);
        }

        if (filterItem.getOperation() == FilterOperation.IS_NULL || filterItem.getOperation() == FilterOperation.IS_NOT_NULL) {
            boolean booleanValue = Boolean.parseBoolean(filterItem.getValue().toString());
            addFilterToPredicateNullCase(path, filterItem.getOperation(), booleanValue);
            return;
        }

        if (processBoolean(filterItem, path)) return;
        if (processDateTime(filterItem, path)) return;
        if (processNumber(filterItem, path)) return;
        if (processUUID(filterItem, path)) return;
        if (processString(filterItem, path)) return;

        throw new BadRequestException(Message.INVALID_REQUEST);
    }

    protected boolean processUUID(FilterItem filterItem, Path path) {
        if (!DynamicFilter.isUUIDField(path)) {
            return false;
        }

        UUID uuidValue = UUID.fromString(filterItem.getValue().toString());
        addFilterToPredicate(path, filterItem.getOperation(), uuidValue);
        return true;
    }

    protected boolean processString(FilterItem filterItem, Path path) {
        if (!DynamicFilter.isStringField(path)) {
            return false;
        }

        String stringValue = filterItem.getValue().toString();
        addFilterToPredicate(path, filterItem.getOperation(), stringValue);
        return true;
    }

    protected boolean processDateTime(FilterItem filterItem, Path path) {
        if (!DynamicFilter.isDateTimeField(path)) {
            return false;
        }

        String value = filterItem.getValue().toString();

        if (value.matches("\\d{4}-\\d{2}-\\d{2}")) {
            LocalDate dateValue = DynamicFilter.convertToLocalDate(value);
            addFilterToPredicate(path, filterItem.getOperation(), dateValue);
            return true;
        }

        LocalDateTime dateTimeValue = DynamicFilter.convertToLocalDateTime(filterItem.getValue().toString());
        addFilterToPredicate(path, filterItem.getOperation(), dateTimeValue);
        return true;
    }

    protected boolean processNumber(FilterItem filterItem, Path path) {
        if (!DynamicFilter.isNumberField(path)) {
            return false;
        }

        String value = filterItem.getValue().toString();
        if (processInteger(filterItem, value, path)) return true;
        if (processLong(filterItem, value, path)) return true;
        if (processFloat(filterItem, value, path)) return true;
        if (processDouble(filterItem, value, path)) return true;

        return false;
    }

    protected boolean processDouble(FilterItem filterItem, String value, Path path) {
        if (!DynamicFilter.isDoubleField(path)) {
            return false;
        }

        Double doubleValue = Double.parseDouble(value);
        addFilterToPredicate(path, filterItem.getOperation(), doubleValue);
        return true;
    }

    protected boolean processFloat(FilterItem filterItem, String value, Path path) {
        if (!DynamicFilter.isFloatField(path)) {
            return false;
        }

        Float floatValue = Float.parseFloat(value);
        addFilterToPredicate(path, filterItem.getOperation(), floatValue);
        return true;
    }

    protected boolean processLong(FilterItem filterItem, String value, Path path) {
        if (!DynamicFilter.isLongField(path)) {
            return false;
        }

        Long longValue = Long.parseLong(value);
        addFilterToPredicate(path, filterItem.getOperation(), longValue);
        return true;
    }

    protected boolean processInteger(FilterItem filterItem, String value, Path path) {
        if (!DynamicFilter.isIntegerField(path)) {
            return false;
        }

        Integer integerValue = Integer.parseInt(value);
        addFilterToPredicate(path, filterItem.getOperation(), integerValue);
        return true;
    }

    protected boolean processBoolean(FilterItem filterItem, Path path) {
        if (!DynamicFilter.isBooleanField(path)) {
            return false;
        }

        String value = filterItem.getValue().toString();

        Boolean booleanValue = Boolean.parseBoolean(value);
        addFilterToPredicate(path, filterItem.getOperation(), booleanValue);
        return true;
    }

    public void addFilterToPredicate(Path path, FilterOperation operation, Integer value) {
        switch (operation) {
            case FilterOperation.EQUAL:
                predicates.add(criteriaBuilder.equal(path, value));
                break;
            case FilterOperation.GREATER_THAN:
                predicates.add(criteriaBuilder.greaterThan(path, value));
                break;
            case FilterOperation.GREATER_THAN_OR_EQUAL:
                predicates.add(criteriaBuilder.greaterThanOrEqualTo(path, value));
                break;
            case FilterOperation.LESS_THAN:
                predicates.add(criteriaBuilder.lessThan(path, value));
                break;
            case FilterOperation.LESS_THAN_OR_EQUAL:
                predicates.add(criteriaBuilder.lessThanOrEqualTo(path, value));
                break;
        }
    }

    public void addFilterToPredicate(Path path, FilterOperation operation, Long value) {
        switch (operation) {
            case FilterOperation.EQUAL:
                predicates.add(criteriaBuilder.equal(path, value));
                break;
            case FilterOperation.GREATER_THAN:
                predicates.add(criteriaBuilder.greaterThan(path, value));
                break;
            case FilterOperation.GREATER_THAN_OR_EQUAL:
                predicates.add(criteriaBuilder.greaterThanOrEqualTo(path, value));
                break;
            case FilterOperation.LESS_THAN:
                predicates.add(criteriaBuilder.lessThan(path, value));
                break;
            case FilterOperation.LESS_THAN_OR_EQUAL:
                predicates.add(criteriaBuilder.lessThanOrEqualTo(path, value));
                break;
        }
    }

    public void addFilterToPredicate(Path path, FilterOperation operation, Double value) {
        switch (operation) {
            case FilterOperation.EQUAL:
                predicates.add(criteriaBuilder.equal(path, value));
                break;
            case FilterOperation.GREATER_THAN:
                predicates.add(criteriaBuilder.greaterThan(path, value));
                break;
            case FilterOperation.GREATER_THAN_OR_EQUAL:
                predicates.add(criteriaBuilder.greaterThanOrEqualTo(path, value));
                break;
            case FilterOperation.LESS_THAN:
                predicates.add(criteriaBuilder.lessThan(path, value));
                break;
            case FilterOperation.LESS_THAN_OR_EQUAL:
                predicates.add(criteriaBuilder.lessThanOrEqualTo(path, value));
                break;
        }
    }

    public void addFilterToPredicate(Path path, FilterOperation operation, Float value) {
        switch (operation) {
            case FilterOperation.EQUAL:
                predicates.add(criteriaBuilder.equal(path, value));
                break;
            case FilterOperation.GREATER_THAN:
                predicates.add(criteriaBuilder.greaterThan(path, value));
                break;
            case FilterOperation.GREATER_THAN_OR_EQUAL:
                predicates.add(criteriaBuilder.greaterThanOrEqualTo(path, value));
                break;
            case FilterOperation.LESS_THAN:
                predicates.add(criteriaBuilder.lessThan(path, value));
                break;
            case FilterOperation.LESS_THAN_OR_EQUAL:
                predicates.add(criteriaBuilder.lessThanOrEqualTo(path, value));
                break;
        }
    }

    public void addFilterToPredicate(Path path, FilterOperation operation, LocalDateTime value) {
        switch (operation) {
            case FilterOperation.EQUAL:
                predicates.add(criteriaBuilder.equal(path, value));
                break;
            case FilterOperation.GREATER_THAN:
                predicates.add(criteriaBuilder.greaterThan(path, value));
                break;
            case FilterOperation.GREATER_THAN_OR_EQUAL:
                predicates.add(criteriaBuilder.greaterThanOrEqualTo(path, value));
                break;
            case FilterOperation.LESS_THAN:
                predicates.add(criteriaBuilder.lessThan(path, value));
                break;
            case FilterOperation.LESS_THAN_OR_EQUAL:
                predicates.add(criteriaBuilder.lessThanOrEqualTo(path, value));
                break;
        }
    }

    public void addFilterToPredicate(Path path, FilterOperation operation, LocalDate value) {
        switch (operation) {
            case FilterOperation.EQUAL:
                LocalDateTime startOfDay = value.atStartOfDay();
                LocalDateTime endOfDay = value.atTime(23, 59, 59, 999999999);
                predicates.add(criteriaBuilder.between(path, startOfDay, endOfDay));
                break;

            case FilterOperation.GREATER_THAN:
                LocalDateTime endOfDayToGreaterThan = value.atTime(23, 59, 59, 999999999);
                predicates.add(criteriaBuilder.greaterThan(path, endOfDayToGreaterThan));
                break;

            case FilterOperation.GREATER_THAN_OR_EQUAL:
                LocalDateTime startOfDayForGreaterThanOrEqual = value.atStartOfDay();
                predicates.add(criteriaBuilder.greaterThanOrEqualTo(path, startOfDayForGreaterThanOrEqual));
                break;

            case FilterOperation.LESS_THAN:
                LocalDateTime startOfDayForLessThan = value.atStartOfDay();
                predicates.add(criteriaBuilder.lessThan(path, startOfDayForLessThan));
                break;

            case FilterOperation.LESS_THAN_OR_EQUAL:
                LocalDateTime endOfDayForLessThanOrEqual = value.atTime(23, 59, 59, 999999999);
                predicates.add(criteriaBuilder.lessThanOrEqualTo(path, endOfDayForLessThanOrEqual));
                break;
        }
    }

    public void addFilterToPredicate(Path path, FilterOperation operation, String value) {
        switch (operation) {
            case FilterOperation.EQUAL:
                predicates.add(criteriaBuilder.equal(path, value));
                break;
            case FilterOperation.NOT_EQUAL:
                predicates.add(criteriaBuilder.notEqual(path, value));
                break;
            case FilterOperation.LIKE:
                predicates.add(criteriaBuilder.like(path, "%" + value + "%"));
                break;
            case FilterOperation.NOT_LIKE:
                predicates.add(criteriaBuilder.notLike(path, "%" + value + "%"));
                break;
            case FilterOperation.IN:
                String[] inValues = value.split("\\s*,\\s*");
                predicates.add(path.in(Arrays.asList(inValues)));
                break;
            case FilterOperation.NOT_IN:
                String[] ninInvalues = value.split("\\s*,\\s*");
                predicates.add(criteriaBuilder.not(path.in(Arrays.asList(ninInvalues))));
                break;
        }
    }

    public void addFilterToPredicate(Path path, FilterOperation operation, UUID value) {
        switch (operation) {
            case FilterOperation.EQUAL:
                predicates.add(criteriaBuilder.equal(path, value));
                break;
            case FilterOperation.NOT_EQUAL:
                predicates.add(criteriaBuilder.notEqual(path, value));
                break;
        }
    }

    public void addFilterToPredicate(Path path, FilterOperation operation, Boolean value) {
        switch (operation) {
            case FilterOperation.EQUAL:
                predicates.add(criteriaBuilder.equal(path, value));
                break;
            case FilterOperation.NOT_EQUAL:
                predicates.add(criteriaBuilder.notEqual(path, value));
                break;
        }
    }

    public void addFilterToPredicateNullCase(Path path, FilterOperation operation, boolean logic) {
        switch (operation) {
            case FilterOperation.IS_NULL:
                if (logic) {
                    predicates.add(criteriaBuilder.isNull(path));
                } else {
                    predicates.add(criteriaBuilder.isNotNull(path));
                }
                break;
            case FilterOperation.IS_NOT_NULL:
                if (logic) {
                    predicates.add(criteriaBuilder.isNotNull(path));
                } else {
                    predicates.add(criteriaBuilder.isNull(path));
                }
                break;
        }
    }
}
