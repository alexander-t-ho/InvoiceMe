import { useQuery, useMutation, useQueryClient } from "@tanstack/react-query";
import { discountsApi, type DiscountCodeValidationResponse } from "@/lib/api/discounts";
import { useToast } from "@/hooks/use-toast";
import { invoicesApi } from "@/lib/api/invoices";
import type { InvoiceResponse } from "@/types/api";

/**
 * React Query hook for validating a discount code.
 */
export function useValidateDiscountCode(code: string | null) {
  return useQuery({
    queryKey: ["discount-codes", "validate", code],
    queryFn: () => discountsApi.validate(code!),
    enabled: !!code && code.trim().length > 0,
  });
}

/**
 * React Query hook for listing all discount codes.
 */
export function useDiscountCodes() {
  return useQuery({
    queryKey: ["discount-codes"],
    queryFn: () => discountsApi.listAll(),
  });
}

/**
 * React Query hook for applying a discount code to an invoice.
 */
export function useApplyDiscount() {
  const queryClient = useQueryClient();
  const { toast } = useToast();

  return useMutation({
    mutationFn: ({ invoiceId, discountCode }: { invoiceId: string; discountCode: string }) =>
      invoicesApi.applyDiscount(invoiceId, discountCode),
    onSuccess: (_, variables) => {
      queryClient.invalidateQueries({ queryKey: ["invoices", variables.invoiceId] });
      queryClient.invalidateQueries({ queryKey: ["invoices"] });
      toast({
        title: "Success",
        description: "Discount code applied successfully",
      });
    },
    onError: (error: Error) => {
      toast({
        title: "Error",
        description: error.message || "Failed to apply discount code",
        variant: "destructive",
      });
    },
  });
}

/**
 * React Query hook for removing a discount from an invoice.
 */
export function useRemoveDiscount() {
  const queryClient = useQueryClient();
  const { toast } = useToast();

  return useMutation({
    mutationFn: (invoiceId: string) => invoicesApi.removeDiscount(invoiceId),
    onSuccess: (_, invoiceId) => {
      queryClient.invalidateQueries({ queryKey: ["invoices", invoiceId] });
      queryClient.invalidateQueries({ queryKey: ["invoices"] });
      toast({
        title: "Success",
        description: "Discount removed successfully",
      });
    },
    onError: (error: Error) => {
      toast({
        title: "Error",
        description: error.message || "Failed to remove discount",
        variant: "destructive",
      });
    },
  });
}









