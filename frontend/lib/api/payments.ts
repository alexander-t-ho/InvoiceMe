import { apiClient } from "./client";
import type {
  PaymentDetailResponse,
  RecordPaymentRequest,
} from "@/types/api";

export const paymentsApi = {
  /**
   * Get payment by ID.
   */
  getById: async (id: string): Promise<PaymentDetailResponse> => {
    const response = await apiClient.get<PaymentDetailResponse>(
      `/payments/${id}`
    );
    return response.data;
  },

  /**
   * Record a new payment (admin use - requires authentication).
   */
  record: async (data: RecordPaymentRequest): Promise<PaymentDetailResponse> => {
    const response = await apiClient.post<PaymentDetailResponse>(
      "/payments",
      data
    );
    return response.data;
  },

  /**
   * Record a payment for a customer invoice (portal use).
   * This endpoint verifies the invoice belongs to the customer.
   */
  recordCustomerPayment: async (
    invoiceId: string,
    customerId: string,
    data: RecordPaymentRequest
  ): Promise<PaymentDetailResponse> => {
    const response = await apiClient.post<PaymentDetailResponse>(
      `/customers/portal/invoices/${invoiceId}/payments?customerId=${customerId}`,
      data
    );
    return response.data;
  },

  /**
   * Get all payments for an invoice.
   */
  getByInvoice: async (invoiceId: string): Promise<PaymentDetailResponse[]> => {
    const response = await apiClient.get<PaymentDetailResponse[]>(
      `/payments/invoices/${invoiceId}`
    );
    return response.data;
  },
};

