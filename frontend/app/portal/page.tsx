"use client";

import { useEffect, useState } from "react";
import { useRouter } from "next/navigation";
import { useQuery } from "@tanstack/react-query";
import { useAuth } from "@/contexts/AuthContext";
import { ProtectedRoute } from "@/components/auth/protected-route";
import { MainLayout } from "@/components/layout/main-layout";
import { CustomerDashboardCards } from "@/components/ui/customer-dashboard-cards";
import { invoiceService } from "@/lib/services/InvoiceService";
import { LoadingSpinner } from "@/components/ui/loading-spinner";
import type { InvoiceSummaryResponse } from "@/types/api";

export default function CustomerPortalPage() {
  const router = useRouter();
  const { isAuthenticated, isLoading: authLoading, userType, user } = useAuth();
  const [mounted, setMounted] = useState(false);
  const [customerId, setCustomerId] = useState<string | null>(null);

  useEffect(() => {
    setMounted(true);
  }, []);

  useEffect(() => {
    if (mounted && !authLoading) {
      if (!isAuthenticated) {
        // Not authenticated, redirect to universal login - use replace
        router.replace("/login");
        return;
      }

      if (userType === "ADMIN") {
        // Admin should go to admin dashboard
        router.replace("/");
        return;
      }

      // Get customer ID
      if (userType === "CUSTOMER" && user?.customerId) {
        setCustomerId(user.customerId);
      } else {
        // Fallback to session storage
        const storedCustomerId = sessionStorage.getItem("portal_customer_id");
        if (storedCustomerId) {
          setCustomerId(storedCustomerId);
        } else {
          router.replace("/login");
        }
      }
    }
  }, [mounted, authLoading, isAuthenticated, userType, user, router]);

  // Fetch customer invoices - optimized with placeholderData for instant rendering
  const shouldFetch = isAuthenticated && !authLoading && customerId !== null;
  const { data: invoicesData, isLoading: invoicesLoading } = useQuery({
    queryKey: ["customer-invoices", customerId],
    queryFn: () => invoiceService.getCustomerInvoices(customerId!),
    enabled: shouldFetch,
    staleTime: 5 * 60 * 1000, // 5 minutes
    placeholderData: (previousData) => previousData, // Show cached data immediately
    refetchOnMount: false, // Don't refetch if data is fresh
  });

  // Calculate dashboard statistics
  const calculateStats = () => {
    if (!invoicesData?.content) {
      return {
        totalInvoices: 0,
        totalPaid: 0,
        outstandingBalance: 0,
        overdueInvoices: 0,
        unpaidInvoices: 0,
        soonestDueInvoiceId: null,
      };
    }

    const invoices: InvoiceSummaryResponse[] = invoicesData.content.filter(
      (invoice) => invoice.status === "SENT" || invoice.status === "PAID"
    );
    const totalInvoices = invoices.length;

    let totalPaid = 0;
    let outstandingBalance = 0;
    let overdueInvoices = 0;
    let unpaidInvoices = 0;
    let soonestDueInvoice: { id: string; dueDate: Date } | null = null;
    const today = new Date();

    invoices.forEach((invoice: InvoiceSummaryResponse) => {
      const amountPaid = invoice.totalAmount - invoice.balance;
      totalPaid += amountPaid;
      outstandingBalance += invoice.balance;

      // Check if unpaid (has balance remaining)
      if (invoice.balance > 0) {
        unpaidInvoices++;
        
        // Track invoice with soonest due date
        const dueDate = new Date(invoice.dueDate);
        if (!soonestDueInvoice || dueDate < soonestDueInvoice.dueDate) {
          soonestDueInvoice = {
            id: invoice.id,
            dueDate: dueDate,
          };
        }
      }

      // Check if overdue
      if (invoice.status === "SENT" && invoice.balance > 0) {
        const dueDate = new Date(invoice.dueDate);
        if (dueDate < today) {
          overdueInvoices++;
        }
      }
    });

    const soonestDueInvoiceId: string | null = soonestDueInvoice ? soonestDueInvoice.id : null;

    return {
      totalInvoices,
      totalPaid,
      outstandingBalance,
      overdueInvoices,
      unpaidInvoices,
      soonestDueInvoiceId,
    };
  };

  const stats = calculateStats();
  const isLoading = invoicesLoading || authLoading || !mounted;

  // Show loading while checking auth or fetching data
  if (!mounted || authLoading || (isAuthenticated && userType === "CUSTOMER" && !customerId)) {
    return (
      <div className="flex items-center justify-center min-h-screen bg-[#0f1e35]">
        <LoadingSpinner size="lg" />
      </div>
    );
  }

  // Only show dashboard for customers
  if (isAuthenticated && userType === "CUSTOMER") {
    return (
      <ProtectedRoute>
        <MainLayout>
          <CustomerDashboardCards
            totalInvoices={stats.totalInvoices}
            totalPaid={stats.totalPaid}
            outstandingBalance={stats.outstandingBalance}
            overdueInvoices={stats.overdueInvoices}
            unpaidInvoices={stats.unpaidInvoices}
            soonestDueInvoiceId={stats.soonestDueInvoiceId}
            isLoading={isLoading}
            customerName={user?.email || user?.username || "Customer"}
          />
        </MainLayout>
      </ProtectedRoute>
    );
  }

  // Fallback loading state
  return (
    <div className="flex items-center justify-center min-h-screen bg-[#0f1e35]">
      <LoadingSpinner size="lg" />
    </div>
  );
}

