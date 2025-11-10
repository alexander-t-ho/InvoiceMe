import { useMutation, useQueryClient } from "@tanstack/react-query";
import { paymentService } from "@/lib/services/PaymentService";
import { useToast } from "@/hooks/use-toast";
import type { RecordPaymentRequest } from "@/types/api";

/**
 * React Query hook for recording a payment in the customer portal.
 * This hook uses the customer portal endpoint which verifies invoice ownership.
 */
export function useRecordPortalPayment(invoiceId: string, customerId: string) {
  const queryClient = useQueryClient();
  const { toast } = useToast();

  return useMutation({
    mutationFn: (data: RecordPaymentRequest) =>
      paymentService.recordCustomerPayment(invoiceId, customerId, data),
    onSuccess: () => {
      queryClient.invalidateQueries({ queryKey: ["payments"] });
      queryClient.invalidateQueries({ queryKey: ["payments", "invoice", invoiceId] });
      queryClient.invalidateQueries({ queryKey: ["invoices", invoiceId] });
      queryClient.invalidateQueries({ queryKey: ["invoices"] });
      toast({
        title: "Success",
        description: "Payment recorded successfully",
      });
    },
    onError: (error: any) => {
      toast({
        title: "Error",
        description: error.response?.data?.message || error.message || "Failed to record payment",
        variant: "destructive",
      });
    },
  });
}

