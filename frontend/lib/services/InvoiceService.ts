import { invoicesApi } from "@/lib/api/invoices";
import type {
  InvoiceResponse,
  InvoiceSummaryResponse,
  CreateInvoiceRequest,
  UpdateInvoiceRequest,
  AddLineItemRequest,
  PagedResponse,
  InvoiceStatus,
} from "@/types/api";

/**
 * Invoice Service (ViewModel layer in MVVM pattern).
 * Provides business logic and data transformation for invoice operations.
 */
export class InvoiceService {
  /**
   * Get all invoices with optional filters.
   */
  async getAllInvoices(filters?: {
    status?: InvoiceStatus;
    customerId?: string;
    page?: number;
    size?: number;
  }): Promise<PagedResponse<InvoiceSummaryResponse>> {
    return invoicesApi.getAll(filters);
  }

  /**
   * Get invoice by ID.
   */
  async getInvoiceById(id: string): Promise<InvoiceResponse> {
    return invoicesApi.getById(id);
  }

  /**
   * Create a new invoice (in DRAFT status).
   */
  async createInvoice(data: CreateInvoiceRequest): Promise<InvoiceResponse> {
    return invoicesApi.create(data);
  }

  /**
   * Update invoice dates (only for DRAFT invoices).
   */
  async updateInvoice(
    id: string,
    data: UpdateInvoiceRequest
  ): Promise<InvoiceResponse> {
    return invoicesApi.update(id, data);
  }

  /**
   * Mark invoice as sent (transitions from DRAFT to SENT).
   */
  async markInvoiceAsSent(id: string): Promise<InvoiceResponse> {
    return invoicesApi.markAsSent(id);
  }

  /**
   * Add a line item to an invoice (only for DRAFT invoices).
   */
  async addLineItem(
    invoiceId: string,
    lineItem: AddLineItemRequest
  ): Promise<InvoiceResponse> {
    return invoicesApi.addLineItem(invoiceId, lineItem);
  }

  /**
   * Remove a line item from an invoice (only for DRAFT invoices).
   */
  async removeLineItem(
    invoiceId: string,
    lineItemId: string
  ): Promise<InvoiceResponse> {
    return invoicesApi.removeLineItem(invoiceId, lineItemId);
  }

  /**
   * Get invoices by status.
   */
  async getInvoicesByStatus(
    status: InvoiceStatus,
    page: number = 0,
    size: number = 20
  ): Promise<PagedResponse<InvoiceSummaryResponse>> {
    return invoicesApi.getAll({ status, page, size });
  }

  /**
   * Get invoices by customer (admin use).
   */
  async getInvoicesByCustomer(
    customerId: string,
    page: number = 0,
    size: number = 20
  ): Promise<PagedResponse<InvoiceSummaryResponse>> {
    return invoicesApi.getAll({ customerId, page, size });
  }

  /**
   * Get invoices for a specific customer (portal use).
   * This endpoint verifies the customer can only see their own invoices.
   */
  async getCustomerInvoices(
    customerId: string,
    page: number = 0,
    size: number = 20
  ): Promise<PagedResponse<InvoiceSummaryResponse>> {
    return invoicesApi.getCustomerInvoices(customerId, page, size);
  }

  /**
   * Get invoice by ID for a specific customer (portal use).
   * This endpoint verifies the invoice belongs to the customer.
   */
  async getCustomerInvoiceById(
    invoiceId: string,
    customerId: string
  ): Promise<InvoiceResponse> {
    return invoicesApi.getCustomerInvoiceById(invoiceId, customerId);
  }
}

// Export singleton instance
export const invoiceService = new InvoiceService();

