"use client";

import { useEffect, useMemo } from "react";
import { useRouter, usePathname } from "next/navigation";
import { useAuth } from "@/contexts/AuthContext";
import { LoadingSpinner } from "@/components/ui/loading-spinner";

interface ProtectedRouteProps {
  children: React.ReactNode;
}

export function ProtectedRoute({ children }: ProtectedRouteProps) {
  const { isAuthenticated, isLoading, userType } = useAuth();
  const router = useRouter();
  const pathname = usePathname();

  // Memoize redirect logic to prevent unnecessary recalculations
  const shouldRedirect = useMemo(() => {
    if (isLoading) return null;
    if (!isAuthenticated) return "/login";
    
    // Allow customers to access portal routes
    if (userType === "CUSTOMER" && pathname?.startsWith("/portal")) {
      return null; // Customer is on a portal route, allow access
    }
    
    // Redirect customers trying to access admin routes
    if (userType === "CUSTOMER") return "/portal";
    
    // Redirect admins trying to access portal routes (optional - you may want to allow this)
    if (userType === "ADMIN" && pathname?.startsWith("/portal")) {
      return "/"; // Admin trying to access portal, redirect to admin dashboard
    }
    
    return null;
  }, [isLoading, isAuthenticated, userType, pathname]);

  useEffect(() => {
    if (shouldRedirect) {
      // Use replace instead of push to avoid adding to history stack
      router.replace(shouldRedirect);
    }
  }, [shouldRedirect, router]);

  // OPTIMIZATION: Show content immediately if auth is cached
  // Only show loading if we're actually redirecting or don't have cached auth
  if (isLoading) {
    // Check if we have cached auth data - if so, optimistically show content
    if (typeof window !== "undefined") {
      const cachedToken = localStorage.getItem("auth_token");
      if (cachedToken) {
        // Optimistically show content - auth check is fast and cached
        // This prevents blocking navigation while auth state loads
        return <>{children}</>;
      }
    }
    return (
      <div className="flex items-center justify-center min-h-screen">
        <LoadingSpinner size="lg" />
      </div>
    );
  }

  // Show children if authenticated and on correct route
  if (isAuthenticated) {
    // Allow customers on portal routes
    if (userType === "CUSTOMER" && pathname?.startsWith("/portal")) {
      return <>{children}</>;
    }
    // Allow admins on admin routes
    if (userType === "ADMIN" && !pathname?.startsWith("/portal")) {
      return <>{children}</>;
    }
  }

  // Only show loading if we're actually checking auth or redirecting
  // This prevents flash of empty content during redirect
  return (
    <div className="flex items-center justify-center min-h-screen">
      <LoadingSpinner size="lg" />
    </div>
  );
}

