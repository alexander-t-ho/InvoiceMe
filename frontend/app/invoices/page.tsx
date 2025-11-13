"use client";

import { useState } from "react";
import { useQueryClient } from "@tanstack/react-query";
import { ProtectedRoute } from "@/components/auth/protected-route";
import { MainLayout } from "@/components/layout/main-layout";
import { useInvoices, useMarkInvoiceAsSent } from "@/hooks/useInvoices";
import { Button } from "@/components/ui/button";
import { prefetchRouteData } from "@/lib/prefetch-enhanced";
import {
  Table,
  TableBody,
  TableCell,
  TableHead,
  TableHeader,
  TableRow,
} from "@/components/ui/table";
import { InvoiceStatusBadge } from "@/components/invoices/invoice-status-badge";
import { LoadingSpinner } from "@/components/ui/loading-spinner";
import { TableSkeleton } from "@/components/ui/skeleton";
import { Card, CardContent, CardDescription, CardHeader, CardTitle } from "@/components/ui/card";
import Link from "next/link";
import { Plus, Send } from "lucide-react";
import {
  Select,
  SelectContent,
  SelectItem,
  SelectTrigger,
  SelectValue,
} from "@/components/ui/select";
import type { InvoiceStatus } from "@/types/api";
import { useToast } from "@/hooks/use-toast";

// Format date helper
function formatDate(dateString: string): string {
  const date = new Date(dateString);
  return date.toLocaleDateString("en-US", { year: "numeric", month: "short", day: "numeric" });
}

