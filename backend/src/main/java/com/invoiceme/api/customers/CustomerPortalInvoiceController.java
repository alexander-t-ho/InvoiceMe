package com.invoiceme.api.customers;

import com.invoiceme.application.invoices.getById.GetInvoiceByIdQuery;
import com.invoiceme.application.invoices.getById.GetInvoiceByIdHandler;
import com.invoiceme.application.invoices.listByCustomer.ListInvoicesByCustomerQuery;
import com.invoiceme.application.invoices.listByCustomer.ListInvoicesByCustomerHandler;
import com.invoiceme.application.payments.record.RecordPaymentCommand;
import com.invoiceme.application.payments.record.RecordPaymentHandler;
import com.invoiceme.application.payments.getById.GetPaymentByIdQuery;
import com.invoiceme.application.payments.getById.GetPaymentByIdHandler;
import com.invoiceme.application.customers.listAll.PagedResult;
import com.invoiceme.api.common.PagedResponse;
import com.invoiceme.api.invoices.InvoiceResponse;
import com.invoiceme.api.invoices.InvoiceSummaryResponse;
import com.invoiceme.api.invoices.LineItemResponse;
import com.invoiceme.api.invoices.PaymentResponse;
import com.invoiceme.api.payments.PaymentDetailResponse;
import com.invoiceme.api.payments.RecordPaymentRequest;
import com.invoiceme.domain.exceptions.DomainValidationException;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

/**
 * REST controller for Customer Portal invoice operations.
 * This controller ensures customers can only access their own invoices.
 */
@RestController
@RequestMapping("/api/v1/customers/portal/invoices")
@Tag(name = "Customer Portal", description = "Customer portal invoice API")
public class CustomerPortalInvoiceController {
    
    private final GetInvoiceByIdHandler getInvoiceByIdHandler;
    private final ListInvoicesByCustomerHandler listInvoicesByCustomerHandler;
    private final RecordPaymentHandler recordPaymentHandler;
    private final GetPaymentByIdHandler getPaymentByIdHandler;
    
    public CustomerPortalInvoiceController(
            GetInvoiceByIdHandler getInvoiceByIdHandler,
            ListInvoicesByCustomerHandler listInvoicesByCustomerHandler,
            RecordPaymentHandler recordPaymentHandler,
            GetPaymentByIdHandler getPaymentByIdHandler) {
        this.getInvoiceByIdHandler = getInvoiceByIdHandler;
        this.listInvoicesByCustomerHandler = listInvoicesByCustomerHandler;
        this.recordPaymentHandler = recordPaymentHandler;
        this.getPaymentByIdHandler = getPaymentByIdHandler;
    }
    
    /**
     * Get invoice by ID - only if it belongs to the specified customer.
     * This endpoint requires customerId as a query parameter to verify ownership.
     */
    @GetMapping("/{id}")
    @Operation(summary = "Get invoice by ID for customer", description = "Retrieves an invoice only if it belongs to the specified customer")
    @ApiResponse(responseCode = "200", description = "Invoice found")
    @ApiResponse(responseCode = "403", description = "Invoice does not belong to customer")
    @ApiResponse(responseCode = "404", description = "Invoice not found")
    public ResponseEntity<InvoiceResponse> getInvoiceById(
            @Parameter(description = "Invoice ID") @PathVariable UUID id,
            @Parameter(description = "Customer ID to verify ownership") @RequestParam UUID customerId) {
        
        GetInvoiceByIdQuery query = new GetInvoiceByIdQuery(id);
        var invoiceDto = getInvoiceByIdHandler.handle(query);
        
        // Verify the invoice belongs to the customer
        if (!invoiceDto.customerId().equals(customerId)) {
            throw new DomainValidationException(
                "Invoice does not belong to the specified customer"
            );
        }
        
        InvoiceResponse response = toResponse(invoiceDto);
        return ResponseEntity.ok(response);
    }
    
