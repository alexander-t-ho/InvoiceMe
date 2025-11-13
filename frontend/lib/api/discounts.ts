import { apiClient } from "./client";

export interface DiscountCodeValidationResponse {
  isValid: boolean;
  message: string;
  discountPercent: number | null;
}

export interface DiscountCodeResponse {
  code: string;
  discountPercent: number;
  isActive: boolean;
  createdAt: string;
  updatedAt: string;
}

export const discountsApi = {
  /**
   * Validate a discount code.
   */
  validate: async (code: string): Promise<DiscountCodeValidationResponse> => {
    const response = await apiClient.get<DiscountCodeValidationResponse>(
      `/discount-codes/validate/${encodeURIComponent(code)}`
    );
    return response.data;
  },

  /**
   * List all discount codes.
   */
  listAll: async (): Promise<DiscountCodeResponse[]> => {
    const response = await apiClient.get<DiscountCodeResponse[]>("/discount-codes");
    return response.data;
  },
};









