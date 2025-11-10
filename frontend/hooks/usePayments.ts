import { useQuery, useMutation, useQueryClient } from "@tanstack/react-query";
import { paymentService } from "@/lib/services/PaymentService";
import { useToast } from "@/hooks/use-toast";
import type {
  PaymentDetailResponse,
  RecordPaymentRequest,
} from "@/types/api";

/**
 * React Query hook for fetching a payment by ID.
 */
export function usePayment(id: string | undefined) {
  return useQuery({
    queryKey: ["payments", id],
    queryFn: () => paymentService.getPaymentById(id!),
    enabled: !!id,
  });
}

/**
 * React Query hook for fetching payments by invoice ID.
 */
export function usePaymentsByInvoice(invoiceId: string | undefined) {
  return useQuery({
    queryKey: ["payments", "invoice", invoiceId],
    queryFn: () => paymentService.getPaymentsByInvoice(invoiceId!),
    enabled: !!invoiceId,
  });
}

/**
 * React Query hook for recording a payment.
 */
export function useRecordPayment() {
  const queryClient = useQueryClient();
  const { toast } = useToast();

  return useMutation({
    mutationFn: (data: RecordPaymentRequest) =>
      paymentService.recordPayment(data),
    onSuccess: (_, variables) => {
      queryClient.invalidateQueries({ queryKey: ["payments"] });
      queryClient.invalidateQueries({ queryKey: ["payments", "invoice", variables.invoiceId] });
      queryClient.invalidateQueries({ queryKey: ["invoices", variables.invoiceId] });
      queryClient.invalidateQueries({ queryKey: ["invoices"] });
      toast({
        title: "Success",
        description: "Payment recorded successfully",
      });
    },
    onError: (error: Error) => {
      toast({
        title: "Error",
        description: error.message || "Failed to record payment",
        variant: "destructive",
      });
    },
  });
}

