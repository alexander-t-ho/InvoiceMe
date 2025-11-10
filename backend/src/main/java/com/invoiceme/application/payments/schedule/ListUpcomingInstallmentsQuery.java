package com.invoiceme.application.payments.schedule;

import jakarta.validation.constraints.NotNull;

import java.time.LocalDate;

/**
 * Query to list upcoming installments.
 */
public record ListUpcomingInstallmentsQuery(
    @NotNull(message = "Up to date is required")
    LocalDate upToDate
) {
}



