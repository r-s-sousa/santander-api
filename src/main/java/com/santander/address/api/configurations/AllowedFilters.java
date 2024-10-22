package com.santander.address.api.configurations;

import com.santander.address.api.utils.AllowedFilter;
import com.santander.address.api.utils.DynamicFilter;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@AllArgsConstructor
public class AllowedFilters {

    public static void addFiltersWithPrefix(String prefix, List<AllowedFilter> actualFilters, List<AllowedFilter> newFilters) {
        for (AllowedFilter filter : newFilters) {
            String newKey = "%s.%s".formatted(prefix, filter.getKey());
            filter.setKey(newKey);
            actualFilters.add(filter);
        }
    }

    public static List<AllowedFilter> zipCode() {
        List<AllowedFilter> allowedFilters = new ArrayList<>();
        DynamicFilter.addIdFilters(allowedFilters, false, "id");
        DynamicFilter.addStringFilters(allowedFilters, false, "zipcode", "service_name", "request", "response");
        DynamicFilter.addDateFilters(allowedFilters, false, "createdAt");
        return allowedFilters;
    }

    public static List<AllowedFilter> product() {
        List<AllowedFilter> allowedFilters = new ArrayList<>();
        DynamicFilter.addIdFilters(allowedFilters, false, "id");
        DynamicFilter.addStringFilters(allowedFilters, false, "name", "description");
        DynamicFilter.addDateFilters(allowedFilters, false, "createdAt", "updatedAt");
        DynamicFilter.addDateFilters(allowedFilters, true, "deletedAt");
        return allowedFilters;
    }
}
