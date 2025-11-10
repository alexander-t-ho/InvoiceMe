import { apiClient } from "./client";
import type { AuthResponse, LoginRequest, RegisterRequest, UnifiedLoginRequest, UnifiedAuthResponse } from "@/types/auth";

export const authApi = {
  /**
   * Login with username and password (legacy admin login).
   */
  login: async (data: LoginRequest): Promise<AuthResponse> => {
    const response = await apiClient.post<AuthResponse>("/auth/login", data);
    return response.data;
  },

  /**
   * Unified login for both admins and customers.
   */
  unifiedLogin: async (data: UnifiedLoginRequest): Promise<UnifiedAuthResponse> => {
    const response = await apiClient.post<UnifiedAuthResponse>("/auth/unified-login", data);
    return response.data;
  },

  /**
   * Register a new user.
   */
  register: async (data: RegisterRequest): Promise<AuthResponse> => {
    const response = await apiClient.post<AuthResponse>("/auth/register", data);
    return response.data;
  },
};

