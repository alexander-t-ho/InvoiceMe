"use client";

import { useState, useEffect } from "react";
import { useParams } from "next/navigation";
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
import { invoiceService } from "@/lib/services/InvoiceService";
import type { UpdateCustomerFormData } from "@/lib/validations/customer";
import type { InvoiceResponse } from "@/types/api";

export default function CustomerDetailPage() {
  const params = useParams();
  const customerId = params.id as string;
  const [isEditing, setIsEditing] = useState(false);

  const { data: customer, isLoading: customerLoading, error } = useCustomer(customerId);
  const { data: invoicesData, isLoading: invoicesLoading } = useInvoices({
    customerId,
    page: 0,
    size: 1000, // Get all invoices for this customer
  });
  const updateCustomer = useUpdateCustomer();
  const [fullInvoices, setFullInvoices] = useState<InvoiceResponse[]>([]);
  const [loadingFullInvoices, setLoadingFullInvoices] = useState(false);

  // Fetch full invoice details for accurate payment date calculation
  useEffect(() => {
    const fetchFullInvoices = async () => {
      if (!invoicesData?.content || invoicesData.content.length === 0) {
        setFullInvoices([]);
        return;
      }

      setLoadingFullInvoices(true);
      try {
        // Fetch full details for all invoices (or at least paid ones)
        const fullInvoicePromises = invoicesData.content.map((invoice) =>
          invoiceService.getInvoiceById(invoice.id)
        );
        const fullInvoicesData = await Promise.all(fullInvoicePromises);
        setFullInvoices(fullInvoicesData);
      } catch (error) {
        console.error("Error fetching full invoice details:", error);
        setFullInvoices([]);
      } finally {
        setLoadingFullInvoices(false);
      }
    };

    fetchFullInvoices();
  }, [invoicesData]);

  // Calculate stats from invoices
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

    // Calculate total paid (sum of all payments)
    let totalPaid = 0;
    let onTimeCount = 0;
    let paidInvoicesCount = 0;

    invoices.forEach((invoice) => {
      const amountPaid = invoice.totalAmount - invoice.balance;
      totalPaid += amountPaid;

      // Check if invoice has been paid (has payments)
      if (amountPaid > 0) {
        paidInvoicesCount++;
        
        // Find full invoice details to check payment dates
        const fullInvoice = fullInvoices.find((inv) => inv.id === invoice.id);
        const dueDate = new Date(invoice.dueDate);
        
        if (fullInvoice && fullInvoice.payments && fullInvoice.payments.length > 0) {
          // Check if all payments were made on or before the due date
          const allPaymentsOnTime = fullInvoice.payments.every((payment) => {
            const paymentDate = new Date(payment.paymentDate);
            return paymentDate <= dueDate;
          });
          
          if (allPaymentsOnTime) {
            onTimeCount++;
          }
        } else {
          // Fallback: use heuristic if full invoice details not available
          const today = new Date();
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
  const isLoading = customerLoading || invoicesLoading || loadingFullInvoices;

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
            <Link href="/customers">
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

          <CustomerDetailStats
            totalPaid={stats.totalPaid}
            onTimePaymentPercentage={stats.onTimePaymentPercentage}
            totalInvoices={stats.totalInvoices}
            isLoading={isLoading}
            customerName={customer.name}
          />
        </div>
      </MainLayout>
    </ProtectedRoute>
  );
}

