"use client";

import { usePathname } from "next/navigation";
import { useEffect, useState, useTransition } from "react";

export function NavigationLoading() {
  const pathname = usePathname();
  const [isPending, startTransition] = useTransition();
  const [isNavigating, setIsNavigating] = useState(false);
  const [prevPathname, setPrevPathname] = useState(pathname);

  useEffect(() => {
    if (pathname !== prevPathname) {
      setIsNavigating(true);
      setPrevPathname(pathname);
      // Clear navigation state after navigation completes
      const timer = setTimeout(() => {
        startTransition(() => {
          setIsNavigating(false);
        });
      }, 300);
      return () => clearTimeout(timer);
    }
  }, [pathname, prevPathname, startTransition]);

  // Show loading indicator during navigation or pending transitions
  if (!isNavigating && !isPending) return null;

  return (
    <div className="fixed top-0 left-0 right-0 h-1 bg-slate-700 z-50">
      <div 
        className="h-full bg-blue-500 transition-all duration-300 ease-out" 
        style={{ width: isPending ? '80%' : '60%' }} 
      />
    </div>
  );
}
