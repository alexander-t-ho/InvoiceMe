"use client";

import { useEffect, useState } from "react";
import { useRouter } from "next/navigation";
import { useQueryClient } from "@tanstack/react-query";
import Link from "next/link";
import { Card, CardContent, CardDescription, CardHeader, CardTitle } from "@/components/ui/card";
import { Button } from "@/components/ui/button";
import { LoadingSpinner } from "@/components/ui/loading-spinner";
import { Badge } from "@/components/ui/badge";
import { MainLayout } from "@/components/layout/main-layout";
import { ProtectedRoute } from "@/components/auth/protected-route";
import { prefetchRouteData } from "@/lib/prefetch-enhanced";
import {
  Table,
  TableBody,
  TableCell,
  TableHead,
  TableHeader,
  TableRow,
} from "@/components/ui/table";
import { invoiceService } from "@/lib/services/InvoiceService";
import { useAuth } from "@/contexts/AuthContext";
import type { InvoiceSummaryResponse } from "@/types/api";
import { format } from "date-fns";
import { FileText, DollarSign } from "lucide-react";

export default function CustomerInvoicesPage() {
  const router = useRouter();
  const queryClient = useQueryClient();
  const { isAuthenticated, userType, user, logout } = useAuth();
  const [invoices, setInvoices] = useState<InvoiceSummaryResponse[]>([]);
  const [isLoading, setIsLoading] = useState(true);
  const [customerEmail, setCustomerEmail] = useState<string>("");
  const [customerId, setCustomerId] = useState<string | null>(null);
  const [isAdmin, setIsAdmin] = useState(false);

  useEffect(() => {
    // Check for admin authentication first
    const storedUser = localStorage.getItem("auth_user");
    if (storedUser) {
      try {
        const parsedUser = JSON.parse(storedUser);
        if (parsedUser.userType === "ADMIN" && isAuthenticated) {
          setIsAdmin(true);
          setCustomerEmail(parsedUser.email || parsedUser.username || "Admin");
          
          // Fetch all invoices for admin
          const fetchAllInvoices = async () => {
            try {
              const data = await invoiceService.getAllInvoices({ page: 0, size: 1000 });
              // Admins can see all invoices, but filter out DRAFT for portal view
              const visibleInvoices = data.content.filter(
                (invoice) => invoice.status === "SENT" || invoice.status === "PAID"
              );
              setInvoices(visibleInvoices);
            } catch (error) {
              console.error("Error fetching invoices:", error);
            } finally {
              setIsLoading(false);
            }
          };
          
          fetchAllInvoices();
          return;
        }
      } catch (error) {
        console.error("Error parsing user data:", error);
      }
    }

    // Check if customer is in session (fallback for customer-only login)
    const storedCustomerId = sessionStorage.getItem("portal_customer_id");
    const storedEmail = sessionStorage.getItem("portal_customer_email");

           if (!storedCustomerId && !isAuthenticated) {
             router.replace("/login");
             return;
           }

    // If authenticated as customer, get customerId from auth context
    if (isAuthenticated && userType === "CUSTOMER" && user?.customerId) {
      setCustomerId(user.customerId);
      setCustomerEmail(user.email || "");
      
      // Fetch invoices using the secure customer portal endpoint
      const fetchInvoices = async () => {
        if (!user.customerId) return;
        try {
          const data = await invoiceService.getCustomerInvoices(user.customerId);
          // Filter out DRAFT invoices - customers should only see SENT and PAID invoices
          const visibleInvoices = data.content.filter(
            (invoice) => invoice.status === "SENT" || invoice.status === "PAID"
          );
          setInvoices(visibleInvoices);
        } catch (error) {
          console.error("Error fetching invoices:", error);
        } finally {
          setIsLoading(false);
        }
      };
      
      fetchInvoices();
    } else if (storedCustomerId) {
      // Legacy customer session (backward compatibility)
      setCustomerId(storedCustomerId);
      setCustomerEmail(storedEmail || "");

      // Fetch invoices using the secure customer portal endpoint
      const fetchInvoices = async () => {
        try {
          const data = await invoiceService.getCustomerInvoices(storedCustomerId);
          // Filter out DRAFT invoices - customers should only see SENT and PAID invoices
          const visibleInvoices = data.content.filter(
            (invoice) => invoice.status === "SENT" || invoice.status === "PAID"
          );
          setInvoices(visibleInvoices);
        } catch (error) {
          console.error("Error fetching invoices:", error);
        } finally {
          setIsLoading(false);
        }
      };

      fetchInvoices();
    }
  }, [router, isAuthenticated, userType, user]);

  const getStatusBadge = (status: string) => {
    switch (status) {
      case "PAID":
        return <Badge className="bg-green-500">Paid</Badge>;
      case "SENT":
        return <Badge className="bg-yellow-500">Pending</Badge>;
      case "DRAFT":
        return <Badge variant="outline">Draft</Badge>;
      default:
        return <Badge variant="outline">{status}</Badge>;
    }
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

  return (
    <ProtectedRoute>
      <MainLayout>
        <div className="space-y-6">
        <div>
          <h1 className="text-3xl font-bold tracking-tight text-slate-100">
            {isAdmin ? "All Invoices" : "My Invoices"}
          </h1>
          <p className="text-slate-300 mt-1">
            {customerEmail && `Logged in as: ${customerEmail}${isAdmin ? " (Admin)" : ""}`}
          </p>
        </div>

        <Card className="bg-[#1e3a5f] border-slate-700">
          <CardHeader>
            <CardTitle className="text-slate-100">Invoice List</CardTitle>
            <CardDescription className="text-slate-300">
              {isAdmin ? "View all customer invoices" : "View and pay your invoices"}
            </CardDescription>
          </CardHeader>
          <CardContent>
            {invoices.length === 0 ? (
              <div className="text-center py-12">
                <FileText className="mx-auto h-12 w-12 text-muted-foreground mb-4" />
                <p className="text-lg font-medium">No invoices found</p>
                <p className="text-sm text-muted-foreground mt-2">
                  You don&apos;t have any invoices yet.
                </p>
              </div>
            ) : (
              <Table>
                <TableHeader>
                  <TableRow>
                    <TableHead>Invoice #</TableHead>
                    {isAdmin && <TableHead>Customer</TableHead>}
                    <TableHead>Issue Date</TableHead>
                    <TableHead>Due Date</TableHead>
                    <TableHead>Total Amount</TableHead>
                    <TableHead>Balance</TableHead>
                    <TableHead>Status</TableHead>
                    <TableHead className="text-right">Actions</TableHead>
                  </TableRow>
                </TableHeader>
                <TableBody>
                  {invoices.map((invoice) => (
                    <TableRow key={invoice.id}>
                      <TableCell className="font-medium text-slate-100">
                        {invoice.id.substring(0, 8)}...
                      </TableCell>
                      {isAdmin && (
                        <TableCell className="text-slate-100">
                          {invoice.customerName || "N/A"}
                        </TableCell>
                      )}
                      <TableCell className="text-slate-100">
                        {format(new Date(invoice.issueDate), "MMM dd, yyyy")}
                      </TableCell>
                      <TableCell className="text-slate-100">
                        {format(new Date(invoice.dueDate), "MMM dd, yyyy")}
                      </TableCell>
                      <TableCell className="text-slate-100">
                        <div className="flex items-center">
                          <DollarSign className="h-4 w-4 mr-1" />
                          {invoice.totalAmount.toFixed(2)}
                        </div>
                      </TableCell>
                      <TableCell className="text-slate-100">
                        <div className="flex items-center">
                          <DollarSign className="h-4 w-4 mr-1" />
                          {invoice.balance.toFixed(2)}
                        </div>
                      </TableCell>
                      <TableCell>{getStatusBadge(invoice.status)}</TableCell>
                      <TableCell className="text-right">
                        <Link 
                          href={`/portal/invoices/${invoice.id}`} 
                          prefetch={true}
                          onMouseEnter={() => prefetchRouteData(queryClient, `/portal/invoices/${invoice.id}`)}
                        >
                          <Button variant="outline" size="sm">
                            View Details
                          </Button>
                        </Link>
                      </TableCell>
                    </TableRow>
                  ))}
                </TableBody>
              </Table>
            )}
          </CardContent>
        </Card>
        </div>
      </MainLayout>
    </ProtectedRoute>
  );
}

