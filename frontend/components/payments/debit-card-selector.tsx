"use client";

import { useState, useEffect } from "react";
import { CreditCard, Plus } from "lucide-react";
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

export interface DummyDebitCard {
  id: string;
  last4: string;
  brand: string;
  name: string;
  expiryMonth: string;
  expiryYear: string;
}

const DEFAULT_CARD_TEMPLATES = [
  {
    id: "debit-1",
    last4: "1234",
    brand: "Visa Debit",
    expiryMonth: "12",
    expiryYear: "2025",
  },
  {
    id: "debit-2",
    last4: "5678",
    brand: "Mastercard Debit",
    expiryMonth: "06",
    expiryYear: "2026",
  },
  {
    id: "debit-3",
    last4: "9012",
    brand: "Visa Debit",
    expiryMonth: "09",
    expiryYear: "2027",
  },
];

const addCardSchema = z.object({
  cardNumber: z.string().regex(/^\d{4}$/, "Last 4 digits must be 4 numbers"),
  brand: z.string().min(1, "Card brand is required"),
  expiryMonth: z.string().min(1, "Expiry month is required"),
  expiryYear: z.string().min(1, "Expiry year is required"),
});

type AddCardFormData = z.infer<typeof addCardSchema>;

interface DebitCardSelectorProps {
  selectedCardId: string | null;
  onSelectCard: (cardId: string) => void;
  userName: string; // Name of the logged-in user
}

const STORAGE_KEY_PREFIX = "user_debit_cards_";

