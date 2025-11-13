import { useQuery, useMutation, useQueryClient } from "@tanstack/react-query";
import { customerService } from "@/lib/services/CustomerService";
import { useToast } from "@/hooks/use-toast";
import type {
  CustomerResponse,
  CreateCustomerRequest,
  UpdateCustomerRequest,
  PagedResponse,
} from "@/types/api";

/**
 * React Query hook for fetching all customers with pagination.
 */
export function useCustomers(
  page: number = 0,
  size: number = 20,
  sortBy: string = "name"
) {
  return useQuery({
    queryKey: ["customers", page, size, sortBy],
    queryFn: () => customerService.getAllCustomers(page, size, sortBy),
    placeholderData: (previousData) => previousData, // Show previous data while fetching new data (React Query v5)
    // Removed refetchOnMount: true - rely on staleTime and cache for better performance
  });
}

/**
 * React Query hook for fetching a single customer by ID.
 */
export function useCustomer(id: string | undefined) {
  return useQuery({
    queryKey: ["customers", id],
    queryFn: () => customerService.getCustomerById(id!),
    enabled: !!id,
  });
}

/**
 * React Query hook for creating a customer.
 */
export function useCreateCustomer() {
  const queryClient = useQueryClient();
  const { toast } = useToast();

  return useMutation({
    mutationFn: (data: CreateCustomerRequest) =>
      customerService.createCustomer(data),
    onSuccess: (customer) => {
      // Invalidate all customer queries to ensure list refreshes
      queryClient.invalidateQueries({ queryKey: ["customers"] });
      toast({
        title: "Success",
        description: `Customer created successfully. They can log in with email "${customer.email}" and password "123456".`,
      });
    },
    onError: (error: Error) => {
      toast({
        title: "Error",
        description: error.message || "Failed to create customer",
        variant: "destructive",
      });
    },
  });
}

/**
 * React Query hook for updating a customer.
 */
export function useUpdateCustomer() {
  const queryClient = useQueryClient();
  const { toast } = useToast();

  return useMutation({
    mutationFn: ({
      id,
      data,
    }: {
      id: string;
      data: UpdateCustomerRequest;
    }) => customerService.updateCustomer(id, data),
    onSuccess: (_, variables) => {
      queryClient.invalidateQueries({ queryKey: ["customers"] });
      queryClient.invalidateQueries({ queryKey: ["customers", variables.id] });
      toast({
        title: "Success",
        description: "Customer updated successfully",
      });
    },
    onError: (error: Error) => {
      toast({
        title: "Error",
        description: error.message || "Failed to update customer",
        variant: "destructive",
      });
    },
  });
}

/**
 * React Query hook for deleting a customer.
 */
export function useDeleteCustomer() {
  const queryClient = useQueryClient();
  const { toast } = useToast();

  return useMutation({
    mutationFn: (id: string) => customerService.deleteCustomer(id),
    onSuccess: () => {
      queryClient.invalidateQueries({ queryKey: ["customers"] });
      toast({
        title: "Success",
        description: "Customer deleted successfully",
      });
    },
    onError: (error: Error) => {
      toast({
        title: "Error",
        description: error.message || "Failed to delete customer",
        variant: "destructive",
      });
    },
  });
}

