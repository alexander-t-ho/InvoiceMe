package com.invoiceme.api.invoices;

import com.invoiceme.application.invoices.addLineItem.AddLineItemCommand;
import com.invoiceme.application.invoices.addLineItem.AddLineItemHandler;
import com.invoiceme.application.invoices.create.CreateInvoiceCommand;
import com.invoiceme.application.invoices.create.CreateInvoiceHandler;
import com.invoiceme.application.invoices.getById.GetInvoiceByIdQuery;
import com.invoiceme.application.invoices.getById.GetInvoiceByIdHandler;
import com.invoiceme.application.invoices.listByCustomer.ListInvoicesByCustomerQuery;
import com.invoiceme.application.invoices.listByCustomer.ListInvoicesByCustomerHandler;
import com.invoiceme.application.invoices.listByStatus.ListInvoicesByStatusQuery;
import com.invoiceme.application.invoices.listByStatus.ListInvoicesByStatusHandler;
import com.invoiceme.application.invoices.listAll.ListAllInvoicesQuery;
import com.invoiceme.application.invoices.listAll.ListAllInvoicesHandler;
import com.invoiceme.application.invoices.markAsSent.MarkInvoiceAsSentCommand;
import com.invoiceme.application.invoices.markAsSent.MarkInvoiceAsSentHandler;
import com.invoiceme.application.invoices.removeLineItem.RemoveLineItemCommand;
import com.invoiceme.application.invoices.removeLineItem.RemoveLineItemHandler;
import com.invoiceme.application.invoices.update.UpdateInvoiceCommand;
import com.invoiceme.application.invoices.update.UpdateInvoiceHandler;
import com.invoiceme.application.discounts.apply.ApplyDiscountCodeCommand;
import com.invoiceme.application.discounts.apply.ApplyDiscountCodeHandler;
import com.invoiceme.application.discounts.remove.RemoveDiscountCodeCommand;
import com.invoiceme.application.discounts.remove.RemoveDiscountCodeHandler;
import com.invoiceme.application.customers.listAll.PagedResult;
import com.invoiceme.api.common.PagedResponse;
import com.invoiceme.domain.invoices.InvoiceStatus;
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
 * REST controller for Invoice operations.
 */
@RestController
@RequestMapping("/api/v1/invoices")
@Tag(name = "Invoices", description = "Invoice management API")
public class InvoiceController {
    
    private final CreateInvoiceHandler createInvoiceHandler;
    private final UpdateInvoiceHandler updateInvoiceHandler;
    private final MarkInvoiceAsSentHandler markInvoiceAsSentHandler;
    private final AddLineItemHandler addLineItemHandler;
    private final RemoveLineItemHandler removeLineItemHandler;
    private final GetInvoiceByIdHandler getInvoiceByIdHandler;
    private final ListInvoicesByStatusHandler listInvoicesByStatusHandler;
    private final ListInvoicesByCustomerHandler listInvoicesByCustomerHandler;
    private final ListAllInvoicesHandler listAllInvoicesHandler;
    private final ApplyDiscountCodeHandler applyDiscountCodeHandler;
    private final RemoveDiscountCodeHandler removeDiscountCodeHandler;
    
    public InvoiceController(
            CreateInvoiceHandler createInvoiceHandler,
            UpdateInvoiceHandler updateInvoiceHandler,
            MarkInvoiceAsSentHandler markInvoiceAsSentHandler,
            AddLineItemHandler addLineItemHandler,
            RemoveLineItemHandler removeLineItemHandler,
            GetInvoiceByIdHandler getInvoiceByIdHandler,
            ListInvoicesByStatusHandler listInvoicesByStatusHandler,
            ListInvoicesByCustomerHandler listInvoicesByCustomerHandler,
            ListAllInvoicesHandler listAllInvoicesHandler,
            ApplyDiscountCodeHandler applyDiscountCodeHandler,
            RemoveDiscountCodeHandler removeDiscountCodeHandler) {
        this.createInvoiceHandler = createInvoiceHandler;
        this.updateInvoiceHandler = updateInvoiceHandler;
        this.markInvoiceAsSentHandler = markInvoiceAsSentHandler;
        this.addLineItemHandler = addLineItemHandler;
        this.removeLineItemHandler = removeLineItemHandler;
        this.getInvoiceByIdHandler = getInvoiceByIdHandler;
        this.listInvoicesByStatusHandler = listInvoicesByStatusHandler;
        this.listInvoicesByCustomerHandler = listInvoicesByCustomerHandler;
        this.listAllInvoicesHandler = listAllInvoicesHandler;
        this.applyDiscountCodeHandler = applyDiscountCodeHandler;
        this.removeDiscountCodeHandler = removeDiscountCodeHandler;
    }
    