export function DebitCardSelector({ selectedCardId, onSelectCard, userName }: DebitCardSelectorProps) {
  const [cards, setCards] = useState<DummyDebitCard[]>([]);
  const [addCardOpen, setAddCardOpen] = useState(false);
  const [userId, setUserId] = useState<string>("");

  // Generate a user ID based on the user name for localStorage key
  useEffect(() => {
    // Use a simple hash of the user name as the storage key
    const hash = userName.split("").reduce((acc, char) => {
      return ((acc << 5) - acc) + char.charCodeAt(0);
    }, 0);
    setUserId(Math.abs(hash).toString());
  }, [userName]);

  // Load cards from localStorage and merge with default cards
  useEffect(() => {
    if (!userId) return;

    const storageKey = `${STORAGE_KEY_PREFIX}${userId}`;
    const storedCards = localStorage.getItem(storageKey);
    const customCards: DummyDebitCard[] = storedCards ? JSON.parse(storedCards) : [];

    // Create default cards with user's name
    const defaultCards: DummyDebitCard[] = DEFAULT_CARD_TEMPLATES.map((template) => ({
      ...template,
      name: userName,
    }));

    // Merge default cards with custom cards
    setCards([...defaultCards, ...customCards]);
  }, [userId, userName]);

  const addCardForm = useForm<AddCardFormData>({
    resolver: zodResolver(addCardSchema),
    defaultValues: {
      cardNumber: "",
      brand: "",
      expiryMonth: "",
      expiryYear: "",
    },
  });

  const handleAddCard = (data: AddCardFormData) => {
    if (!userId) return;

    const newCard: DummyDebitCard = {
      id: `debit-custom-${Date.now()}`,
      last4: data.cardNumber,
      brand: data.brand,
      name: userName,
      expiryMonth: data.expiryMonth,
      expiryYear: data.expiryYear,
    };

    const storageKey = `${STORAGE_KEY_PREFIX}${userId}`;
    const storedCards = localStorage.getItem(storageKey);
    const customCards: DummyDebitCard[] = storedCards ? JSON.parse(storedCards) : [];
    customCards.push(newCard);
    localStorage.setItem(storageKey, JSON.stringify(customCards));

    // Update local state
    setCards((prev) => [...prev, newCard]);
    setAddCardOpen(false);
    addCardForm.reset();
  };

  const currentYear = new Date().getFullYear();
  const years = Array.from({ length: 10 }, (_, i) => currentYear + i);

  return (
    <div className="space-y-3">
      <Label className="text-sm font-medium">Select Debit Card</Label>
      <div className="grid gap-3">
        {cards.map((card) => (
          <button
            key={card.id}
            type="button"
            onClick={() => onSelectCard(card.id)}
            className={cn(
              "relative flex items-center gap-4 rounded-lg border-2 p-4 text-left transition-all hover:border-green-400",
              selectedCardId === card.id
                ? "border-green-500 bg-green-950/30"
                : "border-slate-600 bg-[#1e3a5f]"
            )}
          >
            <div className="flex h-12 w-12 items-center justify-center rounded-lg bg-green-900/30">
              <CreditCard className="h-6 w-6 text-green-400" />
            </div>
            <div className="flex-1">
              <div className="flex items-center justify-between">
                <div>
                  <div className="font-semibold text-slate-100">{card.brand}</div>
                  <div className="text-sm text-slate-300">•••• •••• •••• {card.last4}</div>
                </div>
                <div className="text-right">
                  <div className="text-sm font-medium text-slate-100">{card.name}</div>
                  <div className="text-xs text-slate-400">
                    {card.expiryMonth}/{card.expiryYear}
                  </div>
                </div>
              </div>
            </div>
            {selectedCardId === card.id && (
              <div className="absolute right-3 top-3">
                <div className="h-5 w-5 rounded-full bg-green-500 flex items-center justify-center">
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

      {/* Add New Card Button */}
      <Button
        type="button"
        variant="outline"
        className="w-full border-dashed border-2 border-slate-600 hover:border-green-400 hover:bg-green-950/20 text-slate-100"
        onClick={() => setAddCardOpen(true)}
      >
        <Plus className="mr-2 h-4 w-4" />
        Add New Debit Card
      </Button>

      {/* Add Card Dialog */}
      <Dialog open={addCardOpen} onOpenChange={setAddCardOpen}>
        <DialogContent>
          <DialogHeader>
            <DialogTitle>Add New Debit Card</DialogTitle>
            <DialogDescription>
              Add a new debit card. The card will be saved with your name: {userName}
            </DialogDescription>
          </DialogHeader>
          <form onSubmit={addCardForm.handleSubmit(handleAddCard)} className="space-y-4">
            <div className="space-y-2">
              <Label htmlFor="cardNumber">Last 4 Digits *</Label>
              <Input
                id="cardNumber"
                placeholder="1234"
                maxLength={4}
                type="text"
                inputMode="numeric"
                pattern="[0-9]*"
                {...addCardForm.register("cardNumber", {
                  onChange: (e) => {
                    // Only allow numbers
                    const numericValue = e.target.value.replace(/[^0-9]/g, "");
                    addCardForm.setValue("cardNumber", numericValue);
                  },
                })}
              />
              {addCardForm.formState.errors.cardNumber && (
                <p className="text-sm text-destructive">
                  {addCardForm.formState.errors.cardNumber.message}
                </p>
              )}
            </div>
            <div className="space-y-2">
              <Label htmlFor="brand">Card Brand *</Label>
              <Select
                value={addCardForm.watch("brand")}
                onValueChange={(value) => addCardForm.setValue("brand", value)}
              >
                <SelectTrigger>
                  <SelectValue placeholder="Select card brand" />
                </SelectTrigger>
                <SelectContent>
                  <SelectItem value="Visa Debit">Visa Debit</SelectItem>
                  <SelectItem value="Mastercard Debit">Mastercard Debit</SelectItem>
                  <SelectItem value="Discover Debit">Discover Debit</SelectItem>
                </SelectContent>
              </Select>
              {addCardForm.formState.errors.brand && (
                <p className="text-sm text-destructive">
                  {addCardForm.formState.errors.brand.message}
                </p>
              )}
            </div>
            <div className="grid grid-cols-2 gap-4">
              <div className="space-y-2">
                <Label htmlFor="expiryMonth">Expiry Month *</Label>
                <Select
                  value={addCardForm.watch("expiryMonth")}
                  onValueChange={(value) => addCardForm.setValue("expiryMonth", value)}
                >
                  <SelectTrigger>
                    <SelectValue placeholder="MM" />
                  </SelectTrigger>
                  <SelectContent>
                    {Array.from({ length: 12 }, (_, i) => {
                      const month = String(i + 1).padStart(2, "0");
                      return (
                        <SelectItem key={month} value={month}>
                          {month}
                        </SelectItem>
                      );
                    })}
                  </SelectContent>
                </Select>
                {addCardForm.formState.errors.expiryMonth && (
                  <p className="text-sm text-destructive">
                    {addCardForm.formState.errors.expiryMonth.message}
                  </p>
                )}
              </div>
              <div className="space-y-2">
                <Label htmlFor="expiryYear">Expiry Year *</Label>
                <Select
                  value={addCardForm.watch("expiryYear")}
                  onValueChange={(value) => addCardForm.setValue("expiryYear", value)}
                >
                  <SelectTrigger>
                    <SelectValue placeholder="YYYY" />
                  </SelectTrigger>
                  <SelectContent>
                    {years.map((year) => (
                      <SelectItem key={year} value={String(year)}>
                        {year}
                      </SelectItem>
                    ))}
                  </SelectContent>
                </Select>
                {addCardForm.formState.errors.expiryYear && (
                  <p className="text-sm text-destructive">
                    {addCardForm.formState.errors.expiryYear.message}
                  </p>
                )}
              </div>
            </div>
            <DialogFooter>
              <Button
                type="button"
                variant="outline"
                onClick={() => {
                  setAddCardOpen(false);
                  addCardForm.reset();
                }}
              >
                Cancel
              </Button>
              <Button type="submit">Add Card</Button>
            </DialogFooter>
          </form>
        </DialogContent>
      </Dialog>

      <p className="text-xs text-muted-foreground">
        These are test debit cards for demonstration purposes only.
      </p>
    </div>
  );
}

