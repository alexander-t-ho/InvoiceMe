import { apiClient } from "./client";
import type {
  ItemResponse,
  CreateItemRequest,
  UpdateItemRequest,
  PagedResponse,
} from "@/types/api";

export const itemsApi = {
  /**
   * Get all items with pagination.
   */
  getAll: async (
    page: number = 0,
    size: number = 20
  ): Promise<PagedResponse<ItemResponse>> => {
    const response = await apiClient.get<PagedResponse<ItemResponse>>(
      "/items",
      {
        params: { page, size },
      }
    );
    return response.data;
  },

  /**
   * Get item by ID.
   */
  getById: async (id: string): Promise<ItemResponse> => {
    const response = await apiClient.get<ItemResponse>(`/items/${id}`);
    return response.data;
  },

  /**
   * Create a new item.
   */
  create: async (data: CreateItemRequest): Promise<ItemResponse> => {
    const response = await apiClient.post<ItemResponse>("/items", data);
    return response.data;
  },

  /**
   * Update an existing item.
   */
  update: async (
    id: string,
    data: UpdateItemRequest
  ): Promise<ItemResponse> => {
    const response = await apiClient.put<ItemResponse>(
      `/items/${id}`,
      data
    );
    return response.data;
  },

  /**
   * Delete an item.
   */
  delete: async (id: string): Promise<void> => {
    await apiClient.delete(`/items/${id}`);
  },
};









