"use client";

import { useMemo } from "react";
import { useQuery } from "@tanstack/react-query";
import { useAuth } from "@/contexts/AuthContext";
import { ProtectedRoute } from "@/components/auth/protected-route";
import { MainLayout } from "@/components/layout/main-layout";
import { BouncyCardsFeatures } from "@/components/ui/bounce-card-features";
import { customerService } from "@/lib/services/CustomerService";
import { invoiceService } from "@/lib/services/InvoiceService";
import { itemsApi } from "@/lib/api/items";
import type { InvoiceSummaryResponse } from "@/types/api";

// Constants
const STALE_TIME_MS = 5 * 60 * 1000; // 5 minutes
const COUNT_PAGE_SIZE = 1; // Only need total count
const PAID_INVOICES_PAGE_SIZE = 10; // Sample size for revenue calculation

/**
 * Calculate revenue and payment statistics from paid invoices
 */
function calculatePaymentStats(invoices: InvoiceSummaryResponse[]) {
  const totalRevenue = invoices.reduce((sum, inv) => sum + inv.totalAmount, 0);
  const totalPayments = invoices.reduce((sum, inv) => sum + (inv.totalAmount - inv.balance), 0);
  return { totalRevenue, totalPayments };
}

/**
 * Admin dashboard page showing overview statistics
 */
export default function HomePage() {
  const { isAuthenticated, isLoading: authLoading } = useAuth();
  const shouldFetch = isAuthenticated && !authLoading;
  
  // Fetch dashboard statistics in parallel
  const { data: customersData, isLoading: customersLoading } = useQuery({
    queryKey: ["customers", 0, COUNT_PAGE_SIZE],
    queryFn: () => customerService.getAllCustomers(0, COUNT_PAGE_SIZE),
    enabled: shouldFetch,
    staleTime: STALE_TIME_MS,
  });
  
  const { data: invoicesData, isLoading: invoicesLoading } = useQuery({
    queryKey: ["invoices", { page: 0, size: COUNT_PAGE_SIZE }],
    queryFn: () => invoiceService.getAllInvoices({ page: 0, size: COUNT_PAGE_SIZE }),
    enabled: shouldFetch,
    staleTime: STALE_TIME_MS,
  });
  
  const { data: paidInvoicesData, isLoading: paymentsLoading } = useQuery({
    queryKey: ["invoices", { status: "PAID", page: 0, size: PAID_INVOICES_PAGE_SIZE }],
    queryFn: () => invoiceService.getAllInvoices({ status: "PAID", page: 0, size: PAID_INVOICES_PAGE_SIZE }),
    enabled: shouldFetch,
    staleTime: STALE_TIME_MS,
  });
  
  const { data: itemsData, isLoading: itemsLoading } = useQuery({
    queryKey: ["items", 0, COUNT_PAGE_SIZE],
    queryFn: () => itemsApi.getAll(0, COUNT_PAGE_SIZE),
    enabled: shouldFetch,
    staleTime: STALE_TIME_MS,
  });
  
  // Extract totals with safe defaults
  const stats = useMemo(() => {
    const totalCustomers = customersData?.totalElements ?? 0;
    const totalInvoices = invoicesData?.totalElements ?? 0;
    const totalItems = itemsData?.totalElements ?? 0;
    const paidInvoices = paidInvoicesData?.content ?? [];
    const { totalRevenue, totalPayments } = calculatePaymentStats(paidInvoices);
    
    return {
      totalCustomers,
      totalInvoices,
      totalItems,
      totalRevenue,
      totalPayments,
    };
  }, [customersData, invoicesData, itemsData, paidInvoicesData]);

  const isLoading = customersLoading || invoicesLoading || paymentsLoading || itemsLoading;

  return (
    <ProtectedRoute>
      <MainLayout>
        <BouncyCardsFeatures
          totalCustomers={stats.totalCustomers}
          totalInvoices={stats.totalInvoices}
          totalPayments={stats.totalPayments}
          totalRevenue={stats.totalRevenue}
          totalItems={stats.totalItems}
          isLoading={isLoading}
        />
      </MainLayout>
    </ProtectedRoute>
  );
}
