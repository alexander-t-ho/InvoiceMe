package com.invoiceme.api.payments;

import com.invoiceme.application.payments.getById.GetPaymentByIdQuery;
import com.invoiceme.application.payments.getById.GetPaymentByIdHandler;
import com.invoiceme.application.payments.listByInvoice.ListPaymentsByInvoiceQuery;
import com.invoiceme.application.payments.listByInvoice.ListPaymentsByInvoiceHandler;
import com.invoiceme.application.payments.record.RecordPaymentCommand;
import com.invoiceme.application.payments.record.RecordPaymentHandler;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

/**
 * REST controller for Payment operations.
 */
@RestController
@RequestMapping("/api/v1/payments")
@Tag(name = "Payments", description = "Payment management API")
public class PaymentController {
    
    private final RecordPaymentHandler recordPaymentHandler;
    private final GetPaymentByIdHandler getPaymentByIdHandler;
    private final ListPaymentsByInvoiceHandler listPaymentsByInvoiceHandler;
    
    public PaymentController(
            RecordPaymentHandler recordPaymentHandler,
            GetPaymentByIdHandler getPaymentByIdHandler,
            ListPaymentsByInvoiceHandler listPaymentsByInvoiceHandler) {
        this.recordPaymentHandler = recordPaymentHandler;
        this.getPaymentByIdHandler = getPaymentByIdHandler;
        this.listPaymentsByInvoiceHandler = listPaymentsByInvoiceHandler;
    }
    
    @PostMapping
    @Operation(summary = "Record a payment", description = "Records a payment for an invoice")
    @ApiResponse(responseCode = "201", description = "Payment recorded successfully")
    @ApiResponse(responseCode = "400", description = "Invalid input or business rule violation")
    @ApiResponse(responseCode = "404", description = "Invoice not found")
    public ResponseEntity<PaymentDetailResponse> recordPayment(@Valid @RequestBody RecordPaymentRequest request) {
        RecordPaymentCommand command = new RecordPaymentCommand(
            request.invoiceId(),
            request.amount(),
            request.paymentDate(),
            request.paymentMethod()
        );
        UUID paymentId = recordPaymentHandler.handle(command);
        
        GetPaymentByIdQuery query = new GetPaymentByIdQuery(paymentId);
        var paymentDto = getPaymentByIdHandler.handle(query);
        
        PaymentDetailResponse response = toResponse(paymentDto);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }
    
    @GetMapping("/{id}")
    @Operation(summary = "Get payment by ID", description = "Retrieves a payment by its ID")
    @ApiResponse(responseCode = "200", description = "Payment found")
    @ApiResponse(responseCode = "404", description = "Payment not found")
    public ResponseEntity<PaymentDetailResponse> getPaymentById(
            @Parameter(description = "Payment ID") @PathVariable UUID id) {
        GetPaymentByIdQuery query = new GetPaymentByIdQuery(id);
        var paymentDto = getPaymentByIdHandler.handle(query);
        
        PaymentDetailResponse response = toResponse(paymentDto);
        return ResponseEntity.ok(response);
    }
    
    @GetMapping("/invoices/{invoiceId}")
    @Operation(summary = "List payments by invoice", description = "Retrieves all payments for an invoice")
    @ApiResponse(responseCode = "200", description = "Payments retrieved successfully")
    public ResponseEntity<List<PaymentDetailResponse>> listPaymentsByInvoice(
            @Parameter(description = "Invoice ID") @PathVariable UUID invoiceId) {
        ListPaymentsByInvoiceQuery query = new ListPaymentsByInvoiceQuery(invoiceId);
        var payments = listPaymentsByInvoiceHandler.handle(query);
        
        List<PaymentDetailResponse> response = payments.stream()
                .map(payment -> new PaymentDetailResponse(
                    payment.id(),
                    invoiceId, // Use invoiceId from path parameter
                    invoiceId.toString(), // Use invoiceId as invoiceNumber
                    payment.amount(),
                    payment.paymentDate(),
                    payment.paymentMethod(),
                    payment.createdAt()
                ))
                .toList();
        
        return ResponseEntity.ok(response);
    }
    
    private PaymentDetailResponse toResponse(com.invoiceme.application.payments.getById.PaymentDetailDto dto) {
        return new PaymentDetailResponse(
            dto.id(),
            dto.invoiceId(),
            dto.invoiceNumber(),
            dto.amount(),
            dto.paymentDate(),
            dto.paymentMethod(),
            dto.createdAt()
        );
    }
}