export default function InvoicesPage() {
  const queryClient = useQueryClient();
  const [page, setPage] = useState(0);
  const [statusFilter, setStatusFilter] = useState<InvoiceStatus | undefined>();
  const { toast } = useToast();
  const markAsSent = useMarkInvoiceAsSent();

  const { data, isLoading, error } = useInvoices({
    status: statusFilter,
    page,
    size: 20,
  });

  const handleSendInvoice = async (invoiceId: string) => {
    markAsSent.mutate(invoiceId, {
      onSuccess: () => {
        toast({
          title: "Success",
          description: "Invoice has been sent and is now visible to the customer.",
        });
      },
    });
  };

  // Optimistic rendering - show page structure immediately
  const showFullLoading = isLoading && !data;

  if (error) {
    return (
      <ProtectedRoute>
        <MainLayout>
        <div className="flex items-center justify-center py-12">
          <div className="text-center">
            <p className="text-destructive">Error loading invoices</p>
            <p className="text-sm text-muted-foreground mt-2">
              {error instanceof Error ? error.message : "Unknown error"}
            </p>
          </div>
        </div>
      </MainLayout>
      </ProtectedRoute>
    );
  }

  if (showFullLoading) {
    return (
      <ProtectedRoute>
        <MainLayout>
          <div className="space-y-6">
            <div className="flex items-center justify-between">
              <div>
                <h1 className="text-3xl font-bold tracking-tight">Invoices</h1>
                <p className="text-muted-foreground">
                  Manage your invoices
                </p>
              </div>
            </div>
            <TableSkeleton rows={10} cols={6} />
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
          <div>
            <h1 className="text-3xl font-bold tracking-tight">Invoices</h1>
            <p className="text-muted-foreground">
              Manage your invoices
            </p>
          </div>
          <Link href="/invoices/new" prefetch={true}>
            <Button>
              <Plus className="mr-2 h-4 w-4" />
              New Invoice
            </Button>
          </Link>
        </div>

        {isLoading && (
          <div className="flex items-center justify-center py-4">
            <LoadingSpinner size="sm" />
            <span className="ml-2 text-sm text-muted-foreground">Loading invoices...</span>
          </div>
        )}

        <div className="flex items-center space-x-4">
          <Select
            value={statusFilter || "all"}
            onValueChange={(value) => {
              setStatusFilter(value === "all" ? undefined : (value as InvoiceStatus));
              setPage(0); // Reset to first page when filter changes
            }}
          >
            <SelectTrigger className="w-[180px]">
              <SelectValue placeholder="Filter by status" />
            </SelectTrigger>
            <SelectContent>
              <SelectItem value="all">All Statuses</SelectItem>
              <SelectItem value="DRAFT">Draft</SelectItem>
              <SelectItem value="SENT">Sent</SelectItem>
              <SelectItem value="PAID">Paid</SelectItem>
            </SelectContent>
          </Select>
        </div>

        {data && data.content.length > 0 ? (
          <>
            <Card className="bg-[#1e3a5f] border-slate-700">
              <CardHeader>
                <CardTitle className="text-slate-100">Invoice List</CardTitle>
                <CardDescription className="text-slate-300">
                  Manage your invoices
                </CardDescription>
              </CardHeader>
              <CardContent>
                <Table>
                  <TableHeader>
                    <TableRow>
                      <TableHead className="text-slate-100">Customer</TableHead>
                      <TableHead className="text-slate-100">Status</TableHead>
                      <TableHead className="text-slate-100">Created Date</TableHead>
                      <TableHead className="text-slate-100">Issue Date</TableHead>
                      <TableHead className="text-slate-100">Due Date</TableHead>
                      <TableHead className="text-right text-slate-100">Total</TableHead>
                      <TableHead className="text-right text-slate-100">Balance</TableHead>
                      <TableHead className="text-right text-slate-100">Actions</TableHead>
                    </TableRow>
                  </TableHeader>
                  <TableBody>
                    {data.content.map((invoice) => (
                      <TableRow key={invoice.id}>
                        <TableCell className="font-medium text-slate-100">
                          {invoice.customerName}
                        </TableCell>
                        <TableCell>
                          <InvoiceStatusBadge status={invoice.status} />
                        </TableCell>
                        <TableCell className="text-slate-100">
                          {invoice.createdAt ? formatDate(invoice.createdAt) : "-"}
                        </TableCell>
                        <TableCell className="text-slate-100">
                          {formatDate(invoice.issueDate)}
                        </TableCell>
                        <TableCell className="text-slate-100">
                          {formatDate(invoice.dueDate)}
                        </TableCell>
                        <TableCell className="text-right text-slate-100">
                          ${invoice.totalAmount.toFixed(2)}
                        </TableCell>
                        <TableCell className="text-right text-slate-100">
                          ${invoice.balance.toFixed(2)}
                        </TableCell>
                        <TableCell className="text-right">
                          <div className="flex justify-end gap-2">
                            {invoice.status === "DRAFT" && (
                              <Button
                                variant="default"
                                size="sm"
                                onClick={() => handleSendInvoice(invoice.id)}
                                disabled={markAsSent.isPending}
                              >
                                <Send className="mr-2 h-4 w-4" />
                                Send
                              </Button>
                            )}
                          <Link 
                            href={`/invoices/${invoice.id}`} 
                            prefetch={true}
                            onMouseEnter={() => prefetchRouteData(queryClient, `/invoices/${invoice.id}`)}
                          >
                            <Button variant="outline" size="sm">
                              View
                            </Button>
                          </Link>
                          </div>
                        </TableCell>
                      </TableRow>
                    ))}
                  </TableBody>
                </Table>
              </CardContent>
            </Card>

            <div className="flex items-center justify-between">
              <div className="text-sm text-muted-foreground">
                Showing {data.content.length} of {data.totalElements} invoices
              </div>
              <div className="flex space-x-2">
                <Button
                  variant="outline"
                  size="sm"
                  onClick={() => setPage((p) => Math.max(0, p - 1))}
                  disabled={!data.hasPrevious || page === 0}
                >
                  Previous
                </Button>
                <Button
                  variant="outline"
                  size="sm"
                  onClick={() => setPage((p) => p + 1)}
                  disabled={!data.hasNext}
                >
                  Next
                </Button>
              </div>
            </div>
          </>
        ) : (
          <div className="text-center py-12">
            <p className="text-muted-foreground">No invoices found</p>
            <Link href="/invoices/new" className="mt-4 inline-block">
              <Button>
                <Plus className="mr-2 h-4 w-4" />
                Create Your First Invoice
              </Button>
            </Link>
          </div>
        )}
      </div>
    </MainLayout>
    </ProtectedRoute>
  );
}

