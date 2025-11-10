import { apiClient } from "./client";
import type {
  InvoiceResponse,
  InvoiceSummaryResponse,
  CreateInvoiceRequest,
  UpdateInvoiceRequest,
  AddLineItemRequest,
  PagedResponse,
  InvoiceStatus,
} from "@/types/api";

export const invoicesApi = {
  /**
   * Get all invoices with optional filters.
   * For admin use - requires authentication.
   */
  getAll: async (
    filters?: {
      status?: InvoiceStatus;
      customerId?: string;
      page?: number;
      size?: number;
    }
  ): Promise<PagedResponse<InvoiceSummaryResponse>> => {
    const params: Record<string, string | number> = {
      page: filters?.page ?? 0,
      size: filters?.size ?? 20,
    };
    
    if (filters?.status) {
      params.status = filters.status;
    }
    if (filters?.customerId) {
      params.customerId = filters.customerId;
    }
    
    const response = await apiClient.get<PagedResponse<InvoiceSummaryResponse>>(
      "/invoices",
      { params }
    );
    return response.data;
  },

  /**
   * Get invoices for a specific customer (portal use).
   * This endpoint verifies the customer can only see their own invoices.
   */
  getCustomerInvoices: async (
    customerId: string,
    page: number = 0,
    size: number = 20
  ): Promise<PagedResponse<InvoiceSummaryResponse>> => {
    const response = await apiClient.get<PagedResponse<InvoiceSummaryResponse>>(
      "/customers/portal/invoices",
      { params: { customerId, page, size } }
    );
    return response.data;
  },

  /**
   * Get invoice by ID for a specific customer (portal use).
   * This endpoint verifies the invoice belongs to the customer.
   */
  getCustomerInvoiceById: async (
    invoiceId: string,
    customerId: string
  ): Promise<InvoiceResponse> => {
    const response = await apiClient.get<InvoiceResponse>(
      `/customers/portal/invoices/${invoiceId}`,
      { params: { customerId } }
    );
    return response.data;
  },

  /**
   * Get invoice by ID.
   */
  getById: async (id: string): Promise<InvoiceResponse> => {
    const response = await apiClient.get<InvoiceResponse>(`/invoices/${id}`);
    return response.data;
  },

  /**
   * Create a new invoice.
   */
  create: async (data: CreateInvoiceRequest): Promise<InvoiceResponse> => {
    const response = await apiClient.post<InvoiceResponse>("/invoices", data);
    return response.data;
  },

  /**
   * Update an invoice (dates only, for DRAFT invoices).
   */
  update: async (
    id: string,
    data: UpdateInvoiceRequest
  ): Promise<InvoiceResponse> => {
    const response = await apiClient.put<InvoiceResponse>(
      `/invoices/${id}`,
      data
    );
    return response.data;
  },

  /**
   * Mark invoice as sent.
   */
  markAsSent: async (id: string): Promise<InvoiceResponse> => {
    const response = await apiClient.post<InvoiceResponse>(
      `/invoices/${id}/send`
    );
    return response.data;
  },

  /**
   * Add a line item to an invoice.
   */
  addLineItem: async (
    invoiceId: string,
    lineItem: AddLineItemRequest
  ): Promise<InvoiceResponse> => {
    const response = await apiClient.post<InvoiceResponse>(
      `/invoices/${invoiceId}/line-items`,
      lineItem
    );
    return response.data;
  },

  /**
   * Remove a line item from an invoice.
   */
  removeLineItem: async (
    invoiceId: string,
    lineItemId: string
  ): Promise<InvoiceResponse> => {
    const response = await apiClient.delete<InvoiceResponse>(
      `/invoices/${invoiceId}/line-items/${lineItemId}`
    );
    return response.data;
  },

  /**
   * Apply a discount code to an invoice.
   */
  applyDiscount: async (
    invoiceId: string,
    discountCode: string
  ): Promise<InvoiceResponse> => {
    const response = await apiClient.post<InvoiceResponse>(
      `/invoices/${invoiceId}/apply-discount`,
      { discountCode }
    );
    return response.data;
  },

  /**
   * Remove discount from an invoice.
   */
  removeDiscount: async (invoiceId: string): Promise<InvoiceResponse> => {
    const response = await apiClient.delete<InvoiceResponse>(
      `/invoices/${invoiceId}/discount`
    );
    return response.data;
  },
};

