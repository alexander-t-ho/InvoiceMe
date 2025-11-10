package com.invoiceme.api.common;

import java.util.List;

/**
 * Generic paginated response DTO for API.
 */
public record PagedResponse<T>(
    List<T> content,
    int page,
    int size,
    long totalElements,
    int totalPages,
    boolean hasNext,
    boolean hasPrevious
) {
    public static <T> PagedResponse<T> of(List<T> content, int page, int size, long totalElements) {
        int totalPages = (int) Math.ceil((double) totalElements / size);
        return new PagedResponse<>(
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

