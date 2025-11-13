"use client";

import { useEffect } from "react";
import { useQueryClient } from "@tanstack/react-query";
import { useAuth } from "@/contexts/AuthContext";
import { prefetchAllMainRoutes } from "@/lib/prefetch-enhanced";

/**
 * Component that prefetches all main routes after authentication.
 * This improves navigation performance by preloading data.
 */
export function AppPrefetch() {
  const queryClient = useQueryClient();
  const { isAuthenticated, isLoading } = useAuth();

  useEffect(() => {
    // Only prefetch if authenticated and not loading
    if (isAuthenticated && !isLoading) {
      // Prefetch with a small delay to avoid blocking initial render
      const timer = setTimeout(() => {
        prefetchAllMainRoutes(queryClient);
      }, 500);

      return () => clearTimeout(timer);
    }
  }, [isAuthenticated, isLoading, queryClient]);

  return null;
}

