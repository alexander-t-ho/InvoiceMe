"use client";

import { QueryClient, QueryClientProvider } from "@tanstack/react-query";
import { useState } from "react";
import { AuthProvider } from "@/contexts/AuthContext";

export function Providers({ children }: { children: React.ReactNode }) {
  const [queryClient] = useState(
    () =>
      new QueryClient({
        defaultOptions: {
          queries: {
            staleTime: 5 * 60 * 1000, // 5 minutes - data doesn't change that frequently
            gcTime: 10 * 60 * 1000, // 10 minutes - keep in cache longer (cacheTime renamed to gcTime in v5)
            refetchOnWindowFocus: false,
            refetchOnMount: false, // Don't refetch if data is fresh - critical for fast navigation
            refetchOnReconnect: false, // Don't refetch on reconnect if data is fresh
            retry: 1,
            retryDelay: 1000, // Wait 1 second before retry
            // Enable request deduplication
            structuralSharing: true,
            // Use cached data immediately while refetching in background
            placeholderData: (previousData) => previousData,
            // Network mode - only fetch when online
            networkMode: 'online',
            // Optimize for fast navigation - prioritize cached data
            notifyOnChangeProps: ['data', 'error'], // Only notify on data/error changes, not loading states
          },
        },
      })
  );

  return (
    <QueryClientProvider client={queryClient}>
      <AuthProvider>{children}</AuthProvider>
    </QueryClientProvider>
  );
}

