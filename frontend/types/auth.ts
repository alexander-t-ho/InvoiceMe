export interface User {
  username: string;
  email: string;
  userType?: "ADMIN" | "CUSTOMER";
  userId?: string;
  customerId?: string;
}

export interface AuthResponse {
  token: string;
  username: string;
  email: string;
}

export interface UnifiedAuthResponse {
  userType: "ADMIN" | "CUSTOMER";
  token: string;
  userId: string | null;
  name: string;
  email: string;
  customerId: string | null;
}

export interface LoginRequest {
  username: string;
  password: string;
}

export interface UnifiedLoginRequest {
  identifier: string;
  password: string;
}

export interface RegisterRequest {
  username: string;
  email: string;
  password: string;
}

