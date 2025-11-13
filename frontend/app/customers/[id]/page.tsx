"use client";

import { useState, useEffect, Suspense } from "react";
import { useParams, useSearchParams } from "next/navigation";
import { ProtectedRoute } from "@/components/auth/protected-route";
import { MainLayout } from "@/components/layout/main-layout";
import { CustomerForm } from "@/components/customers/customer-form";
import { CustomerDetailStats } from "@/components/customers/customer-detail-stats";
import { useCustomer, useUpdateCustomer } from "@/hooks/useCustomers";
import { useInvoices } from "@/hooks/useInvoices";
import { LoadingSpinner } from "@/components/ui/loading-spinner";
import { Card, CardContent, CardDescription, CardHeader, CardTitle } from "@/components/ui/card";
import { Button } from "@/components/ui/button";
import { Edit, ArrowLeft } from "lucide-react";
import Link from "next/link";
import type { UpdateCustomerFormData } from "@/lib/validations/customer";

function CustomerDetailPageContent() {
  const params = useParams();
  const searchParams = useSearchParams();
  const customerId = params.id as string;
  const [isEditing, setIsEditing] = useState(searchParams.get("edit") === "true");

  const { data: customer, isLoading: customerLoading, error } = useCustomer(customerId);
  const { data: invoicesData, isLoading: invoicesLoading } = useInvoices({
    customerId,
    page: 0,
    size: 100, // Limit to 100 invoices for performance
  });
  const updateCustomer = useUpdateCustomer();
  
  // Removed heavy invoice fetching - calculate stats from summary data only
  // This prevents blocking page navigation with 20+ API calls

  // Calculate stats from invoices - optimized to use summary data only
  // This avoids blocking navigation with heavy API calls
  const calculateStats = () => {
    if (!invoicesData?.content) {
      return {
        totalPaid: 0,
        onTimePaymentPercentage: 0,
        totalInvoices: 0,
      };
    }

    const invoices = invoicesData.content;
    const totalInvoices = invoices.length;

    // Calculate total paid (sum of all payments) from summary data
    let totalPaid = 0;
    let onTimeCount = 0;
    let paidInvoicesCount = 0;
    const today = new Date();

    invoices.forEach((invoice) => {
      const amountPaid = invoice.totalAmount - invoice.balance;
      totalPaid += amountPaid;

      // Check if invoice has been paid (has payments)
      if (amountPaid > 0) {
        paidInvoicesCount++;
        
        // Use heuristic based on summary data - much faster than fetching full details
        const dueDate = new Date(invoice.dueDate);
        if (invoice.status === "PAID" && invoice.balance === 0) {
          // Fully paid invoice - assume on time if due date hasn't passed
          if (dueDate >= today) {
            onTimeCount++;
          }
        } else if (invoice.balance > 0 && dueDate >= today) {
          // Partially paid but not overdue yet
          onTimeCount++;
        }
      }
    });

    // Calculate on-time payment percentage
    const onTimePaymentPercentage =
      paidInvoicesCount > 0 ? (onTimeCount / paidInvoicesCount) * 100 : 0;

    return {
      totalPaid,
      onTimePaymentPercentage,
      totalInvoices,
    };
  };

  const stats = calculateStats();
  // Optimistic rendering - show page structure immediately
  // Only show full loading if we have no cached data
  const showFullLoading = (customerLoading || invoicesLoading) && !customer && !invoicesData;

  const handleSubmit = (data: UpdateCustomerFormData) => {
    updateCustomer.mutate(
      { id: customerId, data },
      {
        onSuccess: () => {
          setIsEditing(false);
        },
      }
    );
  };

  // Only show full loading if we have no cached data
  if (showFullLoading) {
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

  if (error || !customer) {
    return (
      <ProtectedRoute>
        <MainLayout>
          <div className="flex items-center justify-center py-12">
            <div className="text-center">
              <p className="text-destructive">Error loading customer</p>
              <p className="text-sm text-muted-foreground mt-2">
                {error instanceof Error ? error.message : "Customer not found"}
              </p>
            </div>
          </div>
        </MainLayout>
      </ProtectedRoute>
    );
  }

  if (isEditing) {
    return (
      <ProtectedRoute>
        <MainLayout>
          <div className="space-y-6">
            <div className="flex items-center justify-between">
              <div>
                <h1 className="text-3xl font-bold tracking-tight">Edit Customer</h1>
                <p className="text-muted-foreground">
                  Update customer information
                </p>
              </div>
              <Button
                variant="outline"
                onClick={() => setIsEditing(false)}
              >
                <ArrowLeft className="mr-2 h-4 w-4" />
                Back to View
              </Button>
            </div>

            <Card>
              <CardHeader>
                <CardTitle>Customer Information</CardTitle>
                <CardDescription>
                  Update the customer details below
                </CardDescription>
              </CardHeader>
              <CardContent>
                <CustomerForm
                  initialData={{
                    name: customer.name,
                    email: customer.email,
                    address: customer.address,
                  }}
                  onSubmit={handleSubmit}
                  isLoading={updateCustomer.isPending}
                  submitLabel="Update Customer"
                />
              </CardContent>
            </Card>
          </div>
        </MainLayout>
      </ProtectedRoute>
    );
  }

  return (
    <ProtectedRoute>
      <MainLayout>
        <div className="space-y-6">
          <div className="flex items-center justify-between">
            <Link href="/customers" prefetch={true}>
              <Button variant="outline">
                <ArrowLeft className="mr-2 h-4 w-4" />
                Back to Customers
              </Button>
            </Link>
            <Button onClick={() => setIsEditing(true)}>
              <Edit className="mr-2 h-4 w-4" />
              Edit Customer
            </Button>
          </div>

          {(customerLoading || invoicesLoading) && (
            <div className="flex items-center justify-center py-4">
              <LoadingSpinner size="sm" />
              <span className="ml-2 text-sm text-muted-foreground">Loading customer data...</span>
            </div>
          )}

          <CustomerDetailStats
            totalPaid={stats.totalPaid}
            onTimePaymentPercentage={stats.onTimePaymentPercentage}
            totalInvoices={stats.totalInvoices}
            isLoading={customerLoading || invoicesLoading}
            customerName={customer?.name || ""}
          />
        </div>
      </MainLayout>
    </ProtectedRoute>
  );
}

export default function CustomerDetailPage() {
  return (
    <Suspense fallback={
      <ProtectedRoute>
        <MainLayout>
          <div className="flex items-center justify-center py-12">
            <LoadingSpinner size="lg" />
          </div>
        </MainLayout>
      </ProtectedRoute>
    }>
      <CustomerDetailPageContent />
    </Suspense>
  );
}

