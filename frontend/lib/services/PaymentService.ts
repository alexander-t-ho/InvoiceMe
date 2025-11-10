import { paymentsApi } from "@/lib/api/payments";
import type {
  PaymentDetailResponse,
  RecordPaymentRequest,
} from "@/types/api";

/**
 * Payment Service (ViewModel layer in MVVM pattern).
 * Provides business logic and data transformation for payment operations.
 */
export class PaymentService {
  /**
   * Get payment by ID.
   */
  async getPaymentById(id: string): Promise<PaymentDetailResponse> {
    return paymentsApi.getById(id);
  }

  /**
   * Record a new payment for an invoice (admin use - requires authentication).
   */
  async recordPayment(
    data: RecordPaymentRequest
  ): Promise<PaymentDetailResponse> {
    return paymentsApi.record(data);
  }

  /**
   * Record a payment for a customer invoice (portal use).
   * This endpoint verifies the invoice belongs to the customer.
   */
  async recordCustomerPayment(
    invoiceId: string,
    customerId: string,
    data: RecordPaymentRequest
  ): Promise<PaymentDetailResponse> {
    return paymentsApi.recordCustomerPayment(invoiceId, customerId, data);
  }

  /**
   * Get all payments for an invoice.
   */
  async getPaymentsByInvoice(
    invoiceId: string
  ): Promise<PaymentDetailResponse[]> {
    return paymentsApi.getByInvoice(invoiceId);
  }
}

// Export singleton instance
export const paymentService = new PaymentService();

