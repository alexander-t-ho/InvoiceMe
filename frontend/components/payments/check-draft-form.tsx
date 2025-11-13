"use client";

import { useForm } from "react-hook-form";
import { zodResolver } from "@hookform/resolvers/zod";
import { z } from "zod";
import { Label } from "@/components/ui/label";
import { Input } from "@/components/ui/input";
import { Textarea } from "@/components/ui/textarea";
import { FileText } from "lucide-react";

const checkDraftSchema = z.object({
  checkNumber: z.string().min(1, "Check number is required"),
  bankName: z.string().min(1, "Bank name is required"),
  accountNumber: z.string().min(1, "Account number is required"),
  routingNumber: z.string().regex(/^\d{9}$/, "Routing number must be 9 digits"),
  memo: z.string().optional(),
});

export type CheckDraftFormData = z.infer<typeof checkDraftSchema>;

interface CheckDraftFormProps {
  amount: number;
  payeeName: string;
  onDraftComplete: (data: CheckDraftFormData | null) => void;
  isDrafted: boolean;
  draftedData?: CheckDraftFormData | null;
}

export function CheckDraftForm({ 
  amount, 
  payeeName, 
  onDraftComplete, 
  isDrafted,
  draftedData 
}: CheckDraftFormProps) {
  const form = useForm<CheckDraftFormData>({
    resolver: zodResolver(checkDraftSchema),
    defaultValues: draftedData || {
      checkNumber: "",
      bankName: "",
      accountNumber: "",
      routingNumber: "",
      memo: "",
    },
  });

  const handleSubmit = (data: CheckDraftFormData) => {
    onDraftComplete(data);
  };

  if (isDrafted && draftedData) {
    return (
      <div className="space-y-4 border-t pt-4">
        <div className="rounded-lg border-2 border-slate-600 bg-[#1e3a5f] p-6">
          <div className="mb-4 flex items-center gap-2">
            <FileText className="h-5 w-5 text-blue-400" />
            <h3 className="text-lg font-semibold text-slate-100">Check Draft</h3>
          </div>
          
          <div className="space-y-3 text-sm">
            <div className="flex justify-between border-b border-slate-600 pb-2">
              <span className="text-slate-300">Check Number:</span>
              <span className="font-medium text-slate-100">{draftedData.checkNumber}</span>
            </div>
            <div className="flex justify-between border-b border-slate-600 pb-2">
              <span className="text-slate-300">Pay to the order of:</span>
              <span className="font-medium text-slate-100">{payeeName}</span>
            </div>
            <div className="flex justify-between border-b border-slate-600 pb-2">
              <span className="text-slate-300">Amount:</span>
              <span className="font-bold text-slate-100">${amount.toFixed(2)}</span>
            </div>
            <div className="flex justify-between border-b border-slate-600 pb-2">
              <span className="text-slate-300">Bank:</span>
              <span className="font-medium text-slate-100">{draftedData.bankName}</span>
            </div>
            <div className="flex justify-between border-b border-slate-600 pb-2">
              <span className="text-slate-300">Account Number:</span>
              <span className="font-medium text-slate-100">••••{draftedData.accountNumber.slice(-4)}</span>
            </div>
            <div className="flex justify-between border-b border-slate-600 pb-2">
              <span className="text-slate-300">Routing Number:</span>
              <span className="font-medium text-slate-100">••••{draftedData.routingNumber.slice(-4)}</span>
            </div>
            {draftedData.memo && (
              <div className="flex justify-between border-b border-slate-600 pb-2">
                <span className="text-slate-300">Memo:</span>
                <span className="font-medium text-slate-100">{draftedData.memo}</span>
              </div>
            )}
          </div>
          
          <button
            type="button"
            onClick={() => {
              onDraftComplete(null);
              form.reset();
            }}
            className="mt-4 text-sm text-blue-400 hover:text-blue-300 underline"
          >
            Edit Check Details
          </button>
        </div>
      </div>
    );
  }

  return (
    <div className="space-y-4 border-t pt-4">
      <div className="flex items-center gap-2">
        <FileText className="h-5 w-5 text-blue-400" />
        <Label className="text-base font-semibold text-slate-100">Draft Check</Label>
      </div>
      
      <form onSubmit={form.handleSubmit(handleSubmit)} className="space-y-4">
        <div className="grid grid-cols-2 gap-4">
          <div className="space-y-2">
            <Label htmlFor="checkNumber" className="text-slate-200">Check Number *</Label>
            <Input
              id="checkNumber"
              placeholder="1234"
              {...form.register("checkNumber")}
              className="bg-[#0f1e35] border-slate-600 text-slate-100"
            />
            {form.formState.errors.checkNumber && (
              <p className="text-sm text-destructive">
                {form.formState.errors.checkNumber.message}
              </p>
            )}
          </div>
          
          <div className="space-y-2">
            <Label htmlFor="bankName" className="text-slate-200">Bank Name *</Label>
            <Input
              id="bankName"
              placeholder="Chase Bank"
              {...form.register("bankName")}
              className="bg-[#0f1e35] border-slate-600 text-slate-100"
            />
            {form.formState.errors.bankName && (
              <p className="text-sm text-destructive">
                {form.formState.errors.bankName.message}
              </p>
            )}
          </div>
        </div>
        
        <div className="grid grid-cols-2 gap-4">
          <div className="space-y-2">
            <Label htmlFor="accountNumber" className="text-slate-200">Account Number *</Label>
            <Input
              id="accountNumber"
              placeholder="1234567890"
              {...form.register("accountNumber")}
              className="bg-[#0f1e35] border-slate-600 text-slate-100"
            />
            {form.formState.errors.accountNumber && (
              <p className="text-sm text-destructive">
                {form.formState.errors.accountNumber.message}
              </p>
            )}
          </div>
          
          <div className="space-y-2">
            <Label htmlFor="routingNumber" className="text-slate-200">Routing Number *</Label>
            <Input
              id="routingNumber"
              placeholder="021000021"
              maxLength={9}
              {...form.register("routingNumber", {
                onChange: (e) => {
                  // Only allow numbers
                  const numericValue = e.target.value.replace(/[^0-9]/g, "");
                  form.setValue("routingNumber", numericValue);
                },
              })}
              className="bg-[#0f1e35] border-slate-600 text-slate-100"
            />
            {form.formState.errors.routingNumber && (
              <p className="text-sm text-destructive">
                {form.formState.errors.routingNumber.message}
              </p>
            )}
          </div>
        </div>
        
        <div className="space-y-2">
          <Label htmlFor="memo" className="text-slate-200">Memo (Optional)</Label>
          <Textarea
            id="memo"
            placeholder="Payment for invoice..."
            {...form.register("memo")}
            className="bg-[#0f1e35] border-slate-600 text-slate-100"
            rows={2}
          />
        </div>
        
        <div className="rounded-lg border border-slate-600 bg-[#0f1e35] p-4">
          <div className="text-sm space-y-1">
            <div className="flex justify-between">
              <span className="text-slate-300">Pay to:</span>
              <span className="font-medium text-slate-100">{payeeName}</span>
            </div>
            <div className="flex justify-between">
              <span className="text-slate-300">Amount:</span>
              <span className="font-bold text-slate-100">${amount.toFixed(2)}</span>
            </div>
          </div>
        </div>
        
        <button
          type="submit"
          className="w-full rounded-lg bg-blue-600 px-4 py-2 text-sm font-medium text-white hover:bg-blue-700 transition-colors"
        >
          Draft Check
        </button>
      </form>
    </div>
  );
}

