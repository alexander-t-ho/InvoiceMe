"use client";

import { useParams, useRouter } from "next/navigation";
import { MainLayout } from "@/components/layout/main-layout";
import { useInvoice, useMarkInvoiceAsSent, useAddLineItem, useRemoveLineItem } from "@/hooks/useInvoices";
import { usePaymentsByInvoice, useRecordPayment } from "@/hooks/usePayments";
import { useApplyDiscount, useRemoveDiscount, useValidateDiscountCode } from "@/hooks/useDiscounts";
import { usePaymentSchedule } from "@/hooks/usePaymentSchedules";
import { LoadingSpinner } from "@/components/ui/loading-spinner";
import { useAuth } from "@/contexts/AuthContext";
import { Card, CardContent, CardDescription, CardHeader, CardTitle } from "@/components/ui/card";
import { Button } from "@/components/ui/button";
import { InvoiceStatusBadge } from "@/components/invoices/invoice-status-badge";
import {
  Table,
  TableBody,
  TableCell,
  TableHead,
  TableHeader,
  TableRow,
} from "@/components/ui/table";
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
import { useForm } from "react-hook-form";
import { zodResolver } from "@hookform/resolvers/zod";
import { addLineItemSchema, type AddLineItemFormData } from "@/lib/validations/invoice";
import { recordPaymentSchema, type RecordPaymentFormData } from "@/lib/validations/payment";
import { Trash2, Plus, Send, DollarSign, Library, Printer } from "lucide-react";
import { CreditCardSelector } from "@/components/payments/credit-card-selector";
import { DebitCardSelector } from "@/components/payments/debit-card-selector";
import { BankAccountSelector } from "@/components/payments/bank-account-selector";
import { CheckDraftForm, type CheckDraftFormData } from "@/components/payments/check-draft-form";
import { SmallMeteors } from "@/components/ui/small-meteors";
import { useState, Fragment } from "react";
import { ItemLibraryDialog } from "@/components/items/item-library-dialog";
import { useCreateItem } from "@/hooks/useItems";
import { Checkbox } from "@/components/ui/checkbox";
import type { ItemResponse } from "@/types/api";

function formatDate(dateString: string): string {
  const date = new Date(dateString);
  // Format as MM-DD-YYYY for invoice display
  const month = String(date.getMonth() + 1).padStart(2, '0');
  const day = String(date.getDate()).padStart(2, '0');
  const year = date.getFullYear();
  return `${month}-${day}-${year}`;
}

