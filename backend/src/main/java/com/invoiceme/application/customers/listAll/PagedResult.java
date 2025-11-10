package com.invoiceme.application.customers.listAll;

import java.util.List;

/**
 * Generic paginated result DTO.
 */
public record PagedResult<T>(
    List<T> content,
    int page,
    int size,
    long totalElements,
    int totalPages,
    boolean hasNext,
    boolean hasPrevious
) {
    public static <T> PagedResult<T> of(List<T> content, int page, int size, long totalElements) {
        int totalPages = (int) Math.ceil((double) totalElements / size);
        return new PagedResult<>(
            content,
            page,
            size,
            totalElements,
            totalPages,
            page < totalPages - 1,
            page > 0
        );
    }
}

