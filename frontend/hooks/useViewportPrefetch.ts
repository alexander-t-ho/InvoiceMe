"use client";

import { useEffect, useRef } from "react";
import { useQueryClient } from "@tanstack/react-query";
import { prefetchRouteData } from "@/lib/prefetch-enhanced";

/**
 * Hook to prefetch data for links when they become visible in the viewport.
 * This improves performance by prefetching data before users hover or click.
 */
export function useViewportPrefetch() {
  const queryClient = useQueryClient();
  const prefetchedRoutes = useRef<Set<string>>(new Set());

  useEffect(() => {
    // Only run in browser
    if (typeof window === "undefined") return;

    const observer = new IntersectionObserver(
      (entries) => {
        entries.forEach((entry) => {
          if (entry.isIntersecting) {
            const link = entry.target as HTMLAnchorElement;
            const href = link.getAttribute("href");
            
            if (href && !prefetchedRoutes.current.has(href)) {
              prefetchedRoutes.current.add(href);
              // Prefetch with a small delay to avoid blocking main thread
              setTimeout(() => {
                prefetchRouteData(queryClient, href);
              }, 100);
            }
          }
        });
      },
      {
        // Start prefetching when link is 200px away from viewport
        rootMargin: "200px",
        threshold: 0.01,
      }
    );

    // Observe all links in the document
    const links = document.querySelectorAll('a[href^="/"]');
    links.forEach((link) => {
      observer.observe(link);
    });

    // Observe dynamically added links
    const mutationObserver = new MutationObserver((mutations) => {
      mutations.forEach((mutation) => {
        mutation.addedNodes.forEach((node) => {
          if (node.nodeType === Node.ELEMENT_NODE) {
            const element = node as Element;
            const newLinks = element.querySelectorAll?.('a[href^="/"]');
            newLinks?.forEach((link) => {
              observer.observe(link);
            });
            // Also check if the node itself is a link
            if (element.tagName === "A" && element.getAttribute("href")?.startsWith("/")) {
              observer.observe(element);
            }
          }
        });
      });
    });

    mutationObserver.observe(document.body, {
      childList: true,
      subtree: true,
    });

    return () => {
      observer.disconnect();
      mutationObserver.disconnect();
    };
  }, [queryClient]);
}

