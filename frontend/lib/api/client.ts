import axios, { AxiosError, AxiosInstance, InternalAxiosRequestConfig } from "axios";
import { ErrorResponse } from "@/types/api";

const API_BASE_URL = process.env.NEXT_PUBLIC_API_URL || "http://localhost:8081/api/v1";

/**
 * Creates an Axios instance with base configuration.
 */
export const apiClient: AxiosInstance = axios.create({
  baseURL: API_BASE_URL,
  headers: {
    "Content-Type": "application/json",
  },
  timeout: 15000, // Increased timeout for slower connections, but still reasonable
});

/**
 * Request interceptor: Add auth token if available.
 */
apiClient.interceptors.request.use(
  (config) => {
    // Get token from localStorage
    const token = typeof window !== "undefined" ? localStorage.getItem("auth_token") : null;
    if (token) {
      config.headers.Authorization = `Bearer ${token}`;
    }
    return config;
  },
  (error) => Promise.reject(error)
);

/**
 * Response interceptor: Handle 401 errors (unauthorized).
 */
apiClient.interceptors.response.use(
  (response) => response,
  (error: AxiosError<ErrorResponse>) => {
    if (error.response?.status === 401) {
      // Clear auth data and redirect to login (only if not on portal pages)
      if (typeof window !== "undefined") {
        const isPortalPage = window.location.pathname.startsWith("/portal");
        localStorage.removeItem("auth_token");
        localStorage.removeItem("auth_user");
        if (!isPortalPage) {
        window.location.href = "/login";
        }
      }
    }
    return Promise.reject(error);
  }
);
