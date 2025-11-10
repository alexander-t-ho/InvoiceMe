"use client";

import { ProtectedRoute } from "@/components/auth/protected-route";
import { MainLayout } from "@/components/layout/main-layout";
import { BouncyCardsFeatures } from "@/components/ui/bounce-card-features";
import { useCustomers } from "@/hooks/useCustomers";
import { useInvoices } from "@/hooks/useInvoices";

export default function HomePage() {
  const { data: customersData, isLoading: customersLoading } = useCustomers(0, 1);
  const { data: invoicesData, isLoading: invoicesLoading } = useInvoices({ page: 0, size: 1 });
  
  const totalCustomers = customersData?.totalElements ?? 0;
  const totalInvoices = invoicesData?.totalElements ?? 0;
  
  // Optimized: Only fetch a small sample for display, calculate from summary if available
  // For now, show totals without expensive calculations - can be enhanced with a stats endpoint later
  const { data: paidInvoicesSample, isLoading: paymentsLoading } = useInvoices({ status: "PAID", page: 0, size: 10 });
  
  // Calculate from sample (approximate) - in production, use a dedicated stats endpoint
  const totalRevenue = paidInvoicesSample?.content.reduce((sum, inv) => sum + inv.totalAmount, 0) ?? 0;
  const totalPayments = paidInvoicesSample?.content.reduce((sum, inv) => sum + (inv.totalAmount - inv.balance), 0) ?? 0;

  const isLoading = customersLoading || invoicesLoading || paymentsLoading;

  return (
    <ProtectedRoute>
      <MainLayout>
        <BouncyCardsFeatures
          totalCustomers={totalCustomers}
          totalInvoices={totalInvoices}
          totalPayments={totalPayments}
          totalRevenue={totalRevenue}
          isLoading={isLoading}
        />
      </MainLayout>
    </ProtectedRoute>
  );
}
