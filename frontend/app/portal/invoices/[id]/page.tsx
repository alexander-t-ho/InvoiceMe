"use client";

import { useEffect, useState } from "react";
import { useParams, useRouter } from "next/navigation";
import { useForm } from "react-hook-form";
import { zodResolver } from "@hookform/resolvers/zod";
import { Button } from "@/components/ui/button";
import { LoadingSpinner } from "@/components/ui/loading-spinner";
import {
  Dialog,
  DialogContent,
  DialogDescription,
  DialogFooter,
  DialogHeader,
  DialogTitle,
  DialogTrigger,
} from "@/components/ui/dialog";
import { Input } from "@/components/ui/input";
import { Label } from "@/components/ui/label";
import {
  Select,
  SelectContent,
  SelectItem,
  SelectTrigger,
  SelectValue,
} from "@/components/ui/select";
import { invoiceService } from "@/lib/services/InvoiceService";
import { useRecordPortalPayment } from "@/hooks/usePortalPayments";
import { recordPaymentSchema, type RecordPaymentFormData } from "@/lib/validations/payment";
import { ArrowLeft, CreditCard, Printer, Cloud } from "lucide-react";
import { useToast } from "@/hooks/use-toast";
import type { InvoiceResponse } from "@/types/api";
import { CreditCardSelector } from "@/components/payments/credit-card-selector";
import { Meteors } from "@/components/ui/meteors";

function formatDate(dateString: string): string {
  const date = new Date(dateString);
  // Format as MM-DD-YYYY for invoice display
  const month = String(date.getMonth() + 1).padStart(2, '0');
  const day = String(date.getDate()).padStart(2, '0');
  const year = date.getFullYear();
  return `${month}-${day}-${year}`;
}

