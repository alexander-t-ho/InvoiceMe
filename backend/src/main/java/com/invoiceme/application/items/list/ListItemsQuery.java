package com.invoiceme.application.items.list;

import java.util.UUID;

/**
 * Query to list items for a user.
 */
public record ListItemsQuery(
    UUID userId,
    int page,
    int size
) {
}



