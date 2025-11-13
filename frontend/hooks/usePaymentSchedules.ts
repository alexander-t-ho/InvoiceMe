import { useQuery } from "@tanstack/react-query";
import { paymentSchedulesApi } from "@/lib/api/payment-schedules";

/**
 * React Query hook for fetching payment schedule for an invoice.
 */
export function usePaymentSchedule(invoiceId: string | undefined) {
  return useQuery({
    queryKey: ["payment-schedules", "invoice", invoiceId],
    queryFn: () => paymentSchedulesApi.getByInvoice(invoiceId!),
    enabled: !!invoiceId,
  });
}

/**
 * React Query hook for fetching upcoming installments.
 */
export function useUpcomingInstallments(upToDate: string | undefined) {
  return useQuery({
    queryKey: ["payment-schedules", "upcoming", upToDate],
    queryFn: () => paymentSchedulesApi.getUpcoming(upToDate!),
    enabled: !!upToDate,
  });
}









