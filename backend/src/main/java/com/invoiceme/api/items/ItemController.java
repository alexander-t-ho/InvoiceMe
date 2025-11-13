package com.invoiceme.api.items;

import com.invoiceme.api.common.PagedResponse;
import com.invoiceme.application.items.create.CreateItemCommand;
import com.invoiceme.application.items.create.CreateItemHandler;
import com.invoiceme.application.items.delete.DeleteItemCommand;
import com.invoiceme.application.items.delete.DeleteItemHandler;
import com.invoiceme.application.items.getById.GetItemByIdQuery;
import com.invoiceme.application.items.getById.GetItemByIdHandler;
import com.invoiceme.application.items.list.ListItemsQuery;
import com.invoiceme.application.items.list.ListItemsHandler;
import com.invoiceme.application.items.update.UpdateItemCommand;
import com.invoiceme.application.items.update.UpdateItemHandler;
import com.invoiceme.infrastructure.security.SecurityUtils;
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
 * REST controller for Item operations.
 */
@RestController
@RequestMapping("/api/v1/items")
@Tag(name = "Items", description = "Item library management API")
public class ItemController {
    
    private final CreateItemHandler createItemHandler;
    private final UpdateItemHandler updateItemHandler;
    private final DeleteItemHandler deleteItemHandler;
    private final GetItemByIdHandler getItemByIdHandler;
    private final ListItemsHandler listItemsHandler;
    private final SecurityUtils securityUtils;
    
    public ItemController(
            CreateItemHandler createItemHandler,
            UpdateItemHandler updateItemHandler,
            DeleteItemHandler deleteItemHandler,
            GetItemByIdHandler getItemByIdHandler,
            ListItemsHandler listItemsHandler,
            SecurityUtils securityUtils) {
        this.createItemHandler = createItemHandler;
        this.updateItemHandler = updateItemHandler;
        this.deleteItemHandler = deleteItemHandler;
        this.getItemByIdHandler = getItemByIdHandler;
        this.listItemsHandler = listItemsHandler;
        this.securityUtils = securityUtils;
    }
    
    @PostMapping
    @Operation(summary = "Create a new item", description = "Creates a new item in the item library")
    @ApiResponse(responseCode = "201", description = "Item created successfully")
    @ApiResponse(responseCode = "400", description = "Invalid input")
    public ResponseEntity<ItemResponse> createItem(@Valid @RequestBody CreateItemRequest request) {
        UUID userId = securityUtils.getCurrentUserId();
        
        CreateItemCommand command = new CreateItemCommand(
            userId,
            request.description(),
            request.unitPrice()
        );
        UUID itemId = createItemHandler.handle(command);
        
        // Fetch the created item to return full response
        GetItemByIdQuery query = new GetItemByIdQuery(itemId, userId);
        var itemDto = getItemByIdHandler.handle(query);
        
        ItemResponse response = toResponse(itemDto);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }
    
    @PutMapping("/{id}")
    @Operation(summary = "Update an item", description = "Updates an existing item")
    @ApiResponse(responseCode = "200", description = "Item updated successfully")
    @ApiResponse(responseCode = "400", description = "Invalid input")
    @ApiResponse(responseCode = "404", description = "Item not found")
    public ResponseEntity<ItemResponse> updateItem(
            @Parameter(description = "Item ID") @PathVariable UUID id,
            @Valid @RequestBody UpdateItemRequest request) {
        UUID userId = securityUtils.getCurrentUserId();
        
        UpdateItemCommand command = new UpdateItemCommand(
            id,
            userId,
            request.description(),
            request.unitPrice()
        );
        updateItemHandler.handle(command);
        
        // Fetch the updated item
        GetItemByIdQuery query = new GetItemByIdQuery(id, userId);
        var itemDto = getItemByIdHandler.handle(query);
        
        ItemResponse response = toResponse(itemDto);
        return ResponseEntity.ok(response);
    }
    
    @DeleteMapping("/{id}")
    @Operation(summary = "Delete an item", description = "Deletes an item by ID")
    @ApiResponse(responseCode = "204", description = "Item deleted successfully")
    @ApiResponse(responseCode = "404", description = "Item not found")
    public ResponseEntity<Void> deleteItem(
            @Parameter(description = "Item ID") @PathVariable UUID id) {
        UUID userId = securityUtils.getCurrentUserId();
        
        DeleteItemCommand command = new DeleteItemCommand(id, userId);
        deleteItemHandler.handle(command);
        return ResponseEntity.noContent().build();
    }
    
    @GetMapping("/{id}")
    @Operation(summary = "Get item by ID", description = "Retrieves an item by its ID")
    @ApiResponse(responseCode = "200", description = "Item found")
    @ApiResponse(responseCode = "404", description = "Item not found")
    public ResponseEntity<ItemResponse> getItemById(
            @Parameter(description = "Item ID") @PathVariable UUID id) {
        UUID userId = securityUtils.getCurrentUserId();
        
        GetItemByIdQuery query = new GetItemByIdQuery(id, userId);
        var itemDto = getItemByIdHandler.handle(query);
        
        ItemResponse response = toResponse(itemDto);
        return ResponseEntity.ok(response);
    }
    
    @GetMapping
    @Operation(summary = "List all items", description = "Retrieves a paginated list of all items for the current user")
    @ApiResponse(responseCode = "200", description = "Items retrieved successfully")
    public ResponseEntity<PagedResponse<ItemResponse>> listItems(
            @Parameter(description = "Page number (0-indexed)") @RequestParam(defaultValue = "0") int page,
            @Parameter(description = "Page size") @RequestParam(defaultValue = "20") int size) {
        UUID userId = securityUtils.getCurrentUserId();
        
        ListItemsQuery query = new ListItemsQuery(userId, page, size);
        var result = listItemsHandler.handle(query);
        
        PagedResponse<ItemResponse> response = PagedResponse.of(
            result.content().stream()
                    .map(this::toResponse)
                    .toList(),
            result.page(),
            result.size(),
            result.totalElements()
        );
        return ResponseEntity.ok(response);
    }
    
    private ItemResponse toResponse(GetItemByIdHandler.ItemDto dto) {
        return new ItemResponse(
            dto.id(),
            dto.userId(),
            dto.description(),
            dto.unitPrice(),
            dto.createdAt(),
            dto.updatedAt()
        );
    }
}









