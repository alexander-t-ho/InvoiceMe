package com.invoiceme.api.payments;

import com.invoiceme.application.payments.schedule.GetPaymentScheduleHandler;
import com.invoiceme.application.payments.schedule.GetPaymentScheduleQuery;
import com.invoiceme.application.payments.schedule.ListUpcomingInstallmentsHandler;
import com.invoiceme.application.payments.schedule.ListUpcomingInstallmentsQuery;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

/**
 * REST controller for Payment Schedule operations.
 */
@RestController
@RequestMapping("/api/v1/payment-schedules")
@Tag(name = "Payment Schedules", description = "Payment schedule management API")
public class PaymentScheduleController {
    
    private final GetPaymentScheduleHandler getPaymentScheduleHandler;
    private final ListUpcomingInstallmentsHandler listUpcomingInstallmentsHandler;
    
    public PaymentScheduleController(
            GetPaymentScheduleHandler getPaymentScheduleHandler,
            ListUpcomingInstallmentsHandler listUpcomingInstallmentsHandler) {
        this.getPaymentScheduleHandler = getPaymentScheduleHandler;
        this.listUpcomingInstallmentsHandler = listUpcomingInstallmentsHandler;
    }
    
    @GetMapping("/invoices/{invoiceId}")
    @Operation(summary = "Get payment schedule for invoice", description = "Retrieves the payment schedule for an invoice")
    @ApiResponse(responseCode = "200", description = "Payment schedule found")
    public ResponseEntity<List<PaymentScheduleResponse>> getPaymentSchedule(
            @Parameter(description = "Invoice ID") @PathVariable UUID invoiceId) {
        GetPaymentScheduleQuery query = new GetPaymentScheduleQuery(invoiceId);
        var result = getPaymentScheduleHandler.handle(query);
        
        List<PaymentScheduleResponse> response = result.stream()
                .map(dto -> new PaymentScheduleResponse(
                    dto.id(),
                    dto.invoiceId(),
                    dto.installmentNumber(),
                    dto.amount(),
                    dto.dueDate(),
                    dto.status(),
                    dto.createdAt()
                ))
                .toList();
        return ResponseEntity.ok(response);
    }
    
    @GetMapping("/upcoming")
    @Operation(summary = "List upcoming installments", description = "Lists all upcoming installments up to a certain date")
    @ApiResponse(responseCode = "200", description = "Upcoming installments retrieved successfully")
    public ResponseEntity<List<PaymentScheduleResponse>> listUpcomingInstallments(
            @Parameter(description = "Up to date") @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate upToDate) {
        ListUpcomingInstallmentsQuery query = new ListUpcomingInstallmentsQuery(upToDate);
        var result = listUpcomingInstallmentsHandler.handle(query);
        
        List<PaymentScheduleResponse> response = result.stream()
                .map(dto -> new PaymentScheduleResponse(
                    dto.id(),
                    dto.invoiceId(),
                    dto.installmentNumber(),
                    dto.amount(),
                    dto.dueDate(),
                    dto.status(),
                    dto.createdAt()
                ))
                .toList();
        return ResponseEntity.ok(response);
    }
    
    public record PaymentScheduleResponse(
        UUID id,
        UUID invoiceId,
        int installmentNumber,
        java.math.BigDecimal amount,
        java.time.LocalDate dueDate,
        String status,
        java.time.LocalDateTime createdAt
    ) {
    }
}



