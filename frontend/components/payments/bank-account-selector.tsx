"use client";

import { useState, useEffect } from "react";
import { Building2, Plus } from "lucide-react";
import { cn } from "@/lib/utils";
import { Label } from "@/components/ui/label";
import { Button } from "@/components/ui/button";
import {
  Dialog,
  DialogContent,
  DialogDescription,
  DialogFooter,
  DialogHeader,
  DialogTitle,
} from "@/components/ui/dialog";
import { Input } from "@/components/ui/input";
import {
  Select,
  SelectContent,
  SelectItem,
  SelectTrigger,
  SelectValue,
} from "@/components/ui/select";
import { useForm } from "react-hook-form";
import { zodResolver } from "@hookform/resolvers/zod";
import { z } from "zod";

export interface DummyBankAccount {
  id: string;
  last4: string;
  routingNumber: string;
  bankName: string;
  accountType: string;
  accountHolderName: string;
}

const DEFAULT_BANK_TEMPLATES = [
  {
    id: "bank-1",
    last4: "1234",
    routingNumber: "021000021",
    bankName: "Chase Bank",
    accountType: "Checking",
  },
  {
    id: "bank-2",
    last4: "5678",
    routingNumber: "111000025",
    bankName: "Bank of America",
    accountType: "Checking",
  },
  {
    id: "bank-3",
    last4: "9012",
    routingNumber: "121000248",
    bankName: "Wells Fargo",
    accountType: "Checking",
  },
];

const addBankAccountSchema = z.object({
  last4: z.string().regex(/^\d{4}$/, "Last 4 digits must be 4 numbers"),
  routingNumber: z.string().regex(/^\d{9}$/, "Routing number must be 9 digits"),
  bankName: z.string().min(1, "Bank name is required"),
  accountType: z.string().min(1, "Account type is required"),
});

type AddBankAccountFormData = z.infer<typeof addBankAccountSchema>;

interface BankAccountSelectorProps {
  selectedAccountId: string | null;
  onSelectAccount: (accountId: string) => void;
  userName: string; // Name of the logged-in user
}

const STORAGE_KEY_PREFIX = "user_bank_accounts_";

