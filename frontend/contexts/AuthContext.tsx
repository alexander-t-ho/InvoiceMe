"use client";

import React, { createContext, useContext, useState, useEffect, ReactNode, useMemo, useCallback } from "react";
import { authApi } from "@/lib/api/auth";
import type { User, LoginRequest, RegisterRequest } from "@/types/auth";
import { useRouter } from "next/navigation";

interface AuthContextType {
  user: User | null;
  token: string | null;
  userType: "ADMIN" | "CUSTOMER" | null;
  isAuthenticated: boolean;
  isLoading: boolean;
  login: (identifier: string, password: string) => Promise<void>;
  register: (username: string, email: string, password: string) => Promise<void>;
  logout: () => void;
}

const AuthContext = createContext<AuthContextType | undefined>(undefined);

const TOKEN_KEY = "auth_token";
const USER_KEY = "auth_user";

export function AuthProvider({ children }: { children: ReactNode }) {
  const [user, setUser] = useState<User | null>(null);
  const [token, setToken] = useState<string | null>(null);
  const [userType, setUserType] = useState<"ADMIN" | "CUSTOMER" | null>(null);
  const [isLoading, setIsLoading] = useState(true);
  const router = useRouter();

  // Load auth state from localStorage on mount
  useEffect(() => {
    const loadAuthState = () => {
      try {
        // Check if we're in the browser
        if (typeof window === "undefined") {
          setIsLoading(false);
          return;
        }

        const storedToken = localStorage.getItem(TOKEN_KEY);
        const storedUser = localStorage.getItem(USER_KEY);

        if (storedToken && storedUser) {
          try {
            const parsedUser = JSON.parse(storedUser);
            setToken(storedToken);
            setUser(parsedUser);
            setUserType(parsedUser.userType || null);
          } catch (error) {
            // Invalid stored data, clear it
            localStorage.removeItem(TOKEN_KEY);
            localStorage.removeItem(USER_KEY);
          }
        }
      } catch (error) {
        console.error("Error loading auth state:", error);
      } finally {
        setIsLoading(false);
      }
    };

    // Load immediately - no need for setTimeout delay
    loadAuthState();
  }, []);

  const login = useCallback(async (identifier: string, password: string) => {
    const request = { identifier, password };
    const response = await authApi.unifiedLogin(request);
    
    setToken(response.token);
    setUserType(response.userType);
    setUser({ 
      username: response.name, 
      email: response.email,
      userType: response.userType,
      userId: response.userId || undefined,
      customerId: response.customerId || undefined
    });
    
    localStorage.setItem(TOKEN_KEY, response.token);
    localStorage.setItem(USER_KEY, JSON.stringify({ 
      username: response.name, 
      email: response.email,
      userType: response.userType,
      userId: response.userId,
      customerId: response.customerId
    }));
  }, []);

  const register = useCallback(async (username: string, email: string, password: string) => {
    const request: RegisterRequest = { username, email, password };
    await authApi.register(request);
    
    // After registration, automatically login
    await login(username, password);
  }, [login]);

  const logout = useCallback(() => {
    setToken(null);
    setUser(null);
    setUserType(null);
    localStorage.removeItem(TOKEN_KEY);
    localStorage.removeItem(USER_KEY);
    router.replace("/login");
  }, [router]);

  // Memoize the context value to prevent unnecessary re-renders
  const value: AuthContextType = useMemo(() => ({
    user,
    token,
    userType,
    isAuthenticated: !!token && !!user,
    isLoading,
    login,
    register,
    logout,
  }), [user, token, userType, isLoading, login, register, logout]);

  return <AuthContext.Provider value={value}>{children}</AuthContext.Provider>;
}

export function useAuth() {
  const context = useContext(AuthContext);
  if (context === undefined) {
    throw new Error("useAuth must be used within an AuthProvider");
  }
  return context;
}