    @PostMapping
    @Operation(summary = "Create a new invoice", description = "Creates a new invoice in DRAFT status")
    @ApiResponse(responseCode = "201", description = "Invoice created successfully")
    @ApiResponse(responseCode = "400", description = "Invalid input")
    public ResponseEntity<InvoiceResponse> createInvoice(@Valid @RequestBody CreateInvoiceRequest request) {
        CreateInvoiceCommand command = new CreateInvoiceCommand(
            request.customerId(),
            request.issueDate(),
            request.dueDate(),
            request.paymentPlan() != null ? request.paymentPlan() : com.invoiceme.domain.payments.PaymentPlan.FULL
        );
        UUID invoiceId = createInvoiceHandler.handle(command);
        
        GetInvoiceByIdQuery query = new GetInvoiceByIdQuery(invoiceId);
        var invoiceDto = getInvoiceByIdHandler.handle(query);
        
        InvoiceResponse response = toResponse(invoiceDto);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }
    
    @PutMapping("/{id}")
    @Operation(summary = "Update an invoice", description = "Updates invoice dates (only for DRAFT invoices)")
    @ApiResponse(responseCode = "200", description = "Invoice updated successfully")
    @ApiResponse(responseCode = "400", description = "Invalid input or invoice not in DRAFT status")
    @ApiResponse(responseCode = "404", description = "Invoice not found")
    public ResponseEntity<InvoiceResponse> updateInvoice(
            @Parameter(description = "Invoice ID") @PathVariable UUID id,
            @Valid @RequestBody UpdateInvoiceRequest request) {
        UpdateInvoiceCommand command = new UpdateInvoiceCommand(
            id,
            request.issueDate(),
            request.dueDate()
        );
        updateInvoiceHandler.handle(command);
        
        GetInvoiceByIdQuery query = new GetInvoiceByIdQuery(id);
        var invoiceDto = getInvoiceByIdHandler.handle(query);
        
        InvoiceResponse response = toResponse(invoiceDto);
        return ResponseEntity.ok(response);
    }
    
    @PostMapping("/{id}/send")
    @Operation(summary = "Mark invoice as sent", description = "Transitions invoice from DRAFT to SENT status")
    @ApiResponse(responseCode = "200", description = "Invoice marked as sent successfully")
    @ApiResponse(responseCode = "400", description = "Invoice cannot be sent (must have line items and be in DRAFT status)")
    @ApiResponse(responseCode = "404", description = "Invoice not found")
    public ResponseEntity<InvoiceResponse> markInvoiceAsSent(
            @Parameter(description = "Invoice ID") @PathVariable UUID id) {
        MarkInvoiceAsSentCommand command = new MarkInvoiceAsSentCommand(id);
        markInvoiceAsSentHandler.handle(command);
        
        GetInvoiceByIdQuery query = new GetInvoiceByIdQuery(id);
        var invoiceDto = getInvoiceByIdHandler.handle(query);
        
        InvoiceResponse response = toResponse(invoiceDto);
        return ResponseEntity.ok(response);
    }
    
    @PostMapping("/{id}/line-items")
    @Operation(summary = "Add line item to invoice", description = "Adds a line item to an invoice (only for DRAFT invoices). If itemId is provided, description and unitPrice will be auto-filled from the item library.")
    @ApiResponse(responseCode = "200", description = "Line item added successfully")
    @ApiResponse(responseCode = "400", description = "Invalid input or invoice not in DRAFT status")
    @ApiResponse(responseCode = "404", description = "Invoice not found")
    public ResponseEntity<InvoiceResponse> addLineItem(
            @Parameter(description = "Invoice ID") @PathVariable UUID id,
            @Valid @RequestBody AddLineItemRequest request) {
        AddLineItemCommand command = new AddLineItemCommand(
            id,
            request.itemId(),
            request.description(),
            request.quantity(),
            request.unitPrice()
        );
        addLineItemHandler.handle(command);
        
        GetInvoiceByIdQuery query = new GetInvoiceByIdQuery(id);
        var invoiceDto = getInvoiceByIdHandler.handle(query);
        
        InvoiceResponse response = toResponse(invoiceDto);
        return ResponseEntity.ok(response);
    }
    
