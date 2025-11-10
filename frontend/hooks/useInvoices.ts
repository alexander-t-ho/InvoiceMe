import { useQuery, useMutation, useQueryClient } from "@tanstack/react-query";
import { invoiceService } from "@/lib/services/InvoiceService";
import { useToast } from "@/hooks/use-toast";
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
 * React Query hook for fetching invoices with optional filters.
 */
export function useInvoices(filters?: {
  status?: InvoiceStatus;
  customerId?: string;
  page?: number;
  size?: number;
}) {
  return useQuery({
    queryKey: ["invoices", filters],
    queryFn: () => invoiceService.getAllInvoices(filters),
    placeholderData: (previousData) => previousData, // Show previous data while fetching new data (React Query v5)
  });
}

/**
 * React Query hook for fetching a single invoice by ID.
 */
export function useInvoice(id: string | undefined) {
  return useQuery({
    queryKey: ["invoices", id],
    queryFn: () => invoiceService.getInvoiceById(id!),
    enabled: !!id,
  });
}

/**
 * React Query hook for creating an invoice.
 */
export function useCreateInvoice() {
  const queryClient = useQueryClient();
  const { toast } = useToast();

  return useMutation({
    mutationFn: (data: CreateInvoiceRequest) =>
      invoiceService.createInvoice(data),
    onSuccess: () => {
      queryClient.invalidateQueries({ queryKey: ["invoices"] });
      toast({
        title: "Success",
        description: "Invoice created successfully",
      });
    },
    onError: (error: Error) => {
      toast({
        title: "Error",
        description: error.message || "Failed to create invoice",
        variant: "destructive",
      });
    },
  });
}

/**
 * React Query hook for updating an invoice.
 */
export function useUpdateInvoice() {
  const queryClient = useQueryClient();
  const { toast } = useToast();

  return useMutation({
    mutationFn: ({
      id,
      data,
    }: {
      id: string;
      data: UpdateInvoiceRequest;
    }) => invoiceService.updateInvoice(id, data),
    onSuccess: (_, variables) => {
      queryClient.invalidateQueries({ queryKey: ["invoices"] });
      queryClient.invalidateQueries({ queryKey: ["invoices", variables.id] });
      toast({
        title: "Success",
        description: "Invoice updated successfully",
      });
    },
    onError: (error: Error) => {
      toast({
        title: "Error",
        description: error.message || "Failed to update invoice",
        variant: "destructive",
      });
    },
  });
}

/**
 * React Query hook for marking an invoice as sent.
 */
export function useMarkInvoiceAsSent() {
  const queryClient = useQueryClient();
  const { toast } = useToast();

  return useMutation({
    mutationFn: (id: string) => invoiceService.markInvoiceAsSent(id),
    onSuccess: (_, id) => {
      queryClient.invalidateQueries({ queryKey: ["invoices"] });
      queryClient.invalidateQueries({ queryKey: ["invoices", id] });
      toast({
        title: "Success",
        description: "Invoice marked as sent",
      });
    },
    onError: (error: Error) => {
      toast({
        title: "Error",
        description: error.message || "Failed to mark invoice as sent",
        variant: "destructive",
      });
    },
  });
}

/**
 * React Query hook for adding a line item to an invoice.
 */
export function useAddLineItem() {
  const queryClient = useQueryClient();
  const { toast } = useToast();

  return useMutation({
    mutationFn: ({
      invoiceId,
      lineItem,
    }: {
      invoiceId: string;
      lineItem: AddLineItemRequest;
    }) => invoiceService.addLineItem(invoiceId, lineItem),
    onSuccess: (_, variables) => {
      queryClient.invalidateQueries({ queryKey: ["invoices", variables.invoiceId] });
      toast({
        title: "Success",
        description: "Line item added successfully",
      });
    },
    onError: (error: Error) => {
      toast({
        title: "Error",
        description: error.message || "Failed to add line item",
        variant: "destructive",
      });
    },
  });
}

/**
 * React Query hook for removing a line item from an invoice.
 */
export function useRemoveLineItem() {
  const queryClient = useQueryClient();
  const { toast } = useToast();

  return useMutation({
    mutationFn: ({
      invoiceId,
      lineItemId,
    }: {
      invoiceId: string;
      lineItemId: string;
    }) => invoiceService.removeLineItem(invoiceId, lineItemId),
    onSuccess: (_, variables) => {
      queryClient.invalidateQueries({ queryKey: ["invoices", variables.invoiceId] });
      toast({
        title: "Success",
        description: "Line item removed successfully",
      });
    },
    onError: (error: Error) => {
      toast({
        title: "Error",
        description: error.message || "Failed to remove line item",
        variant: "destructive",
      });
    },
  });
}