export default function InvoiceDetailPage() {
  const params = useParams();
  const router = useRouter();
  const invoiceId = params.id as string;

  const [addLineItemOpen, setAddLineItemOpen] = useState(false);
  const [recordPaymentOpen, setRecordPaymentOpen] = useState(false);
  const [itemLibraryOpen, setItemLibraryOpen] = useState(false);
  const [saveToLibrary, setSaveToLibrary] = useState(false);
  const [discountCodeInput, setDiscountCodeInput] = useState("");
  const [discountDialogOpen, setDiscountDialogOpen] = useState(false);
  const [selectedCreditCardId, setSelectedCreditCardId] = useState<string | null>(null);
  const [selectedDebitCardId, setSelectedDebitCardId] = useState<string | null>(null);
  const [selectedBankAccountId, setSelectedBankAccountId] = useState<string | null>(null);
  const [checkDraftData, setCheckDraftData] = useState<CheckDraftFormData | null>(null);
  const createItem = useCreateItem();
  const applyDiscount = useApplyDiscount();
  const removeDiscount = useRemoveDiscount();
  const { data: discountValidation } = useValidateDiscountCode(
    discountCodeInput.trim().length > 0 ? discountCodeInput : null
  );

  const { data: invoice, isLoading, error } = useInvoice(invoiceId);
  const { data: payments } = usePaymentsByInvoice(invoiceId);
  const { data: paymentSchedule } = usePaymentSchedule(invoiceId);
  const { user } = useAuth();
  const markAsSent = useMarkInvoiceAsSent();
  const addLineItem = useAddLineItem();
  const removeLineItem = useRemoveLineItem();
  const recordPayment = useRecordPayment();

  const lineItemForm = useForm<AddLineItemFormData>({
    resolver: zodResolver(addLineItemSchema),
    defaultValues: {
      description: "",
      quantity: 1,
      unitPrice: 0,
    },
  });

  const paymentForm = useForm<RecordPaymentFormData>({
    resolver: zodResolver(recordPaymentSchema),
    defaultValues: {
      invoiceId: invoiceId,
      amount: 0,
      paymentDate: new Date().toISOString().split("T")[0],
      paymentMethod: "BANK_TRANSFER",
    },
  });

  const handleAddLineItem = (data: AddLineItemFormData) => {
    addLineItem.mutate(
      { invoiceId, lineItem: data },
      {
        onSuccess: () => {
          // If saveToLibrary is checked, save the item to library
          if (saveToLibrary && data.description && data.unitPrice) {
            createItem.mutate({
              description: data.description,
              unitPrice: data.unitPrice,
            });
          }
          setAddLineItemOpen(false);
          setSaveToLibrary(false);
          lineItemForm.reset();
        },
      }
    );
  };

  const handleSelectFromLibrary = (item: ItemResponse) => {
    lineItemForm.setValue("description", item.description);
    lineItemForm.setValue("unitPrice", item.unitPrice);
    lineItemForm.setValue("itemId", item.id);
  };

  const handleRecordPayment = (data: RecordPaymentFormData) => {
    // If credit card is selected, validate that a card is chosen
    if (data.paymentMethod === "CREDIT_CARD" && !selectedCreditCardId) {
      return;
    }
    
    // If debit card is selected, validate that a card is chosen
    if (data.paymentMethod === "DEBIT_CARD" && !selectedDebitCardId) {
      return;
    }
    
    // If bank transfer is selected, validate that an account is chosen
    if (data.paymentMethod === "BANK_TRANSFER" && !selectedBankAccountId) {
      return;
    }
    
    // If check is selected, validate that check is drafted
    if (data.paymentMethod === "CHECK" && !checkDraftData) {
      return;
    }
    
    recordPayment.mutate(data, {
      onSuccess: () => {
        setRecordPaymentOpen(false);
        setSelectedCreditCardId(null);
        setSelectedDebitCardId(null);
        setSelectedBankAccountId(null);
        setCheckDraftData(null);
        paymentForm.reset({
          invoiceId: invoiceId,
          amount: 0,
          paymentDate: new Date().toISOString().split("T")[0],
          paymentMethod: "BANK_TRANSFER",
        });
      },
    });
  };

  if (isLoading) {
    return (
      <MainLayout>
        <div className="flex items-center justify-center py-12">
          <LoadingSpinner size="lg" />
        </div>
      </MainLayout>
    );
  }

  if (error || !invoice) {
    return (
      <MainLayout>
        <div className="flex items-center justify-center py-12">
          <div className="text-center">
            <p className="text-destructive">Error loading invoice</p>
            <p className="text-sm text-muted-foreground mt-2">
              {error instanceof Error ? error.message : "Invoice not found"}
            </p>
          </div>
        </div>
      </MainLayout>
    );
  }

  const canEdit = invoice.status === "DRAFT";
  const canSend = canEdit && invoice.lineItems.length > 0;
  const canAddPayment = invoice.status !== "DRAFT";

  return (
    <MainLayout>
      <div className="space-y-6">
        <div className="flex items-center justify-between">
          <div>
            <h1 className="text-3xl font-bold tracking-tight text-slate-100">Invoice Details</h1>
            <p className="text-slate-300">
              Invoice #{invoice.id.slice(0, 8)}
            </p>
          </div>
          <div className="flex space-x-2">
            {canSend && (
              <Button
                onClick={() => markAsSent.mutate(invoiceId)}
                disabled={markAsSent.isPending}
              >
                <Send className="mr-2 h-4 w-4" />
                Mark as Sent
              </Button>
            )}
            {canAddPayment && (
              <Dialog open={recordPaymentOpen} onOpenChange={setRecordPaymentOpen}>
                <DialogTrigger asChild>
                  <Button>
                    <DollarSign className="mr-2 h-4 w-4" />
                    Record Payment
                  </Button>
                </DialogTrigger>
                <DialogContent className={`bg-[#1e3a5f] border-slate-700 ${(paymentForm.watch("paymentMethod") === "CREDIT_CARD" || paymentForm.watch("paymentMethod") === "DEBIT_CARD" || paymentForm.watch("paymentMethod") === "BANK_TRANSFER" || paymentForm.watch("paymentMethod") === "CHECK") ? "max-w-2xl" : ""}`}>
                  <DialogHeader>
                    <DialogTitle className="text-slate-100">Record Payment</DialogTitle>
                    <DialogDescription className="text-slate-300">
                      Record a payment for this invoice
                    </DialogDescription>
                  </DialogHeader>
                  <form onSubmit={paymentForm.handleSubmit(handleRecordPayment)} className="space-y-4">
                    <div className="space-y-2">
                      <Label htmlFor="amount" className="text-slate-100">Amount *</Label>
                      <Input
                        id="amount"
                        type="number"
                        step="0.01"
                        {...paymentForm.register("amount", { valueAsNumber: true })}
                        className="bg-[#0f1e35] border-slate-600 text-slate-100"
                      />
                      {paymentForm.formState.errors.amount && (
                        <p className="text-sm text-red-400">
                          {paymentForm.formState.errors.amount.message}
                        </p>
                      )}
                    </div>
                    <div className="space-y-2">
                      <Label htmlFor="paymentDate" className="text-slate-100">Payment Date *</Label>
                      <Input
                        id="paymentDate"
                        type="date"
                        {...paymentForm.register("paymentDate")}
                        className="bg-[#0f1e35] border-slate-600 text-slate-100"
                      />
                      {paymentForm.formState.errors.paymentDate && (
                        <p className="text-sm text-red-400">
                          {paymentForm.formState.errors.paymentDate.message}
                        </p>
                      )}
                    </div>
                    <div className="space-y-2">
                      <Label htmlFor="paymentMethod" className="text-slate-100">Payment Method *</Label>
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
                        <SelectTrigger className="bg-[#0f1e35] border-slate-600 text-slate-100">
                          <SelectValue />
                        </SelectTrigger>
                        <SelectContent className="bg-[#1e3a5f] border-slate-700 text-slate-100">
                          <SelectItem value="BANK_TRANSFER">Bank Transfer</SelectItem>
                          <SelectItem value="CREDIT_CARD">Credit Card</SelectItem>
                          <SelectItem value="DEBIT_CARD">Debit Card</SelectItem>
                          <SelectItem value="CASH">Cash</SelectItem>
                          <SelectItem value="CHECK">Check</SelectItem>
                          <SelectItem value="OTHER">Other</SelectItem>
                        </SelectContent>
                      </Select>
                      {paymentForm.formState.errors.paymentMethod && (
                        <p className="text-sm text-red-400">
                          {paymentForm.formState.errors.paymentMethod.message}
                        </p>
                      )}
                    </div>
                    
                    {/* Credit Card Selection - Only show when Credit Card is selected */}
                    {paymentForm.watch("paymentMethod") === "CREDIT_CARD" && user && (
                      <div className="space-y-2 border-t pt-4">
                        <CreditCardSelector
                          selectedCardId={selectedCreditCardId}
                          onSelectCard={setSelectedCreditCardId}
                          userName={user.username || user.email || "User"}
                        />
                        {!selectedCreditCardId && (
                          <p className="text-sm text-red-400">
                            Please select a credit card to continue
                          </p>
                        )}
                      </div>
                    )}
                    
                    {/* Debit Card Selection - Only show when Debit Card is selected */}
                    {paymentForm.watch("paymentMethod") === "DEBIT_CARD" && user && (
                      <div className="space-y-2 border-t pt-4">
                        <DebitCardSelector
                          selectedCardId={selectedDebitCardId}
                          onSelectCard={setSelectedDebitCardId}
                          userName={user.username || user.email || "User"}
                        />
                        {!selectedDebitCardId && (
                          <p className="text-sm text-red-400">
                            Please select a debit card to continue
                          </p>
                        )}
                      </div>
                    )}
                    
                    {/* Bank Account Selection - Only show when Bank Transfer is selected */}
                    {paymentForm.watch("paymentMethod") === "BANK_TRANSFER" && user && (
                      <div className="space-y-2 border-t pt-4">
                        <BankAccountSelector
                          selectedAccountId={selectedBankAccountId}
                          onSelectAccount={setSelectedBankAccountId}
                          userName={user.username || user.email || "User"}
                        />
                        {!selectedBankAccountId && (
                          <p className="text-sm text-red-400">
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
                          setRecordPaymentOpen(false);
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
                        {recordPayment.isPending && (
                          <LoadingSpinner size="sm" className="mr-2" />
                        )}
                        Record Payment
                      </Button>
                    </DialogFooter>
                  </form>
                </DialogContent>
              </Dialog>
            )}
          </div>
        </div>

        <div className="grid gap-4 md:grid-cols-2">
          <Card className="bg-[#1e3a5f] border-slate-700">
            <CardHeader>
              <CardTitle className="text-slate-100">Invoice Information</CardTitle>
            </CardHeader>
            <CardContent className="space-y-2">
              <div className="flex justify-between">
                <span className="text-slate-300">Status:</span>
                <InvoiceStatusBadge status={invoice.status} />
              </div>
              <div className="flex justify-between">
                <span className="text-slate-300">Customer:</span>
                <span className="font-medium text-slate-100">{invoice.customerName}</span>
              </div>
              <div className="flex justify-between">
                <span className="text-slate-300">Issue Date:</span>
                <span className="text-slate-100">{formatDate(invoice.issueDate)}</span>
              </div>
              <div className="flex justify-between">
                <span className="text-slate-300">Due Date:</span>
                <span className="text-slate-100">{formatDate(invoice.dueDate)}</span>
              </div>
              <div className="flex justify-between">
                <span className="text-slate-300">Total Amount:</span>
                <span className="font-bold text-slate-100">${invoice.totalAmount.toFixed(2)}</span>
              </div>
              <div className="flex justify-between">
                <span className="text-slate-300">Subtotal:</span>
                <span className="font-medium text-slate-100">${invoice.subtotal.toFixed(2)}</span>
              </div>
              {invoice.discountCode && (
                <>
                  <div className="flex justify-between">
                    <span className="text-slate-300">Discount ({invoice.discountCode}):</span>
                    <span className="font-medium text-green-400">-${invoice.discountAmount.toFixed(2)}</span>
                  </div>
                </>
              )}
              <div className="flex justify-between">
                <span className="text-slate-300">Total Amount:</span>
                <span className="font-bold text-slate-100">${invoice.totalAmount.toFixed(2)}</span>
              </div>
              <div className="flex justify-between">
                <span className="text-slate-300">Balance:</span>
                <span className="font-bold text-slate-100">${invoice.balance.toFixed(2)}</span>
              </div>
            </CardContent>
          </Card>

          <Card className="bg-[#1e3a5f] border-slate-700">
            <CardHeader>
              <CardTitle className="text-slate-100">Summary</CardTitle>
            </CardHeader>
            <CardContent className="space-y-2">
              <div className="flex justify-between">
                <span className="text-slate-300">Line Items:</span>
                <span className="text-slate-100">{invoice.lineItems.length}</span>
              </div>
              <div className="flex justify-between">
                <span className="text-slate-300">Payments:</span>
                <span className="text-slate-100">{payments?.length || 0}</span>
              </div>
              <div className="flex justify-between">
                <span className="text-slate-300">Paid:</span>
                <span className="text-slate-100">${(invoice.totalAmount - invoice.balance).toFixed(2)}</span>
              </div>
            </CardContent>
          </Card>
        </div>

        {/* Printable Invoice View */}
        <Card className="print:shadow-none relative overflow-hidden bg-[#1e3a5f] border-slate-700 print:bg-[#0f1e35]">
          <CardHeader>
            <div className="flex items-center justify-between">
              <CardTitle className="text-slate-100">Invoice Preview</CardTitle>
              <Button
                variant="outline"
                onClick={() => window.print()}
                className="print:hidden"
              >
                <Printer className="mr-2 h-4 w-4" />
                Print Invoice
              </Button>
            </div>
          </CardHeader>
          <CardContent>
            <div className="bg-[#1e3a5f] p-8 print:bg-[#0f1e35] print:p-0 relative z-10 border border-slate-700 print:border-0 rounded-lg">
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
                  <div className="text-sm text-slate-300 print:text-slate-300">1234 Customer St,</div>
                  <div className="text-sm text-slate-300 print:text-slate-300">Customer Town, ST 12345</div>
                </div>

                {/* Right Column - Invoice Details */}
                <div className="space-y-0">
                  <div className="bg-slate-600 text-white px-4 py-2 font-semibold text-sm print:bg-slate-600">
                    Invoice #
                  </div>
                  <div className="bg-[#2a4d75] print:bg-[#1e3a5f] border-x border-b border-slate-600 print:border-slate-600 px-4 py-2 text-slate-100 print:text-slate-100">
                    {invoice.id.substring(0, 8).toUpperCase()}
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
            </div>
          </CardContent>
        </Card>

        <Card className="bg-[#1e3a5f] border-slate-700">
          <CardHeader>
            <div>
              <div className="flex flex-row items-center justify-between mb-4">
            <div>
              <CardTitle className="text-slate-100">Line Items</CardTitle>
              <CardDescription className="text-slate-300">
                Items included in this invoice
              </CardDescription>
            </div>
              </div>
              {canEdit ? (
                <div className="flex flex-col gap-4">
                <div className="flex items-center justify-between mb-4">
                  <div className="flex-1 mr-4">
                    <div className="flex space-x-2">
                      <Input
                        placeholder="Enter discount code (e.g., Save15, FandF)"
                        value={discountCodeInput}
                        onChange={(e) => setDiscountCodeInput(e.target.value.toUpperCase())}
                        className="max-w-xs bg-[#0f1e35] border-slate-600 text-slate-100"
                      />
                      {discountCodeInput.trim().length > 0 && discountValidation && (
                        <div className="flex items-center">
                          {discountValidation.isValid ? (
                            <Button
                              size="sm"
                              onClick={() => {
                                applyDiscount.mutate(
                                  { invoiceId, discountCode: discountCodeInput.trim() },
                                  {
                                    onSuccess: () => {
                                      setDiscountCodeInput("");
                                    },
                                  }
                                );
                              }}
                              disabled={applyDiscount.isPending}
                            >
                              Apply
                            </Button>
                          ) : (
                            <span className="text-sm text-red-400">
                              {discountValidation.message}
                            </span>
                          )}
                        </div>
                      )}
                    </div>
                    {discountValidation?.isValid && (
                      <p className="text-sm text-green-400 mt-1">
                        {discountValidation.discountPercent}% discount available
                      </p>
                    )}
                  </div>
                  {invoice.discountCode && (
                    <Button
                      variant="outline"
                      size="sm"
                      onClick={() => removeDiscount.mutate(invoiceId)}
                      disabled={removeDiscount.isPending}
                    >
                      Remove Discount
                    </Button>
                  )}
                </div>
                <div className="flex space-x-2">
                  <Dialog open={addLineItemOpen} onOpenChange={setAddLineItemOpen}>
                    <DialogTrigger asChild>
                      <Button size="sm">
                        <Plus className="mr-2 h-4 w-4" />
                        Add Item
                      </Button>
                    </DialogTrigger>
                    <DialogContent className="bg-[#1e3a5f] border-slate-700">
                      <DialogHeader>
                        <DialogTitle className="text-slate-100">Add Line Item</DialogTitle>
                        <DialogDescription className="text-slate-300">
                          Add a new line item to this invoice
                        </DialogDescription>
                      </DialogHeader>
                      <form onSubmit={lineItemForm.handleSubmit(handleAddLineItem)} className="space-y-4">
                        <div className="space-y-2">
                          <div className="flex items-center justify-between">
                            <Label htmlFor="description" className="text-slate-100">Description *</Label>
                            <Button
                              type="button"
                              variant="outline"
                              size="sm"
                              onClick={() => setItemLibraryOpen(true)}
                            >
                              <Library className="mr-2 h-4 w-4" />
                              From Library
                            </Button>
                          </div>
                          <Input
                            id="description"
                            {...lineItemForm.register("description")}
                            className="bg-[#0f1e35] border-slate-600 text-slate-100"
                          />
                          {lineItemForm.formState.errors.description && (
                            <p className="text-sm text-red-400">
                              {lineItemForm.formState.errors.description.message}
                            </p>
                          )}
                        </div>
                    <div className="grid grid-cols-2 gap-4">
                      <div className="space-y-2">
                        <Label htmlFor="quantity" className="text-slate-100">Quantity *</Label>
                        <Input
                          id="quantity"
                          type="number"
                          step="0.01"
                          {...lineItemForm.register("quantity", { valueAsNumber: true })}
                          className="bg-[#0f1e35] border-slate-600 text-slate-100"
                        />
                        {lineItemForm.formState.errors.quantity && (
                          <p className="text-sm text-red-400">
                            {lineItemForm.formState.errors.quantity.message}
                          </p>
                        )}
                      </div>
                      <div className="space-y-2">
                        <Label htmlFor="unitPrice" className="text-slate-100">Unit Price *</Label>
                        <Input
                          id="unitPrice"
                          type="number"
                          step="0.01"
                          {...lineItemForm.register("unitPrice", { valueAsNumber: true })}
                          className="bg-[#0f1e35] border-slate-600 text-slate-100"
                        />
                        {lineItemForm.formState.errors.unitPrice && (
                          <p className="text-sm text-red-400">
                            {lineItemForm.formState.errors.unitPrice.message}
                          </p>
                        )}
                      </div>
                    </div>
                    <div className="flex items-center space-x-2">
                      <Checkbox
                        id="saveToLibrary"
                        checked={saveToLibrary}
                        onCheckedChange={(checked) => setSaveToLibrary(checked === true)}
                      />
                      <Label
                        htmlFor="saveToLibrary"
                        className="text-sm font-normal cursor-pointer"
                      >
                        Save to item library for future use
                      </Label>
                    </div>
                    <DialogFooter>
                      <Button
                        type="button"
                        variant="outline"
                        onClick={() => {
                          setAddLineItemOpen(false);
                          setSaveToLibrary(false);
                        }}
                      >
                        Cancel
                      </Button>
                      <Button type="submit" disabled={addLineItem.isPending}>
                        {addLineItem.isPending && (
                          <LoadingSpinner size="sm" className="mr-2" />
                        )}
                        Add Item
                      </Button>
                    </DialogFooter>
                  </form>
                </DialogContent>
              </Dialog>
              <ItemLibraryDialog
                open={itemLibraryOpen}
                onOpenChange={setItemLibraryOpen}
                onSelect={handleSelectFromLibrary}
              />
                </div>
                </div>
              ) : null}
            </div>
          </CardHeader>
          <CardContent>
            {invoice.lineItems.length > 0 ? (
              <Table>
                <TableHeader>
                  <TableRow>
                    <TableHead className="text-slate-100">Description</TableHead>
                    <TableHead className="text-right text-slate-100">Quantity</TableHead>
                    <TableHead className="text-right text-slate-100">Unit Price</TableHead>
                    <TableHead className="text-right text-slate-100">Total</TableHead>
                    {canEdit && <TableHead className="text-right text-slate-100">Actions</TableHead>}
                  </TableRow>
                </TableHeader>
                <TableBody>
                  {invoice.lineItems.map((item) => (
                    <TableRow key={item.id}>
                      <TableCell className="text-slate-100">{item.description}</TableCell>
                      <TableCell className="text-right text-slate-100">{item.quantity}</TableCell>
                      <TableCell className="text-right text-slate-100">
                        ${item.unitPrice.toFixed(2)}
                      </TableCell>
                      <TableCell className="text-right font-medium text-slate-100">
                        ${item.total.toFixed(2)}
                      </TableCell>
                      {canEdit && (
                        <TableCell className="text-right">
                          <Button
                            variant="outline"
                            size="sm"
                            onClick={() =>
                              removeLineItem.mutate({
                                invoiceId,
                                lineItemId: item.id,
                              })
                            }
                            disabled={removeLineItem.isPending}
                          >
                            <Trash2 className="h-4 w-4" />
                          </Button>
                        </TableCell>
                      )}
                    </TableRow>
                  ))}
                </TableBody>
              </Table>
            ) : (
              <p className="text-sm text-slate-300 text-center py-4">
                No line items. {canEdit && "Add items to continue."}
              </p>
            )}
          </CardContent>
        </Card>

        {payments && payments.length > 0 && (
          <Card className="bg-[#1e3a5f] border-slate-700">
            <CardHeader>
              <CardTitle className="text-slate-100">Payments</CardTitle>
              <CardDescription className="text-slate-300">
                Payment history for this invoice
              </CardDescription>
            </CardHeader>
            <CardContent>
              <Table>
                <TableHeader>
                  <TableRow>
                    <TableHead className="text-slate-100">Date</TableHead>
                    <TableHead className="text-slate-100">Method</TableHead>
                    <TableHead className="text-right text-slate-100">Amount</TableHead>
                  </TableRow>
                </TableHeader>
                <TableBody>
                  {payments.map((payment) => (
                    <TableRow key={payment.id}>
                      <TableCell className="text-slate-100">{formatDate(payment.paymentDate)}</TableCell>
                      <TableCell className="text-slate-100">{payment.paymentMethod}</TableCell>
                      <TableCell className="text-right font-medium text-slate-100">
                        ${payment.amount.toFixed(2)}
                      </TableCell>
                    </TableRow>
                  ))}
                </TableBody>
              </Table>
            </CardContent>
          </Card>
        )}

        {invoice.paymentPlan === "PAY_IN_4" && paymentSchedule && paymentSchedule.length > 0 && (
          <Card className="bg-[#1e3a5f] border-slate-700">
            <CardHeader>
              <CardTitle className="text-slate-100">Payment Schedule</CardTitle>
              <CardDescription className="text-slate-300">
                Installment schedule for Pay in 4 plan
              </CardDescription>
            </CardHeader>
            <CardContent>
              <Table>
                <TableHeader>
                  <TableRow>
                    <TableHead className="text-slate-100">Installment</TableHead>
                    <TableHead className="text-slate-100">Due Date</TableHead>
                    <TableHead className="text-right text-slate-100">Amount</TableHead>
                    <TableHead className="text-right text-slate-100">Status</TableHead>
                  </TableRow>
                </TableHeader>
                <TableBody>
                  {paymentSchedule.map((installment) => (
                    <TableRow key={installment.id}>
                      <TableCell className="text-slate-100">#{installment.installmentNumber}</TableCell>
                      <TableCell className="text-slate-100">{formatDate(installment.dueDate)}</TableCell>
                      <TableCell className="text-right font-medium text-slate-100">
                        ${installment.amount.toFixed(2)}
                      </TableCell>
                      <TableCell className="text-right">
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
                      </TableCell>
                    </TableRow>
                  ))}
                </TableBody>
              </Table>
            </CardContent>
          </Card>
        )}
      </div>
    </MainLayout>
  );
}

