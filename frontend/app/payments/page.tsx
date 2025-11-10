"use client";

import { ProtectedRoute } from "@/components/auth/protected-route";
import { MainLayout } from "@/components/layout/main-layout";
import { useInvoices } from "@/hooks/useInvoices";
import { LoadingSpinner } from "@/components/ui/loading-spinner";
import { Card, CardContent, CardDescription, CardHeader, CardTitle } from "@/components/ui/card";
import { Button } from "@/components/ui/button";
import Link from "next/link";
import { InvoiceStatusBadge } from "@/components/invoices/invoice-status-badge";

function formatDate(dateString: string): string {
  const date = new Date(dateString);
  return date.toLocaleDateString("en-US", { year: "numeric", month: "short", day: "numeric" });
}

export default function PaymentsPage() {
  // Get all invoices with payments (PAID or SENT with balance > 0)
  const { data: sentInvoices, isLoading: isLoadingSent } = useInvoices({
    status: "SENT",
    page: 0,
    size: 100,
  });
  const { data: paidInvoices, isLoading: isLoadingPaid } = useInvoices({
    status: "PAID",
    page: 0,
    size: 100,
  });

  const isLoading = isLoadingSent || isLoadingPaid;

  if (isLoading) {
    return (
      <ProtectedRoute>
        <MainLayout>
        <div className="flex items-center justify-center py-12">
          <LoadingSpinner size="lg" />
        </div>
      </MainLayout>
      </ProtectedRoute>
    );
  }

  const allInvoices = [
    ...(sentInvoices?.content || []),
    ...(paidInvoices?.content || []),
  ].filter((invoice) => invoice.balance < invoice.totalAmount); // Only invoices with payments

  return (
    <ProtectedRoute>
      <MainLayout>
      <div className="space-y-6">
        <div>
          <h1 className="text-3xl font-bold tracking-tight">Payments</h1>
          <p className="text-muted-foreground">
            View invoices with payment history
          </p>
        </div>

        {allInvoices.length > 0 ? (
          <div className="grid gap-4 md:grid-cols-2 lg:grid-cols-3">
            {allInvoices.map((invoice) => (
              <Card key={invoice.id}>
                <CardHeader>
                  <div className="flex items-center justify-between">
                    <CardTitle className="text-lg">
                      {invoice.customerName}
                    </CardTitle>
                    <InvoiceStatusBadge status={invoice.status} />
                  </div>
                  <CardDescription>
                    Due: {formatDate(invoice.dueDate)}
                  </CardDescription>
                </CardHeader>
                <CardContent className="space-y-2">
                  <div className="flex justify-between">
                    <span className="text-muted-foreground">Total:</span>
                    <span className="font-medium">
                      ${invoice.totalAmount.toFixed(2)}
                    </span>
                  </div>
                  <div className="flex justify-between">
                    <span className="text-muted-foreground">Balance:</span>
                    <span className="font-medium">
                      ${invoice.balance.toFixed(2)}
                    </span>
                  </div>
                  <div className="flex justify-between">
                    <span className="text-muted-foreground">Paid:</span>
                    <span className="font-medium text-success">
                      ${(invoice.totalAmount - invoice.balance).toFixed(2)}
                    </span>
                  </div>
                  <Link href={`/invoices/${invoice.id}`} className="block mt-4">
                    <Button variant="outline" className="w-full">
                      View Invoice
                    </Button>
                  </Link>
                </CardContent>
              </Card>
            ))}
          </div>
        ) : (
          <div className="text-center py-12">
            <p className="text-muted-foreground">
              No invoices with payments found
            </p>
          </div>
        )}
      </div>
    </MainLayout>
    </ProtectedRoute>
  );
}

