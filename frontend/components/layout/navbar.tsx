"use client";

import { useState, useEffect } from "react";
import Link from "next/link";
import { usePathname } from "next/navigation";
import { useQueryClient } from "@tanstack/react-query";
import { cn } from "@/lib/utils";
import { useAuth } from "@/contexts/AuthContext";
import { Button } from "@/components/ui/button";
import { LogOut } from "lucide-react";
import { prefetchRouteData } from "@/lib/prefetch-enhanced";

export function Navbar() {
  const pathname = usePathname();
  const queryClient = useQueryClient();
  const { user, logout, isAuthenticated, userType } = useAuth();
  const [mounted, setMounted] = useState(false);

  // Ensure we only render client-specific content after hydration
  useEffect(() => {
    setMounted(true);
  }, []);

  // Don't show navbar on login page
  if (pathname === "/login") {
    return null;
  }

  // Different nav items for customers vs admins
  // Use useMemo to ensure consistent rendering during SSR
  const navItems = (() => {
    // During SSR or before mount, always use admin items to prevent hydration mismatch
    if (!mounted) {
      return [
        { href: "/", label: "Dashboard" },
        { href: "/customers", label: "Customers" },
        { href: "/invoices", label: "Invoices" },
        { href: "/items", label: "Items" },
        { href: "/payments", label: "Payments" },
      ];
    }
    
    // After hydration, use user-specific items
    if (userType === "CUSTOMER") {
      return [
        { href: "/portal", label: "Dashboard" },
        { href: "/portal/invoices", label: "My Invoices" },
      ];
    }
    
    // Default to admin items
    return [
      { href: "/", label: "Dashboard" },
      { href: "/customers", label: "Customers" },
      { href: "/invoices", label: "Invoices" },
      { href: "/items", label: "Items" },
      { href: "/payments", label: "Payments" },
    ];
  })();

  return (
    <nav className="border-b border-slate-700 bg-[#1e3a5f]">
      <div className="mx-auto max-w-7xl px-4 sm:px-6 lg:px-8">
        <div className="flex h-16 items-center justify-between">
          <div className="flex items-center">
            <Link href="/" className="text-xl font-bold text-slate-100">
              GimmeYoMoney
            </Link>
            {mounted ? (
              <div className="ml-10 flex space-x-4">
                {navItems.map((item) => {
                  const isActive = pathname === item.href;
                  return (
                    <Link
                      key={item.href}
                      href={item.href}
                      prefetch={true}
                      onMouseEnter={() => {
                        // Prefetch data on hover for instant navigation
                        prefetchRouteData(queryClient, item.href);
                      }}
                      onFocus={() => {
                        // Also prefetch on keyboard focus
                        prefetchRouteData(queryClient, item.href);
                      }}
                      className={cn(
                        "px-3 py-2 text-sm font-medium transition-colors",
                        isActive
                          ? "text-blue-400 border-b-2 border-blue-400"
                          : "text-slate-300 hover:text-slate-100"
                      )}
                    >
                      {item.label}
                    </Link>
                  );
                })}
              </div>
            ) : (
              // Render placeholder during SSR to prevent hydration mismatch
              // Use the same structure as the actual nav items
              <div className="ml-10 flex space-x-4">
                {[
                  { href: "/", label: "Dashboard" },
                  { href: "/customers", label: "Customers" },
                  { href: "/invoices", label: "Invoices" },
                  { href: "/items", label: "Items" },
                  { href: "/payments", label: "Payments" },
                ].map((item) => (
                  <Link
                    key={item.href}
                    href={item.href}
                    className="px-3 py-2 text-sm font-medium text-slate-300"
                  >
                    {item.label}
                  </Link>
                ))}
              </div>
            )}
          </div>
          {mounted && isAuthenticated && (
            <div className="flex items-center space-x-4">
              <span className="text-sm text-slate-300">
                {user?.username}
              </span>
              <Button variant="outline" size="sm" onClick={logout} className="border-slate-600 text-slate-100 hover:bg-slate-700">
                <LogOut className="h-4 w-4 mr-2" />
                Logout
              </Button>
            </div>
          )}
        </div>
      </div>
    </nav>
  );
}
