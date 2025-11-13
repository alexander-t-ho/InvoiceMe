"use client";

import { useEffect, useState } from "react";
import { useParams, useRouter } from "next/navigation";
import Link from "next/link";
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
import { ArrowLeft, CreditCard, Printer } from "lucide-react";
import { useToast } from "@/hooks/use-toast";
import type { InvoiceResponse } from "@/types/api";
import { usePaymentSchedule } from "@/hooks/usePaymentSchedules";
import { CreditCardSelector } from "@/components/payments/credit-card-selector";
import { DebitCardSelector } from "@/components/payments/debit-card-selector";
import { BankAccountSelector } from "@/components/payments/bank-account-selector";
import { CheckDraftForm, type CheckDraftFormData } from "@/components/payments/check-draft-form";
import { SmallMeteors } from "@/components/ui/small-meteors";
import { PaymentCountdown } from "@/components/ui/payment-countdown";
import { MainLayout } from "@/components/layout/main-layout";
import { ProtectedRoute } from "@/components/auth/protected-route";

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
  const [selectedDebitCardId, setSelectedDebitCardId] = useState<string | null>(null);
  const [selectedBankAccountId, setSelectedBankAccountId] = useState<string | null>(null);
  const [checkDraftData, setCheckDraftData] = useState<CheckDraftFormData | null>(null);

  // Fetch payment schedule for Pay in 4 invoices
  const { data: paymentSchedule } = usePaymentSchedule(invoiceId);

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
    // Check for admin authentication first
    const storedUser = localStorage.getItem("auth_user");
    let isAdmin = false;
    
    if (storedUser) {
      try {
        const parsedUser = JSON.parse(storedUser);
        if (parsedUser.userType === "ADMIN") {
          isAdmin = true;
          setCustomerEmail(parsedUser.email || parsedUser.username || "Admin");
          
          // Fetch invoice using admin endpoint
          const fetchInvoice = async () => {
            try {
              const data = await invoiceService.getInvoiceById(invoiceId);
              setInvoice(data);
              paymentForm.setValue("amount", data.balance);
            } catch (error: any) {
              toast({
                title: "Error",
                description: error.response?.data?.message || "Failed to load invoice",
                variant: "destructive",
              });
              router.replace("/portal/invoices");
            } finally {
              setIsLoading(false);
            }
          };
          
          fetchInvoice();
          return;
        }
      } catch (error) {
        console.error("Error parsing user data:", error);
      }
    }

    // Check if customer is in session (fallback for customer-only login)
    const storedCustomerId = sessionStorage.getItem("portal_customer_id");
    const storedEmail = sessionStorage.getItem("portal_customer_email");
    
           if (!storedCustomerId) {
             router.replace("/login");
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
      router.replace("/login");
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
    
    // If debit card is selected, validate that a card is chosen
    if (data.paymentMethod === "DEBIT_CARD" && !selectedDebitCardId) {
      toast({
        title: "Error",
        description: "Please select a debit card to continue",
        variant: "destructive",
      });
      return;
    }
    
    // If bank transfer is selected, validate that an account is chosen
    if (data.paymentMethod === "BANK_TRANSFER" && !selectedBankAccountId) {
      toast({
        title: "Error",
        description: "Please select a checking account to continue",
        variant: "destructive",
      });
      return;
    }
    
    // If check is selected, validate that check is drafted
    if (data.paymentMethod === "CHECK" && !checkDraftData) {
      toast({
        title: "Error",
        description: "Please draft the check to continue",
        variant: "destructive",
      });
      return;
    }
    
    recordPayment.mutate(data, {
      onSuccess: () => {
        setPaymentDialogOpen(false);
        setSelectedCreditCardId(null);
        setSelectedDebitCardId(null);
        setSelectedBankAccountId(null);
        setCheckDraftData(null);
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
      <ProtectedRoute>
        <MainLayout>
          <div className="flex items-center justify-center py-12">
            <LoadingSpinner size="lg" />
          </div>
        </MainLayout>
      </ProtectedRoute>
    );
  }

  if (!invoice) {
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

  const canMakePayment = invoice.status === "SENT" && invoice.balance > 0;
  const invoiceNumber = invoice.id.substring(0, 8).toUpperCase();
  const paymentTerms = invoice.paymentPlan === "PAY_IN_4" ? "Pay in 4 installments" : "Due on receipt";

  return (
    <ProtectedRoute>
      <MainLayout>
        <div className="max-w-4xl mx-auto print:max-w-none">
        {/* Invoice Card */}
        <div className="bg-[#1e3a5f] rounded-lg shadow-lg p-8 print:bg-[#0f1e35] print:shadow-none border border-slate-700">
          {/* Header */}
          <div className="flex items-start justify-between mb-8 pb-6 border-b border-slate-600 print:border-slate-600">
            {/* Left: Company Info */}
            <div>
              <div className="text-xl font-bold text-slate-100 print:text-slate-100 mb-1">Your Company Inc.</div>
              <div className="text-sm text-slate-300 print:text-slate-300 space-y-0.5">
                <div>1234 Company St.</div>
                <div>Company Town, ST 12345</div>
              </div>
            </div>
            
            {/* Right: Small Meteors and INVOICE Title */}
            <div className="flex flex-col items-end">
              {/* Small Meteors Box */}
              <div className="border border-slate-500 w-24 h-24 mb-4 flex items-center justify-center bg-[#0f1e35] print:bg-[#0f1e35] relative overflow-hidden">
                <SmallMeteors number={8} />
              </div>
              <div className="text-5xl font-bold text-slate-200 print:text-slate-200">INVOICE</div>
            </div>
          </div>

          {/* Bill To and Invoice Details - Two Column */}
          <div className="grid grid-cols-2 gap-6 mb-8">
            {/* Left Column - Bill To */}
            <div>
              <div className="font-semibold text-slate-200 print:text-slate-200 mb-2">Bill To</div>
              <div className="font-semibold text-slate-100 print:text-slate-100 mb-1">{invoice.customerName}</div>
              <div className="text-sm text-slate-300 print:text-slate-300">
                {customerEmail || "1234 Customer St,"}
              </div>
              <div className="text-sm text-slate-300 print:text-slate-300">Customer Town, ST 12345</div>
            </div>

            {/* Right Column - Invoice Details */}
            <div className="space-y-0">
              <div className="bg-slate-600 text-white px-4 py-2 font-semibold text-sm print:bg-slate-600">
                Invoice #
              </div>
              <div className="bg-[#2a4d75] print:bg-[#1e3a5f] border-x border-b border-slate-600 print:border-slate-600 px-4 py-2 text-slate-100 print:text-slate-100">
                {invoiceNumber}
              </div>
              
              <div className="bg-slate-600 text-white px-4 py-2 font-semibold text-sm print:bg-slate-600">
                Invoice date
              </div>
              <div className="bg-[#2a4d75] print:bg-[#1e3a5f] border-x border-b border-slate-600 print:border-slate-600 px-4 py-2 text-slate-100 print:text-slate-100">
                {formatDate(invoice.issueDate)}
              </div>
              
              <div className="bg-slate-600 text-white px-4 py-2 font-semibold text-sm print:bg-slate-600">
                Due date
              </div>
              <div className="bg-[#2a4d75] print:bg-[#1e3a5f] border-x border-b border-slate-600 print:border-slate-600 px-4 py-2 text-slate-100 print:text-slate-100">
                {formatDate(invoice.dueDate)}
              </div>
            </div>
          </div>

          {/* Payment Countdown */}
          {invoice.status !== "PAID" && (
            <div className="mb-6">
              <PaymentCountdown dueDate={invoice.dueDate} status={invoice.status} />
            </div>
          )}

          {/* Line Items Table */}
          <div className="mb-8">
            <table className="w-full border-collapse">
              <thead>
                <tr className="bg-slate-600 text-white print:bg-slate-600">
                  <th className="text-left px-4 py-3 font-semibold text-sm">QTY</th>
                  <th className="text-left px-4 py-3 font-semibold text-sm">Description</th>
                  <th className="text-left px-4 py-3 font-semibold text-sm">Unit Price</th>
                  <th className="text-left px-4 py-3 font-semibold text-sm">Amount</th>
                </tr>
              </thead>
                  <tbody>
                    {invoice.lineItems.map((item) => (
                      <tr key={item.id} className="border-b border-slate-600 print:border-slate-600">
                        <td className="px-4 py-3 text-slate-100 print:text-slate-100">{item.quantity.toFixed(2)}</td>
                        <td className="px-4 py-3 text-slate-100 print:text-slate-100">{item.description}</td>
                        <td className="px-4 py-3 text-slate-100 print:text-slate-100">${item.unitPrice.toFixed(2)}</td>
                        <td className="px-4 py-3 text-slate-100 print:text-slate-100">${item.total.toFixed(2)}</td>
                      </tr>
                    ))}
                  </tbody>
            </table>
          </div>

          {/* Notes and Summary - Two Column */}
          <div className="grid grid-cols-2 gap-6 mb-8">
            {/* Left Column - Notes */}
            <div>
              <div className="font-semibold text-slate-200 print:text-slate-200 mb-2">Note to recipient(s)</div>
              <div className="text-slate-300 print:text-slate-300">Thanks for your business</div>
            </div>

            {/* Right Column - Summary */}
            <div className="space-y-0">
              <div className="flex justify-between px-4 py-2 text-slate-100 print:text-slate-100 border-b border-slate-600 print:border-slate-600">
                <span>Subtotal:</span>
                <span>${invoice.subtotal.toFixed(2)}</span>
              </div>
              {invoice.discountAmount > 0 && (
                <div className="flex justify-between px-4 py-2 text-slate-100 print:text-slate-100 border-b border-slate-600 print:border-slate-600">
                  <span>Discount {invoice.discountCode ? `(${invoice.discountCode})` : ""}:</span>
                  <span className="text-green-400 print:text-green-400">-${invoice.discountAmount.toFixed(2)}</span>
                </div>
              )}
              {/* Sales Tax - 5% for now, can be made configurable later */}
              <div className="flex justify-between px-4 py-2 text-slate-100 print:text-slate-100 border-b border-slate-600 print:border-slate-600">
                <span>Sales Tax (5%):</span>
                <span>${(invoice.totalAmount * 0.05).toFixed(2)}</span>
              </div>
              <div className="bg-slate-600 text-white px-4 py-3 font-bold flex justify-between print:bg-slate-600">
                <span>Total (USD)</span>
                <span>${(invoice.totalAmount * 1.05).toFixed(2)}</span>
              </div>
              {invoice.balance < invoice.totalAmount && (
                <div className="flex justify-between px-4 py-2 text-sm text-slate-300 print:text-slate-300 border-t border-slate-600 print:border-slate-600">
                  <span>Total Paid:</span>
                  <span>${(invoice.totalAmount - invoice.balance).toFixed(2)}</span>
                </div>
              )}
              {invoice.balance > 0 && (
                <div className="flex justify-between px-4 py-2 text-sm font-semibold text-slate-100 print:text-slate-100 border-t border-slate-600 print:border-slate-600">
                  <span>Balance Due:</span>
                  <span className="text-red-400 print:text-red-400">${(invoice.balance * 1.05).toFixed(2)}</span>
                </div>
              )}
            </div>
          </div>

          {/* Terms and Conditions */}
          <div className="mt-8 pt-6 border-t border-slate-600 print:border-slate-600">
            <div className="font-semibold text-slate-200 print:text-slate-200 mb-2">Terms and Conditions</div>
            <div className="text-sm text-slate-300 print:text-slate-300 space-y-1">
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
                  <DialogContent className={(paymentForm.watch("paymentMethod") === "CREDIT_CARD" || paymentForm.watch("paymentMethod") === "DEBIT_CARD" || paymentForm.watch("paymentMethod") === "BANK_TRANSFER" || paymentForm.watch("paymentMethod") === "CHECK") ? "max-w-2xl" : ""}>
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
                            if (value !== "DEBIT_CARD") {
                              setSelectedDebitCardId(null);
                            }
                            if (value !== "BANK_TRANSFER") {
                              setSelectedBankAccountId(null);
                            }
                            if (value !== "CHECK") {
                              setCheckDraftData(null);
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
                      
                      {/* Debit Card Selection - Only show when Debit Card is selected */}
                      {paymentForm.watch("paymentMethod") === "DEBIT_CARD" && invoice && (
                        <div className="space-y-2 border-t pt-4">
                          <DebitCardSelector
                            selectedCardId={selectedDebitCardId}
                            onSelectCard={setSelectedDebitCardId}
                            userName={invoice.customerName}
                          />
                          {!selectedDebitCardId && (
                            <p className="text-sm text-destructive">
                              Please select a debit card to continue
                            </p>
                          )}
                        </div>
                      )}
                      
                      {/* Bank Account Selection - Only show when Bank Transfer is selected */}
                      {paymentForm.watch("paymentMethod") === "BANK_TRANSFER" && invoice && (
                        <div className="space-y-2 border-t pt-4">
                          <BankAccountSelector
                            selectedAccountId={selectedBankAccountId}
                            onSelectAccount={setSelectedBankAccountId}
                            userName={invoice.customerName}
                          />
                          {!selectedBankAccountId && (
                            <p className="text-sm text-destructive">
                              Please select a checking account to continue
                            </p>
                          )}
                        </div>
                      )}
                      
                      {/* Check Draft Form - Only show when Check is selected */}
                      {paymentForm.watch("paymentMethod") === "CHECK" && invoice && (
                        <CheckDraftForm
                          amount={paymentForm.watch("amount") || 0}
                          payeeName={invoice.customerName}
                          onDraftComplete={setCheckDraftData}
                          isDrafted={!!checkDraftData}
                          draftedData={checkDraftData}
                        />
                      )}
                      
                      <DialogFooter>
                        <Button
                          type="button"
                          variant="outline"
                          onClick={() => {
                            setPaymentDialogOpen(false);
                            setSelectedCreditCardId(null);
                            setSelectedDebitCardId(null);
                            setSelectedBankAccountId(null);
                            setCheckDraftData(null);
                          }}
                        >
                          Cancel
                        </Button>
                        <Button 
                          type="submit" 
                          disabled={
                            recordPayment.isPending || 
                            (paymentForm.watch("paymentMethod") === "CREDIT_CARD" && !selectedCreditCardId) ||
                            (paymentForm.watch("paymentMethod") === "DEBIT_CARD" && !selectedDebitCardId) ||
                            (paymentForm.watch("paymentMethod") === "BANK_TRANSFER" && !selectedBankAccountId) ||
                            (paymentForm.watch("paymentMethod") === "CHECK" && !checkDraftData)
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

          {/* Payment Schedule - Only show for Pay in 4 invoices */}
          {invoice.paymentPlan === "PAY_IN_4" && paymentSchedule && paymentSchedule.length > 0 && (
            <div className="mt-8 pt-6 border-t border-slate-600 print:border-slate-600">
              <h3 className="font-semibold text-slate-100 print:text-slate-100 mb-2">Payment Schedule</h3>
              <p className="text-sm text-slate-300 print:text-slate-300 mb-4">
                This invoice is set up for Pay in 4 installments. See the schedule below:
              </p>
              <table className="w-full border-collapse">
                <thead>
                  <tr className="bg-slate-700 print:bg-slate-600">
                    <th className="text-left px-4 py-2 font-semibold text-sm text-slate-100 print:text-slate-100">Installment</th>
                    <th className="text-left px-4 py-2 font-semibold text-sm text-slate-100 print:text-slate-100">Due Date</th>
                    <th className="text-right px-4 py-2 font-semibold text-sm text-slate-100 print:text-slate-100">Amount</th>
                    <th className="text-right px-4 py-2 font-semibold text-sm text-slate-100 print:text-slate-100">Status</th>
                  </tr>
                </thead>
                <tbody>
                  {paymentSchedule.map((installment) => (
                    <tr key={installment.id} className="border-b border-slate-600 print:border-slate-600">
                      <td className="px-4 py-2 text-slate-100 print:text-slate-100">#{installment.installmentNumber}</td>
                      <td className="px-4 py-2 text-slate-100 print:text-slate-100">{formatDate(installment.dueDate)}</td>
                      <td className="px-4 py-2 text-right text-slate-100 print:text-slate-100 font-medium">
                        ${installment.amount.toFixed(2)}
                      </td>
                      <td className="px-4 py-2 text-right text-slate-100 print:text-slate-100">
                        <span
                          className={`px-2 py-1 rounded text-xs font-medium ${
                            installment.status === "PAID"
                              ? "bg-green-500/20 text-green-300 border border-green-500/50"
                              : installment.status === "OVERDUE"
                              ? "bg-red-500/20 text-red-300 border border-red-500/50"
                              : "bg-slate-600 text-slate-200 border border-slate-500"
                          }`}
                        >
                          {installment.status}
                        </span>
                      </td>
                    </tr>
                  ))}
                </tbody>
              </table>
            </div>
          )}

          {/* Payment History */}
          {invoice.payments.length > 0 && (
            <div className="mt-8 pt-6 border-t border-slate-600 print:border-slate-600">
              <h3 className="font-semibold text-slate-100 print:text-slate-100 mb-4">Payment History</h3>
              <table className="w-full border-collapse">
                    <thead>
                      <tr className="bg-slate-700 print:bg-slate-600">
                        <th className="text-left px-4 py-2 font-semibold text-sm text-slate-100 print:text-slate-100">Date</th>
                        <th className="text-left px-4 py-2 font-semibold text-sm text-slate-100 print:text-slate-100">Method</th>
                        <th className="text-right px-4 py-2 font-semibold text-sm text-slate-100 print:text-slate-100">Amount</th>
                      </tr>
                    </thead>
                    <tbody>
                      {invoice.payments.map((payment) => (
                        <tr key={payment.id} className="border-b border-slate-600 print:border-slate-600">
                          <td className="px-4 py-2 text-slate-100 print:text-slate-100">{formatDate(payment.paymentDate)}</td>
                          <td className="px-4 py-2 text-slate-100 print:text-slate-100">{payment.paymentMethod}</td>
                          <td className="px-4 py-2 text-right text-slate-100 print:text-slate-100 font-medium">
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
      </MainLayout>
    </ProtectedRoute>
  );
}

