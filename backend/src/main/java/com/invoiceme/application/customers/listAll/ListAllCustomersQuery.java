package com.invoiceme.application.customers.listAll;

/**
 * Query to list all customers with pagination.
 */
public record ListAllCustomersQuery(
    int page,
    int size,
    String sortBy
) {
    public ListAllCustomersQuery {
        if (page < 0) page = 0;
        if (size < 1) size = 20;
        if (size > 100) size = 100; // Max page size
        if (sortBy == null || sortBy.isBlank()) {
            sortBy = "name";
        }
    }
    
    public ListAllCustomersQuery() {
        this(0, 20, "name");
    }
}

