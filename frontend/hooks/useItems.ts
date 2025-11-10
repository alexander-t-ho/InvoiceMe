import { useQuery, useMutation, useQueryClient } from "@tanstack/react-query";
import { itemsApi } from "@/lib/api/items";
import { useToast } from "@/hooks/use-toast";
import type {
  ItemResponse,
  CreateItemRequest,
  UpdateItemRequest,
  PagedResponse,
} from "@/types/api";

/**
 * React Query hook for fetching all items with pagination.
 */
export function useItems(page: number = 0, size: number = 20) {
  return useQuery({
    queryKey: ["items", page, size],
    queryFn: () => itemsApi.getAll(page, size),
    placeholderData: (previousData) => previousData, // Show previous data while fetching new data (React Query v5)
  });
}

/**
 * React Query hook for fetching a single item by ID.
 */
export function useItem(id: string | undefined) {
  return useQuery({
    queryKey: ["items", id],
    queryFn: () => itemsApi.getById(id!),
    enabled: !!id,
  });
}

/**
 * React Query hook for creating an item.
 */
export function useCreateItem() {
  const queryClient = useQueryClient();
  const { toast } = useToast();

  return useMutation({
    mutationFn: (data: CreateItemRequest) => itemsApi.create(data),
    onSuccess: () => {
      queryClient.invalidateQueries({ queryKey: ["items"] });
      toast({
        title: "Success",
        description: "Item created successfully",
      });
    },
    onError: (error: Error) => {
      toast({
        title: "Error",
        description: error.message || "Failed to create item",
        variant: "destructive",
      });
    },
  });
}

/**
 * React Query hook for updating an item.
 */
export function useUpdateItem() {
  const queryClient = useQueryClient();
  const { toast } = useToast();

  return useMutation({
    mutationFn: ({
      id,
      data,
    }: {
      id: string;
      data: UpdateItemRequest;
    }) => itemsApi.update(id, data),
    onSuccess: (_, variables) => {
      queryClient.invalidateQueries({ queryKey: ["items"] });
      queryClient.invalidateQueries({ queryKey: ["items", variables.id] });
      toast({
        title: "Success",
        description: "Item updated successfully",
      });
    },
    onError: (error: Error) => {
      toast({
        title: "Error",
        description: error.message || "Failed to update item",
        variant: "destructive",
      });
    },
  });
}

/**
 * React Query hook for deleting an item.
 */
export function useDeleteItem() {
  const queryClient = useQueryClient();
  const { toast } = useToast();

  return useMutation({
    mutationFn: (id: string) => itemsApi.delete(id),
    onSuccess: () => {
      queryClient.invalidateQueries({ queryKey: ["items"] });
      toast({
        title: "Success",
        description: "Item deleted successfully",
      });
    },
    onError: (error: Error) => {
      toast({
        title: "Error",
        description: error.message || "Failed to delete item",
        variant: "destructive",
      });
    },
  });
}