    /**
     * List invoices for a specific customer.
     * This endpoint only returns invoices for the specified customer.
     */
    @GetMapping
    @Operation(summary = "List invoices for customer", description = "Retrieves invoices for the specified customer")
    @ApiResponse(responseCode = "200", description = "Invoices retrieved successfully")
    public ResponseEntity<PagedResponse<InvoiceSummaryResponse>> listInvoices(
            @Parameter(description = "Customer ID") @RequestParam UUID customerId,
            @Parameter(description = "Page number (0-indexed)") @RequestParam(defaultValue = "0") int page,
            @Parameter(description = "Page size") @RequestParam(defaultValue = "20") int size) {
        
        ListInvoicesByCustomerQuery query = new ListInvoicesByCustomerQuery(customerId, page, size);
        PagedResult<com.invoiceme.application.invoices.listByStatus.InvoiceSummaryDto> result = 
            listInvoicesByCustomerHandler.handle(query);
        
        PagedResponse<InvoiceSummaryResponse> response = PagedResponse.of(
            result.content().stream()
                    .map(this::toSummaryResponse)
                    .toList(),
            result.page(),
            result.size(),
            result.totalElements()
        );
        return ResponseEntity.ok(response);
    }
    
    /**
     * Record a payment for an invoice - only if it belongs to the specified customer.
     * This endpoint requires customerId as a query parameter to verify ownership.
     */
    @PostMapping("/{id}/payments")
    @Operation(summary = "Record payment for customer invoice", description = "Records a payment for an invoice only if it belongs to the specified customer")
    @ApiResponse(responseCode = "201", description = "Payment recorded successfully")
    @ApiResponse(responseCode = "403", description = "Invoice does not belong to customer")
    @ApiResponse(responseCode = "404", description = "Invoice not found")
    public ResponseEntity<PaymentDetailResponse> recordPayment(
            @Parameter(description = "Invoice ID") @PathVariable UUID id,
            @Parameter(description = "Customer ID to verify ownership") @RequestParam UUID customerId,
            @Valid @RequestBody RecordPaymentRequest request) {
        
        // Verify the invoice belongs to the customer
        GetInvoiceByIdQuery invoiceQuery = new GetInvoiceByIdQuery(id);
        var invoiceDto = getInvoiceByIdHandler.handle(invoiceQuery);
        
        if (!invoiceDto.customerId().equals(customerId)) {
            throw new DomainValidationException(
                "Invoice does not belong to the specified customer"
            );
        }
        
        // Verify the invoice ID in the request matches the path parameter
        if (!request.invoiceId().equals(id)) {
            throw new DomainValidationException(
                "Invoice ID in request body must match the invoice ID in the URL"
            );
        }
        
        // Record the payment
        RecordPaymentCommand command = new RecordPaymentCommand(
            request.invoiceId(),
            request.amount(),
            request.paymentDate(),
            request.paymentMethod()
        );
        UUID paymentId = recordPaymentHandler.handle(command);
        
        // Fetch and return the payment
        GetPaymentByIdQuery paymentQuery = new GetPaymentByIdQuery(paymentId);
        var paymentDto = getPaymentByIdHandler.handle(paymentQuery);
        
        PaymentDetailResponse response = new PaymentDetailResponse(
            paymentDto.id(),
            paymentDto.invoiceId(),
            paymentDto.invoiceNumber(),
            paymentDto.amount(),
            paymentDto.paymentDate(),
            paymentDto.paymentMethod(),
            paymentDto.createdAt()
        );
        
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }
    
    private InvoiceResponse toResponse(com.invoiceme.application.invoices.getById.InvoiceDto dto) {
        return new InvoiceResponse(
            dto.id(),
            dto.customerId(),
            dto.customerName(),
            dto.status(),
            dto.issueDate(),
            dto.dueDate(),
            dto.paymentPlan(),
            dto.discountCode(),
            dto.discountAmount(),
            dto.subtotal(),
            dto.totalAmount(),
            dto.balance(),
            dto.lineItems().stream()
                    .map(item -> new LineItemResponse(
                        item.id(),
                        item.description(),
                        item.quantity(),
                        item.unitPrice(),
                        item.total()
                    ))
                    .toList(),
            dto.payments().stream()
                    .map(payment -> new PaymentResponse(
                        payment.id(),
                        payment.amount(),
                        payment.paymentDate(),
                        payment.paymentMethod(),
                        payment.createdAt()
                    ))
                    .toList(),
            dto.createdAt(),
            dto.updatedAt()
        );
    }
    
    private InvoiceSummaryResponse toSummaryResponse(com.invoiceme.application.invoices.listByStatus.InvoiceSummaryDto dto) {
        return new InvoiceSummaryResponse(
            dto.id(),
            dto.customerId(),
            dto.customerName(),
            dto.status(),
            dto.issueDate(),
            dto.dueDate(),
            dto.totalAmount(),
            dto.balance()
        );
    }
}