export default function CustomerInvoiceDetailPage() {
  const params = useParams();
  const router = useRouter();
  const { toast } = useToast();
  const invoiceId = params.id as string;

  const [invoice, setInvoice] = useState<InvoiceResponse | null>(null);
  const [isLoading, setIsLoading] = useState(true);
  const [paymentDialogOpen, setPaymentDialogOpen] = useState(false);
  const [customerEmail, setCustomerEmail] = useState<string>("");
  const [customerId, setCustomerId] = useState<string | null>(null);
  const [selectedCreditCardId, setSelectedCreditCardId] = useState<string | null>(null);

  const paymentForm = useForm<RecordPaymentFormData>({
    resolver: zodResolver(recordPaymentSchema),
    defaultValues: {
      invoiceId: invoiceId,
      amount: 0,
      paymentDate: new Date().toISOString().split("T")[0],
      paymentMethod: "BANK_TRANSFER",
    },
  });

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

    // Fetch invoice using the secure customer portal endpoint
    // This ensures customers can only access their own invoices
    const fetchInvoice = async () => {
      try {
        const data = await invoiceService.getCustomerInvoiceById(invoiceId, storedCustomerId);
        setInvoice(data);
        
        // Note: Customer details are not needed here as the invoice already contains customer name
        // If needed in the future, we can add a public customer endpoint or use the authenticated customer data

        // Set default payment amount to remaining balance
        paymentForm.setValue("amount", data.balance);
      } catch (error: any) {
        toast({
          title: "Error",
          description: error.response?.data?.message || "Failed to load invoice",
          variant: "destructive",
        });
        router.push("/portal/invoices");
      } finally {
        setIsLoading(false);
      }
    };

    fetchInvoice();
  }, [invoiceId, router, toast, paymentForm]);

  const recordPayment = useRecordPortalPayment(
    invoiceId, 
    customerId || "" // Will be set from sessionStorage in useEffect
  );

  const handlePayment = (data: RecordPaymentFormData) => {
    if (!customerId) {
      toast({
        title: "Error",
        description: "Customer ID is required. Please log in again.",
        variant: "destructive",
      });
      router.push("/portal");
      return;
    }
    
    // If credit card is selected, validate that a card is chosen
    if (data.paymentMethod === "CREDIT_CARD" && !selectedCreditCardId) {
      toast({
        title: "Error",
        description: "Please select a credit card to continue",
        variant: "destructive",
      });
      return;
    }
    
    recordPayment.mutate(data, {
      onSuccess: () => {
        setPaymentDialogOpen(false);
        setSelectedCreditCardId(null);
        // Refresh invoice data using the customer portal endpoint
        invoiceService.getCustomerInvoiceById(invoiceId, customerId).then(setInvoice);
      },
    });
  };

  const handlePrint = () => {
    window.print();
  };

  if (isLoading) {
    return (
      <div className="min-h-screen flex items-center justify-center">
        <LoadingSpinner size="lg" />
      </div>
    );
  }

  if (!invoice) {
    return null;
  }

  const canMakePayment = invoice.status === "SENT" && invoice.balance > 0;
  const invoiceNumber = invoice.id.substring(0, 8).toUpperCase();
  const paymentTerms = invoice.paymentPlan === "PAY_IN_4" ? "Pay in 4 installments" : "Due on receipt";

  return (
    <div className="min-h-screen bg-[#0f1e35] p-4 print:bg-white print:p-0 relative">
      <div className="max-w-4xl mx-auto relative z-10">
        {/* Back button - hidden on print */}
        <div className="mb-4 print:hidden">
          <Button
            variant="outline"
            onClick={() => router.push("/portal/invoices")}
          >
            <ArrowLeft className="mr-2 h-4 w-4" />
            Back to Invoices
          </Button>
        </div>

        {/* Invoice Card */}
        <div className="bg-[#1e3a5f] rounded-lg shadow-lg p-8 print:bg-white print:shadow-none border border-slate-700">
          {/* Header */}
          <div className="flex items-start justify-between mb-8 pb-6 border-b border-slate-600 print:border-gray-200">
            {/* Left: Company Info */}
            <div>
              <div className="text-xl font-bold text-slate-100 print:text-gray-900 mb-1">Your Company Inc.</div>
              <div className="text-sm text-slate-300 print:text-gray-700 space-y-0.5">
                <div>1234 Company St.</div>
                <div>Company Town, ST 12345</div>
              </div>
            </div>
            
            {/* Right: Cloud Icon and INVOICE Title */}
            <div className="flex flex-col items-end">
              {/* Cloud Icon Box */}
              <div className="border border-amber-700 w-24 h-24 mb-4 flex items-center justify-center bg-amber-50 print:bg-amber-50">
                <Cloud className="h-8 w-8 text-amber-700" />
              </div>
              <div className="text-5xl font-bold text-amber-700">INVOICE</div>
            </div>
          </div>

          {/* Bill To and Invoice Details - Two Column */}
          <div className="grid grid-cols-2 gap-6 mb-8">
            {/* Left Column - Bill To */}
            <div>
              <div className="font-semibold text-slate-200 print:text-gray-800 mb-2">Bill To</div>
              <div className="font-semibold text-slate-100 print:text-gray-900 mb-1">{invoice.customerName}</div>
              <div className="text-sm text-slate-300 print:text-gray-700">
                {customerEmail || "1234 Customer St,"}
              </div>
              <div className="text-sm text-slate-300 print:text-gray-700">Customer Town, ST 12345</div>
            </div>

            {/* Right Column - Invoice Details */}
            <div className="space-y-0">
              <div className="bg-amber-700 text-white px-4 py-2 font-semibold text-sm">
                Invoice #
              </div>
              <div className="bg-[#2a4d75] print:bg-white border-x border-b border-slate-600 print:border-gray-200 px-4 py-2 text-slate-100 print:text-gray-800">
                {invoiceNumber}
              </div>
              
              <div className="bg-amber-700 text-white px-4 py-2 font-semibold text-sm">
                Invoice date
              </div>
              <div className="bg-[#2a4d75] print:bg-white border-x border-b border-slate-600 print:border-gray-200 px-4 py-2 text-slate-100 print:text-gray-800">
                {formatDate(invoice.issueDate)}
              </div>
              
              <div className="bg-amber-700 text-white px-4 py-2 font-semibold text-sm">
                Due date
              </div>
              <div className="bg-[#2a4d75] print:bg-white border-x border-b border-slate-600 print:border-gray-200 px-4 py-2 text-slate-100 print:text-gray-800">
                {formatDate(invoice.dueDate)}
              </div>
            </div>
          </div>

          {/* Line Items Table */}
          <div className="mb-8">
            <table className="w-full border-collapse">
              <thead>
                <tr className="bg-amber-700 text-white">
                  <th className="text-left px-4 py-3 font-semibold text-sm">QTY</th>
                  <th className="text-left px-4 py-3 font-semibold text-sm">Description</th>
                  <th className="text-left px-4 py-3 font-semibold text-sm">Unit Price</th>
                  <th className="text-left px-4 py-3 font-semibold text-sm">Amount</th>
                </tr>
              </thead>
                  <tbody>
                    {invoice.lineItems.map((item) => (
                      <tr key={item.id} className="border-b border-slate-600 print:border-gray-200">
                        <td className="px-4 py-3 text-slate-100 print:text-gray-800">{item.quantity.toFixed(2)}</td>
                        <td className="px-4 py-3 text-slate-100 print:text-gray-800">{item.description}</td>
                        <td className="px-4 py-3 text-slate-100 print:text-gray-800">${item.unitPrice.toFixed(2)}</td>
                        <td className="px-4 py-3 text-slate-100 print:text-gray-800">${item.total.toFixed(2)}</td>
                      </tr>
                    ))}
                  </tbody>
            </table>
          </div>

          {/* Notes and Summary - Two Column */}
          <div className="grid grid-cols-2 gap-6 mb-8">
            {/* Left Column - Notes */}
            <div>
              <div className="font-semibold text-slate-200 print:text-gray-800 mb-2">Note to recipient(s)</div>
              <div className="text-slate-300 print:text-gray-700">Thanks for your business</div>
            </div>

            {/* Right Column - Summary */}
            <div className="space-y-0">
              <div className="flex justify-between px-4 py-2 text-slate-100 print:text-gray-800 border-b border-slate-600 print:border-gray-200">
                <span>Subtotal:</span>
                <span>${invoice.subtotal.toFixed(2)}</span>
              </div>
              {invoice.discountAmount > 0 && (
                <div className="flex justify-between px-4 py-2 text-slate-100 print:text-gray-800 border-b border-slate-600 print:border-gray-200">
                  <span>Discount {invoice.discountCode ? `(${invoice.discountCode})` : ""}:</span>
                  <span className="text-green-400 print:text-green-600">-${invoice.discountAmount.toFixed(2)}</span>
                </div>
              )}
              {/* Sales Tax - 5% for now, can be made configurable later */}
              <div className="flex justify-between px-4 py-2 text-slate-100 print:text-gray-800 border-b border-slate-600 print:border-gray-200">
                <span>Sales Tax (5%):</span>
                <span>${(invoice.totalAmount * 0.05).toFixed(2)}</span>
              </div>
              <div className="bg-amber-700 text-white px-4 py-3 font-bold flex justify-between">
                <span>Total (USD)</span>
                <span>${(invoice.totalAmount * 1.05).toFixed(2)}</span>
              </div>
              {invoice.balance < invoice.totalAmount && (
                <div className="flex justify-between px-4 py-2 text-sm text-slate-300 print:text-gray-600 border-t border-slate-600 print:border-gray-200">
                  <span>Total Paid:</span>
                  <span>${(invoice.totalAmount - invoice.balance).toFixed(2)}</span>
                </div>
              )}
              {invoice.balance > 0 && (
                <div className="flex justify-between px-4 py-2 text-sm font-semibold text-slate-100 print:text-gray-800 border-t border-slate-600 print:border-gray-200">
                  <span>Balance Due:</span>
                  <span className="text-red-400 print:text-red-600">${(invoice.balance * 1.05).toFixed(2)}</span>
                </div>
              )}
            </div>
          </div>

          {/* Terms and Conditions */}
          <div className="mt-8 pt-6 border-t border-slate-600 print:border-gray-200">
            <div className="font-semibold text-amber-700 mb-2">Terms and Conditions</div>
            <div className="text-sm text-slate-300 print:text-gray-700 space-y-1">
              <div>Payment is due in 14 days</div>
              <div>Please make checks payable to: Your Company Inc.</div>
            </div>
          </div>

          {/* Action Buttons */}
          <div className="flex gap-4 print:hidden">
            {canMakePayment && (
              <>
                <Dialog open={paymentDialogOpen} onOpenChange={setPaymentDialogOpen}>
                  <DialogTrigger asChild>
                    <Button className="bg-amber-700 hover:bg-amber-800 text-white px-6 py-2 rounded">
                      Pay Invoice
                    </Button>
                  </DialogTrigger>
                  <DialogContent className={paymentForm.watch("paymentMethod") === "CREDIT_CARD" ? "max-w-2xl" : ""}>
                    <DialogHeader>
                      <DialogTitle>Record Payment</DialogTitle>
                      <DialogDescription>
                        Record a payment for this invoice. The maximum amount is the remaining balance.
                      </DialogDescription>
                    </DialogHeader>
                    <form onSubmit={paymentForm.handleSubmit(handlePayment)} className="space-y-4">
                      <div className="space-y-2">
                        <Label htmlFor="amount">Amount *</Label>
                        <Input
                          id="amount"
                          type="number"
                          step="0.01"
                          {...paymentForm.register("amount", { valueAsNumber: true })}
                          max={invoice.balance}
                        />
                        {paymentForm.formState.errors.amount && (
                          <p className="text-sm text-destructive">
                            {paymentForm.formState.errors.amount.message}
                          </p>
                        )}
                        <p className="text-xs text-muted-foreground">
                          Remaining balance: ${invoice.balance.toFixed(2)}
                        </p>
                      </div>
                      <div className="space-y-2">
                        <Label htmlFor="paymentDate">Payment Date *</Label>
                        <Input
                          id="paymentDate"
                          type="date"
                          {...paymentForm.register("paymentDate")}
                        />
                        {paymentForm.formState.errors.paymentDate && (
                          <p className="text-sm text-destructive">
                            {paymentForm.formState.errors.paymentDate.message}
                          </p>
                        )}
                      </div>
                      <div className="space-y-2">
                        <Label htmlFor="paymentMethod">Payment Method *</Label>
                        <Select
                          value={paymentForm.watch("paymentMethod")}
                          onValueChange={(value) => {
                            paymentForm.setValue("paymentMethod", value);
                            if (value !== "CREDIT_CARD") {
                              setSelectedCreditCardId(null);
                            }
                          }}
                        >
                          <SelectTrigger>
                            <SelectValue />
                          </SelectTrigger>
                          <SelectContent>
                            <SelectItem value="BANK_TRANSFER">Bank Transfer</SelectItem>
                            <SelectItem value="CREDIT_CARD">Credit Card</SelectItem>
                            <SelectItem value="DEBIT_CARD">Debit Card</SelectItem>
                            <SelectItem value="CASH">Cash</SelectItem>
                            <SelectItem value="CHECK">Check</SelectItem>
                            <SelectItem value="OTHER">Other</SelectItem>
                          </SelectContent>
                        </Select>
                        {paymentForm.formState.errors.paymentMethod && (
                          <p className="text-sm text-destructive">
                            {paymentForm.formState.errors.paymentMethod.message}
                          </p>
                        )}
                      </div>
                      
                      {/* Credit Card Selection - Only show when Credit Card is selected */}
                      {paymentForm.watch("paymentMethod") === "CREDIT_CARD" && invoice && (
                        <div className="space-y-2 border-t pt-4">
                          <CreditCardSelector
                            selectedCardId={selectedCreditCardId}
                            onSelectCard={setSelectedCreditCardId}
                            userName={invoice.customerName}
                          />
                          {!selectedCreditCardId && (
                            <p className="text-sm text-destructive">
                              Please select a credit card to continue
                            </p>
                          )}
                        </div>
                      )}
                      
                      <DialogFooter>
                        <Button
                          type="button"
                          variant="outline"
                          onClick={() => {
                            setPaymentDialogOpen(false);
                            setSelectedCreditCardId(null);
                          }}
                        >
                          Cancel
                        </Button>
                        <Button 
                          type="submit" 
                          disabled={
                            recordPayment.isPending || 
                            (paymentForm.watch("paymentMethod") === "CREDIT_CARD" && !selectedCreditCardId)
                          }
                        >
                          {recordPayment.isPending ? (
                            <>
                              <LoadingSpinner size="sm" className="mr-2" />
                              Processing...
                            </>
                          ) : (
                            "Record Payment"
                          )}
                        </Button>
                      </DialogFooter>
                    </form>
                  </DialogContent>
                </Dialog>
              </>
            )}
            <Button
              variant="outline"
              onClick={handlePrint}
              className="bg-gray-100 hover:bg-gray-200 text-gray-800 px-6 py-2 rounded"
            >
              <Printer className="mr-2 h-4 w-4" />
              Print
            </Button>
          </div>

          {/* Payment History */}
          {invoice.payments.length > 0 && (
            <div className="mt-8 pt-6 border-t border-slate-600 print:border-gray-200">
              <h3 className="font-semibold text-slate-100 print:text-gray-800 mb-4">Payment History</h3>
              <table className="w-full border-collapse">
                    <thead>
                      <tr className="bg-slate-700 print:bg-gray-100">
                        <th className="text-left px-4 py-2 font-semibold text-sm text-slate-100 print:text-gray-700">Date</th>
                        <th className="text-left px-4 py-2 font-semibold text-sm text-slate-100 print:text-gray-700">Method</th>
                        <th className="text-right px-4 py-2 font-semibold text-sm text-slate-100 print:text-gray-700">Amount</th>
                      </tr>
                    </thead>
                    <tbody>
                      {invoice.payments.map((payment) => (
                        <tr key={payment.id} className="border-b border-slate-600 print:border-gray-200">
                          <td className="px-4 py-2 text-slate-100 print:text-gray-800">{formatDate(payment.paymentDate)}</td>
                          <td className="px-4 py-2 text-slate-100 print:text-gray-800">{payment.paymentMethod}</td>
                          <td className="px-4 py-2 text-right text-slate-100 print:text-gray-800 font-medium">
                            ${payment.amount.toFixed(2)}
                          </td>
                        </tr>
                      ))}
                    </tbody>
              </table>
            </div>
          )}
        </div>
      </div>
    </div>
  );
}

