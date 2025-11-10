"use client";

import Link from "next/link";
import { usePathname } from "next/navigation";
import { cn } from "@/lib/utils";
import { useAuth } from "@/contexts/AuthContext";
import { Button } from "@/components/ui/button";
import { LogOut } from "lucide-react";

export function Navbar() {
  const pathname = usePathname();
  const { user, logout, isAuthenticated } = useAuth();

  // Don't show navbar on login page
  if (pathname === "/login") {
    return null;
  }

  const navItems = [
    { href: "/", label: "Dashboard" },
    { href: "/customers", label: "Customers" },
    { href: "/invoices", label: "Invoices" },
    { href: "/items", label: "Items" },
    { href: "/payments", label: "Payments" },
  ];

  return (
    <nav className="border-b border-slate-700 bg-[#1e3a5f]">
      <div className="mx-auto max-w-7xl px-4 sm:px-6 lg:px-8">
        <div className="flex h-16 items-center justify-between">
          <div className="flex items-center">
            <Link href="/" className="text-xl font-bold text-slate-100">
              InvoiceMe
            </Link>
            <div className="ml-10 flex space-x-4">
              {navItems.map((item) => {
                const isActive = pathname === item.href;
                return (
                  <Link
                    key={item.href}
                    href={item.href}
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
          </div>
          {isAuthenticated && (
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