    @DeleteMapping("/{id}/line-items/{lineItemId}")
    @Operation(summary = "Remove line item from invoice", description = "Removes a line item from an invoice (only for DRAFT invoices)")
    @ApiResponse(responseCode = "200", description = "Line item removed successfully")
    @ApiResponse(responseCode = "400", description = "Invoice not in DRAFT status")
    @ApiResponse(responseCode = "404", description = "Invoice or line item not found")
    public ResponseEntity<InvoiceResponse> removeLineItem(
            @Parameter(description = "Invoice ID") @PathVariable UUID id,
            @Parameter(description = "Line Item ID") @PathVariable UUID lineItemId) {
        RemoveLineItemCommand command = new RemoveLineItemCommand(id, lineItemId);
        removeLineItemHandler.handle(command);
        
        GetInvoiceByIdQuery query = new GetInvoiceByIdQuery(id);
        var invoiceDto = getInvoiceByIdHandler.handle(query);
        
        InvoiceResponse response = toResponse(invoiceDto);
        return ResponseEntity.ok(response);
    }
    
    @PostMapping("/{id}/apply-discount")
    @Operation(summary = "Apply discount code to invoice", description = "Applies a discount code to an invoice (only for DRAFT invoices)")
    @ApiResponse(responseCode = "200", description = "Discount applied successfully")
    @ApiResponse(responseCode = "400", description = "Invalid discount code or invoice not in DRAFT status")
    @ApiResponse(responseCode = "404", description = "Invoice not found")
    public ResponseEntity<InvoiceResponse> applyDiscount(
            @Parameter(description = "Invoice ID") @PathVariable UUID id,
            @Valid @RequestBody ApplyDiscountRequest request) {
        ApplyDiscountCodeCommand command = new ApplyDiscountCodeCommand(id, request.discountCode());
        applyDiscountCodeHandler.handle(command);
        
        GetInvoiceByIdQuery query = new GetInvoiceByIdQuery(id);
        var invoiceDto = getInvoiceByIdHandler.handle(query);
        
        InvoiceResponse response = toResponse(invoiceDto);
        return ResponseEntity.ok(response);
    }
    
    @DeleteMapping("/{id}/discount")
    @Operation(summary = "Remove discount from invoice", description = "Removes the discount code from an invoice (only for DRAFT invoices)")
    @ApiResponse(responseCode = "200", description = "Discount removed successfully")
    @ApiResponse(responseCode = "400", description = "Invoice not in DRAFT status")
    @ApiResponse(responseCode = "404", description = "Invoice not found")
    public ResponseEntity<InvoiceResponse> removeDiscount(
            @Parameter(description = "Invoice ID") @PathVariable UUID id) {
        RemoveDiscountCodeCommand command = new RemoveDiscountCodeCommand(id);
        removeDiscountCodeHandler.handle(command);
        
        GetInvoiceByIdQuery query = new GetInvoiceByIdQuery(id);
        var invoiceDto = getInvoiceByIdHandler.handle(query);
        
        InvoiceResponse response = toResponse(invoiceDto);
        return ResponseEntity.ok(response);
    }
    
    @GetMapping("/{id}")
    @Operation(summary = "Get invoice by ID", description = "Retrieves an invoice with all line items and payments")
    @ApiResponse(responseCode = "200", description = "Invoice found")
    @ApiResponse(responseCode = "404", description = "Invoice not found")
    public ResponseEntity<InvoiceResponse> getInvoiceById(
            @Parameter(description = "Invoice ID") @PathVariable UUID id) {
        GetInvoiceByIdQuery query = new GetInvoiceByIdQuery(id);
        var invoiceDto = getInvoiceByIdHandler.handle(query);
        
        InvoiceResponse response = toResponse(invoiceDto);
        return ResponseEntity.ok(response);
    }
    
    @GetMapping
    @Operation(summary = "List invoices", description = "Retrieves invoices filtered by status or customer")
    @ApiResponse(responseCode = "200", description = "Invoices retrieved successfully")
    public ResponseEntity<PagedResponse<InvoiceSummaryResponse>> listInvoices(
            @Parameter(description = "Filter by status") @RequestParam(required = false) InvoiceStatus status,
            @Parameter(description = "Filter by customer ID") @RequestParam(required = false) UUID customerId,
            @Parameter(description = "Page number (0-indexed)") @RequestParam(defaultValue = "0") int page,
            @Parameter(description = "Page size") @RequestParam(defaultValue = "20") int size) {
        
        PagedResult<com.invoiceme.application.invoices.listByStatus.InvoiceSummaryDto> result;
        
        if (status != null) {
            ListInvoicesByStatusQuery query = new ListInvoicesByStatusQuery(status, page, size);
            result = listInvoicesByStatusHandler.handle(query);
        } else if (customerId != null) {
            ListInvoicesByCustomerQuery query = new ListInvoicesByCustomerQuery(customerId, page, size);
            result = listInvoicesByCustomerHandler.handle(query);
        } else {
            // List all invoices regardless of status
            ListAllInvoicesQuery query = new ListAllInvoicesQuery(page, size);
            result = listAllInvoicesHandler.handle(query);
        }
        
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
    
    public record ApplyDiscountRequest(
        String discountCode
    ) {
    }
}

