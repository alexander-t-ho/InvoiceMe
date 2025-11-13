import { apiClient } from "./client";
import type { PaymentScheduleResponse } from "@/types/api";

export const paymentSchedulesApi = {
  /**
   * Get payment schedule for an invoice.
   */
  getByInvoice: async (invoiceId: string): Promise<PaymentScheduleResponse[]> => {
    const response = await apiClient.get<PaymentScheduleResponse[]>(
      `/payment-schedules/invoices/${invoiceId}`
    );
    return response.data;
  },

  /**
   * List upcoming installments.
   */
  getUpcoming: async (upToDate: string): Promise<PaymentScheduleResponse[]> => {
    const response = await apiClient.get<PaymentScheduleResponse[]>(
      "/payment-schedules/upcoming",
      {
        params: { upToDate },
      }
    );
    return response.data;
  },
};









