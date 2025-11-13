"use client";

import { useViewportPrefetch } from "@/hooks/useViewportPrefetch";

/**
 * Provider component that enables viewport-based prefetching.
 * This should be placed high in the component tree.
 */
export function ViewportPrefetchProvider({ children }: { children: React.ReactNode }) {
  useViewportPrefetch();
  return <>{children}</>;
}

