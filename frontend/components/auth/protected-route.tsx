"use client";

import { useEffect } from "react";
import { useRouter } from "next/navigation";
import { useAuth } from "@/contexts/AuthContext";
import { LoadingSpinner } from "@/components/ui/loading-spinner";

interface ProtectedRouteProps {
  children: React.ReactNode;
}

export function ProtectedRoute({ children }: ProtectedRouteProps) {
  const { isAuthenticated, isLoading, userType } = useAuth();
  const router = useRouter();

  useEffect(() => {
    if (!isLoading && !isAuthenticated) {
      router.push("/login");
    } else if (!isLoading && isAuthenticated && userType === "CUSTOMER") {
      // Redirect customers to their portal if they try to access admin pages
      router.push("/portal/invoices");
    }
  }, [isAuthenticated, isLoading, userType, router]);

  // Show children immediately if authenticated as admin, don't block on loading
  // This allows the page to start rendering while auth check completes
  if (!isLoading && isAuthenticated && userType === "ADMIN") {
    return <>{children}</>;
  }

  // Only show loading if we're actually checking auth
  if (isLoading) {
    return (
      <div className="flex items-center justify-center min-h-screen">
        <LoadingSpinner size="lg" />
      </div>
    );
  }

  // Not authenticated and not loading - will redirect
  // Or customer trying to access admin page - will redirect
  return null;
}