export function BankAccountSelector({ selectedAccountId, onSelectAccount, userName }: BankAccountSelectorProps) {
  const [accounts, setAccounts] = useState<DummyBankAccount[]>([]);
  const [addAccountOpen, setAddAccountOpen] = useState(false);
  const [userId, setUserId] = useState<string>("");

  // Generate a user ID based on the user name for localStorage key
  useEffect(() => {
    // Use a simple hash of the user name as the storage key
    const hash = userName.split("").reduce((acc, char) => {
      return ((acc << 5) - acc) + char.charCodeAt(0);
    }, 0);
    setUserId(Math.abs(hash).toString());
  }, [userName]);

  // Load accounts from localStorage and merge with default accounts
  useEffect(() => {
    if (!userId) return;

    const storageKey = `${STORAGE_KEY_PREFIX}${userId}`;
    const storedAccounts = localStorage.getItem(storageKey);
    const customAccounts: DummyBankAccount[] = storedAccounts ? JSON.parse(storedAccounts) : [];

    // Create default accounts with user's name
    const defaultAccounts: DummyBankAccount[] = DEFAULT_BANK_TEMPLATES.map((template) => ({
      ...template,
      accountHolderName: userName,
    }));

    // Merge default accounts with custom accounts
    setAccounts([...defaultAccounts, ...customAccounts]);
  }, [userId, userName]);

  const addAccountForm = useForm<AddBankAccountFormData>({
    resolver: zodResolver(addBankAccountSchema),
    defaultValues: {
      last4: "",
      routingNumber: "",
      bankName: "",
      accountType: "",
    },
  });

  const handleAddAccount = (data: AddBankAccountFormData) => {
    if (!userId) return;

    const newAccount: DummyBankAccount = {
      id: `bank-custom-${Date.now()}`,
      last4: data.last4,
      routingNumber: data.routingNumber,
      bankName: data.bankName,
      accountType: data.accountType,
      accountHolderName: userName,
    };

    const storageKey = `${STORAGE_KEY_PREFIX}${userId}`;
    const storedAccounts = localStorage.getItem(storageKey);
    const customAccounts: DummyBankAccount[] = storedAccounts ? JSON.parse(storedAccounts) : [];
    customAccounts.push(newAccount);
    localStorage.setItem(storageKey, JSON.stringify(customAccounts));

    // Update local state
    setAccounts((prev) => [...prev, newAccount]);
    setAddAccountOpen(false);
    addAccountForm.reset();
  };

  return (
    <div className="space-y-3">
      <Label className="text-sm font-medium">Select Checking Account</Label>
      <div className="grid gap-3">
        {accounts.map((account) => (
          <button
            key={account.id}
            type="button"
            onClick={() => onSelectAccount(account.id)}
            className={cn(
              "relative flex items-center gap-4 rounded-lg border-2 p-4 text-left transition-all hover:border-blue-400",
              selectedAccountId === account.id
                ? "border-blue-500 bg-blue-950/30"
                : "border-slate-600 bg-[#1e3a5f]"
            )}
          >
            <div className="flex h-12 w-12 items-center justify-center rounded-lg bg-blue-900/30">
              <Building2 className="h-6 w-6 text-blue-400" />
            </div>
            <div className="flex-1">
              <div className="flex items-center justify-between">
                <div>
                  <div className="font-semibold text-slate-100">{account.bankName}</div>
                  <div className="text-sm text-slate-300">
                    •••• •••• •••• {account.last4} • {account.accountType}
                  </div>
                  <div className="text-xs text-slate-400 mt-1">
                    Routing: ••••{account.routingNumber.slice(-4)}
                  </div>
                </div>
                <div className="text-right">
                  <div className="text-sm font-medium text-slate-100">{account.accountHolderName}</div>
                </div>
              </div>
            </div>
            {selectedAccountId === account.id && (
              <div className="absolute right-3 top-3">
                <div className="h-5 w-5 rounded-full bg-blue-500 flex items-center justify-center">
                  <svg
                    className="h-3 w-3 text-white"
                    fill="none"
                    strokeLinecap="round"
                    strokeLinejoin="round"
                    strokeWidth="2"
                    viewBox="0 0 24 24"
                    stroke="currentColor"
                  >
                    <path d="M5 13l4 4L19 7"></path>
                  </svg>
                </div>
              </div>
            )}
          </button>
        ))}
      </div>

      {/* Add New Account Button */}
      <Button
        type="button"
        variant="outline"
        className="w-full border-dashed border-2 border-slate-600 hover:border-blue-400 hover:bg-blue-950/20 text-slate-100"
        onClick={() => setAddAccountOpen(true)}
      >
        <Plus className="mr-2 h-4 w-4" />
        Add New Account
      </Button>

      {/* Add Account Dialog */}
      <Dialog open={addAccountOpen} onOpenChange={setAddAccountOpen}>
        <DialogContent>
          <DialogHeader>
            <DialogTitle>Add New Checking Account</DialogTitle>
            <DialogDescription>
              Add a new checking account. The account will be saved with your name: {userName}
            </DialogDescription>
          </DialogHeader>
          <form onSubmit={addAccountForm.handleSubmit(handleAddAccount)} className="space-y-4">
            <div className="space-y-2">
              <Label htmlFor="last4">Last 4 Digits *</Label>
              <Input
                id="last4"
                placeholder="1234"
                maxLength={4}
                type="text"
                inputMode="numeric"
                pattern="[0-9]*"
                {...addAccountForm.register("last4", {
                  onChange: (e) => {
                    // Only allow numbers
                    const numericValue = e.target.value.replace(/[^0-9]/g, "");
                    addAccountForm.setValue("last4", numericValue);
                  },
                })}
              />
              {addAccountForm.formState.errors.last4 && (
                <p className="text-sm text-destructive">
                  {addAccountForm.formState.errors.last4.message}
                </p>
              )}
            </div>
            <div className="space-y-2">
              <Label htmlFor="routingNumber">Routing Number *</Label>
              <Input
                id="routingNumber"
                placeholder="021000021"
                maxLength={9}
                type="text"
                inputMode="numeric"
                pattern="[0-9]*"
                {...addAccountForm.register("routingNumber", {
                  onChange: (e) => {
                    // Only allow numbers
                    const numericValue = e.target.value.replace(/[^0-9]/g, "");
                    addAccountForm.setValue("routingNumber", numericValue);
                  },
                })}
              />
              {addAccountForm.formState.errors.routingNumber && (
                <p className="text-sm text-destructive">
                  {addAccountForm.formState.errors.routingNumber.message}
                </p>
              )}
            </div>
            <div className="space-y-2">
              <Label htmlFor="bankName">Bank Name *</Label>
              <Select
                value={addAccountForm.watch("bankName")}
                onValueChange={(value) => addAccountForm.setValue("bankName", value)}
              >
                <SelectTrigger>
                  <SelectValue placeholder="Select bank" />
                </SelectTrigger>
                <SelectContent>
                  <SelectItem value="Chase Bank">Chase Bank</SelectItem>
                  <SelectItem value="Bank of America">Bank of America</SelectItem>
                  <SelectItem value="Wells Fargo">Wells Fargo</SelectItem>
                  <SelectItem value="Citibank">Citibank</SelectItem>
                  <SelectItem value="US Bank">US Bank</SelectItem>
                  <SelectItem value="PNC Bank">PNC Bank</SelectItem>
                  <SelectItem value="Capital One">Capital One</SelectItem>
                  <SelectItem value="TD Bank">TD Bank</SelectItem>
                  <SelectItem value="Other">Other</SelectItem>
                </SelectContent>
              </Select>
              {addAccountForm.formState.errors.bankName && (
                <p className="text-sm text-destructive">
                  {addAccountForm.formState.errors.bankName.message}
                </p>
              )}
            </div>
            <div className="space-y-2">
              <Label htmlFor="accountType">Account Type *</Label>
              <Select
                value={addAccountForm.watch("accountType")}
                onValueChange={(value) => addAccountForm.setValue("accountType", value)}
              >
                <SelectTrigger>
                  <SelectValue placeholder="Select account type" />
                </SelectTrigger>
                <SelectContent>
                  <SelectItem value="Checking">Checking</SelectItem>
                  <SelectItem value="Savings">Savings</SelectItem>
                </SelectContent>
              </Select>
              {addAccountForm.formState.errors.accountType && (
                <p className="text-sm text-destructive">
                  {addAccountForm.formState.errors.accountType.message}
                </p>
              )}
            </div>
            <DialogFooter>
              <Button
                type="button"
                variant="outline"
                onClick={() => {
                  setAddAccountOpen(false);
                  addAccountForm.reset();
                }}
              >
                Cancel
              </Button>
              <Button type="submit">Add Account</Button>
            </DialogFooter>
          </form>
        </DialogContent>
      </Dialog>

      <p className="text-xs text-muted-foreground">
        These are test bank accounts for demonstration purposes only.
      </p>
    </div>
  );
}

