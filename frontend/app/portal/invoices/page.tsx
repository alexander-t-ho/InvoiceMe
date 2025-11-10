"use client";

import { useEffect, useState } from "react";
import { useRouter } from "next/navigation";
import Link from "next/link";
import { Card, CardContent, CardDescription, CardHeader, CardTitle } from "@/components/ui/card";
import { Button } from "@/components/ui/button";
import { LoadingSpinner } from "@/components/ui/loading-spinner";
import { Badge } from "@/components/ui/badge";
import {
  Table,
  TableBody,
  TableCell,
  TableHead,
  TableHeader,
  TableRow,
} from "@/components/ui/table";
import { invoiceService } from "@/lib/services/InvoiceService";
import type { InvoiceSummaryResponse } from "@/types/api";
import { format } from "date-fns";
import { ArrowLeft, FileText, DollarSign } from "lucide-react";

export default function CustomerInvoicesPage() {
  const router = useRouter();
  const [invoices, setInvoices] = useState<InvoiceSummaryResponse[]>([]);
  const [isLoading, setIsLoading] = useState(true);
  const [customerEmail, setCustomerEmail] = useState<string>("");
  const [customerId, setCustomerId] = useState<string | null>(null);

  useEffect(() => {
    // Check if customer is in session
    const storedCustomerId = sessionStorage.getItem("portal_customer_id");
    const storedEmail = sessionStorage.getItem("portal_customer_email");

    if (!storedCustomerId) {
      router.push("/portal");
      return;
    }

    setCustomerId(storedCustomerId);
    setCustomerEmail(storedEmail || "");

    // Fetch invoices using the secure customer portal endpoint
    // This ensures customers can only see their own invoices
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
  }, [router]);

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
      <div className="min-h-screen flex items-center justify-center">
        <LoadingSpinner size="lg" />
      </div>
    );
  }

  return (
    <div className="min-h-screen bg-[#0f1e35] p-4 relative">
      <div className="max-w-6xl mx-auto space-y-6 relative z-10">
        <div className="flex items-center justify-between">
          <div>
            <h1 className="text-3xl font-bold tracking-tight text-slate-100">My Invoices</h1>
            <p className="text-slate-300 mt-1">
              {customerEmail && `Logged in as: ${customerEmail}`}
            </p>
          </div>
          <Button
            variant="outline"
            onClick={() => {
              sessionStorage.removeItem("portal_customer_id");
              sessionStorage.removeItem("portal_customer_email");
              router.push("/portal");
            }}
          >
            <ArrowLeft className="mr-2 h-4 w-4" />
            Sign Out
          </Button>
        </div>

        <Card className="bg-[#1e3a5f] border-slate-700">
          <CardHeader>
            <CardTitle className="text-slate-100">Invoice List</CardTitle>
            <CardDescription className="text-slate-300">
              View and pay your invoices
            </CardDescription>
          </CardHeader>
          <CardContent>
            {invoices.length === 0 ? (
              <div className="text-center py-12">
                <FileText className="mx-auto h-12 w-12 text-muted-foreground mb-4" />
                <p className="text-lg font-medium">No invoices found</p>
                <p className="text-sm text-muted-foreground mt-2">
                  You don't have any invoices yet.
                </p>
              </div>
            ) : (
              <Table>
                <TableHeader>
                  <TableRow>
                    <TableHead>Invoice #</TableHead>
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
                      <TableCell className="font-medium">
                        {invoice.id.substring(0, 8)}...
                      </TableCell>
                      <TableCell>
                        {format(new Date(invoice.issueDate), "MMM dd, yyyy")}
                      </TableCell>
                      <TableCell>
                        {format(new Date(invoice.dueDate), "MMM dd, yyyy")}
                      </TableCell>
                      <TableCell>
                        <div className="flex items-center">
                          <DollarSign className="h-4 w-4 mr-1" />
                          {invoice.totalAmount.toFixed(2)}
                        </div>
                      </TableCell>
                      <TableCell>
                        <div className="flex items-center">
                          <DollarSign className="h-4 w-4 mr-1" />
                          {invoice.balance.toFixed(2)}
                        </div>
                      </TableCell>
                      <TableCell>{getStatusBadge(invoice.status)}</TableCell>
                      <TableCell className="text-right">
                        <Link href={`/portal/invoices/${invoice.id}`}>
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
    </div>
  );
}

