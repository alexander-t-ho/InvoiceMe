import { apiClient } from "./client";
import type {
  CustomerResponse,
  CreateCustomerRequest,
  UpdateCustomerRequest,
  PagedResponse,
} from "@/types/api";

export const customersApi = {
  /**
   * Get all customers with pagination.
   */
  getAll: async (
    page: number = 0,
    size: number = 20,
    sortBy: string = "name"
  ): Promise<PagedResponse<CustomerResponse>> => {
    const response = await apiClient.get<PagedResponse<CustomerResponse>>(
      "/customers",
      {
        params: { page, size, sortBy },
      }
    );
    return response.data;
  },

  /**
   * Get customer by ID.
   */
  getById: async (id: string): Promise<CustomerResponse> => {
    const response = await apiClient.get<CustomerResponse>(`/customers/${id}`);
    return response.data;
  },

  /**
       * Authenticate customer by email and password.
       */
      authenticate: async (email: string, password: string): Promise<CustomerResponse> => {
        const response = await apiClient.post<CustomerResponse>("/customers/authenticate", {
          email,
          password,
        });
        return response.data;
      },

      /**
       * Get customer by email.
       */
      getByEmail: async (email: string): Promise<CustomerResponse> => {
        const response = await apiClient.get<CustomerResponse>("/customers/search/by-email", {
          params: { email },
        });
        return response.data;
      },

  /**
   * Create a new customer.
   */
  create: async (data: CreateCustomerRequest): Promise<CustomerResponse> => {
    const response = await apiClient.post<CustomerResponse>("/customers", data);
    return response.data;
  },

  /**
   * Update an existing customer.
   */
  update: async (
    id: string,
    data: UpdateCustomerRequest
  ): Promise<CustomerResponse> => {
    const response = await apiClient.put<CustomerResponse>(
      `/customers/${id}`,
      data
    );
    return response.data;
  },

  /**
   * Delete a customer.
   */
  delete: async (id: string): Promise<void> => {
    await apiClient.delete(`/customers/${id}`);
  },
};

