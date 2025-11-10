package com.invoiceme.application.items.getById;

import java.util.UUID;

/**
 * Query to get an item by ID.
 */
public record GetItemByIdQuery(
    UUID itemId,
    UUID userId
) {
}



