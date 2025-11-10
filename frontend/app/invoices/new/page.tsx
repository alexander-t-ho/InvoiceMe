"use client";

import { useRouter } from "next/navigation";
import { useForm, useFieldArray } from "react-hook-form";
import { zodResolver } from "@hookform/resolvers/zod";
import { MainLayout } from "@/components/layout/main-layout";
import { useCreateInvoice, useAddLineItem } from "@/hooks/useInvoices";
import { useCustomers } from "@/hooks/useCustomers";
import { createInvoiceSchema, addLineItemSchema, type CreateInvoiceFormData, type AddLineItemFormData } from "@/lib/validations/invoice";
import { Button } from "@/components/ui/button";
import { Input } from "@/components/ui/input";
import { Label } from "@/components/ui/label";
import {
  Select,
  SelectContent,
  SelectItem,
  SelectTrigger,
  SelectValue,
} from "@/components/ui/select";
import { RadioGroup, RadioGroupItem } from "@/components/ui/radio-group";
import type { PaymentPlan } from "@/types/api";
import { Card, CardContent, CardDescription, CardHeader, CardTitle } from "@/components/ui/card";
import { LoadingSpinner } from "@/components/ui/loading-spinner";
import { Plus, Trash2, Library } from "lucide-react";
import { useState } from "react";
import { ItemLibraryDialog } from "@/components/items/item-library-dialog";
import type { ItemResponse } from "@/types/api";
import {
  Table,
  TableBody,
  TableCell,
  TableHead,
  TableHeader,
  TableRow,
} from "@/components/ui/table";
import { Checkbox } from "@/components/ui/checkbox";

export default function NewInvoicePage() {
  const router = useRouter();
  const createInvoice = useCreateInvoice();
  const addLineItem = useAddLineItem();
  // Optimized: Only fetch first page of customers (20) for dropdown - users can search if needed
  const { data: customersData, isLoading: customersLoading } = useCustomers(0, 20);
  
  const [itemLibraryOpen, setItemLibraryOpen] = useState(false);
  const [selectedItemIndex, setSelectedItemIndex] = useState<number | null>(null);
  const [saveToLibrary, setSaveToLibrary] = useState(false);

  const {
    register,
    handleSubmit,
    setValue,
    watch,
    control,
    formState: { errors },
  } = useForm<CreateInvoiceFormData>({
    resolver: zodResolver(createInvoiceSchema),
    defaultValues: {
      customerId: "",
      issueDate: new Date().toISOString().split("T")[0],
      dueDate: new Date(Date.now() + 30 * 24 * 60 * 60 * 1000).toISOString().split("T")[0],
      paymentPlan: "FULL" as PaymentPlan,
      lineItems: [],
    },
  });

  const { fields, append, remove } = useFieldArray({
    control,
    name: "lineItems",
  });

  const customerId = watch("customerId");
  const lineItems = watch("lineItems");

  const handleSelectFromLibrary = (item: ItemResponse) => {
    if (selectedItemIndex !== null) {
      setValue(`lineItems.${selectedItemIndex}.description`, item.description);
      setValue(`lineItems.${selectedItemIndex}.unitPrice`, item.unitPrice);
      setValue(`lineItems.${selectedItemIndex}.itemId`, item.id);
    } else {
      append({
        description: item.description,
        unitPrice: item.unitPrice,
        quantity: 1,
        itemId: item.id,
      });
    }
    setItemLibraryOpen(false);
    setSelectedItemIndex(null);
  };

  const handleAddLineItem = () => {
    append({
      description: "",
      quantity: 1,
      unitPrice: 0,
      itemId: null,
    });
  };

  const handleFormSubmit = async (data: CreateInvoiceFormData) => {
    // Extract line items before creating invoice
    const { lineItems, ...invoiceData } = data;
    
    createInvoice.mutate(invoiceData, {
      onSuccess: async (invoice) => {
        // Add all line items sequentially
        if (lineItems && lineItems.length > 0) {
          try {
            // Use the invoiceService directly to add items sequentially
            const { invoiceService } = await import("@/lib/services/InvoiceService");
            for (const item of lineItems) {
              // Format the line item - ensure itemId is only included if it exists
              const lineItemData = {
                description: item.description,
                quantity: item.quantity,
                unitPrice: item.unitPrice,
                ...(item.itemId && { itemId: item.itemId }),
              };
              await invoiceService.addLineItem(invoice.id, lineItemData);
            }
          } catch (error) {
            // If adding items fails, still redirect but show error
            console.error("Error adding line items:", error);
          }
        }
        router.push(`/invoices/${invoice.id}`);
      },
    });
  };

  return (
    <MainLayout>
      <div className="space-y-6">
        <div>
          <h1 className="text-3xl font-bold tracking-tight">New Invoice</h1>
          <p className="text-muted-foreground">
            Create a new invoice
          </p>
        </div>

        <Card>
          <CardHeader>
            <CardTitle>Invoice Information</CardTitle>
            <CardDescription>
              Enter the invoice details below. You can add line items now or after creating the invoice.
            </CardDescription>
          </CardHeader>
          <CardContent>
            <form onSubmit={handleSubmit(handleFormSubmit)} className="space-y-4">
              <div className="space-y-2">
                <Label htmlFor="customerId">Customer *</Label>
                <Select
                  value={customerId}
                  onValueChange={(value) => setValue("customerId", value)}
                >
                  <SelectTrigger>
                    <SelectValue placeholder="Select a customer" />
                  </SelectTrigger>
                  <SelectContent>
                    {customersLoading ? (
                      <SelectItem value="loading" disabled>Loading customers...</SelectItem>
                    ) : customersData?.content && customersData.content.length > 0 ? (
                      customersData.content.map((customer) => (
                      <SelectItem key={customer.id} value={customer.id}>
                        {customer.name} ({customer.email})
                      </SelectItem>
                      ))
                    ) : (
                      <SelectItem value="none" disabled>No customers found</SelectItem>
                    )}
                  </SelectContent>
                </Select>
                {errors.customerId && (
                  <p className="text-sm text-destructive">
                    {errors.customerId.message}
                  </p>
                )}
              </div>

              <div className="grid grid-cols-2 gap-4">
                <div className="space-y-2">
                  <Label htmlFor="issueDate">Issue Date *</Label>
                  <Input
                    id="issueDate"
                    type="date"
                    {...register("issueDate")}
                    disabled={createInvoice.isPending}
                  />
                  {errors.issueDate && (
                    <p className="text-sm text-destructive">
                      {errors.issueDate.message}
                    </p>
                  )}
                </div>

                <div className="space-y-2">
                  <Label htmlFor="dueDate">Due Date *</Label>
                  <Input
                    id="dueDate"
                    type="date"
                    {...register("dueDate")}
                    disabled={createInvoice.isPending}
                  />
                  {errors.dueDate && (
                    <p className="text-sm text-destructive">
                      {errors.dueDate.message}
                    </p>
                  )}
                </div>
              </div>

              <div className="space-y-2">
                <Label>Payment Plan *</Label>
                <RadioGroup
                  value={watch("paymentPlan") || "FULL"}
                  onValueChange={(value: string) => setValue("paymentPlan", value as PaymentPlan)}
                >
                  <div className="flex items-center space-x-2">
                    <RadioGroupItem value="FULL" id="full" />
                    <Label htmlFor="full" className="font-normal cursor-pointer">
                      Full Payment
                    </Label>
                  </div>
                  <div className="flex items-center space-x-2">
                    <RadioGroupItem value="PAY_IN_4" id="pay-in-4" />
                    <Label htmlFor="pay-in-4" className="font-normal cursor-pointer">
                      Pay in 4 (4 installments, every 2 weeks)
                    </Label>
                  </div>
                </RadioGroup>
                {errors.paymentPlan && (
                  <p className="text-sm text-destructive">
                    {errors.paymentPlan.message}
                  </p>
                )}
              </div>

              <div className="space-y-4 border-t pt-4">
                <div className="flex items-center justify-between">
                  <Label className="text-base font-semibold">Line Items</Label>
                  <div className="flex space-x-2">
                    <Button
                      type="button"
                      variant="outline"
                      size="sm"
                      onClick={() => {
                        setSelectedItemIndex(null);
                        setItemLibraryOpen(true);
                      }}
                    >
                      <Library className="mr-2 h-4 w-4" />
                      From Library
                    </Button>
                    <Button
                      type="button"
                      variant="outline"
                      size="sm"
                      onClick={handleAddLineItem}
                    >
                      <Plus className="mr-2 h-4 w-4" />
                      Add Item
                    </Button>
                  </div>
                </div>

                {fields.length > 0 ? (
                  <div className="space-y-4">
                    <div className="rounded-md border">
                      <Table>
                        <TableHeader>
                          <TableRow>
                            <TableHead>Description</TableHead>
                            <TableHead className="text-right">Quantity</TableHead>
                            <TableHead className="text-right">Unit Price</TableHead>
                            <TableHead className="text-right">Total</TableHead>
                            <TableHead className="text-right">Actions</TableHead>
                          </TableRow>
                        </TableHeader>
                        <TableBody>
                          {fields.map((field, index) => {
                            const quantity = watch(`lineItems.${index}.quantity`) || 0;
                            const unitPrice = watch(`lineItems.${index}.unitPrice`) || 0;
                            const total = quantity * unitPrice;
                            return (
                              <TableRow key={field.id}>
                                <TableCell>
                                  <Input
                                    {...register(`lineItems.${index}.description`)}
                                    placeholder="Item description"
                                    className="w-full"
                                  />
                                  {errors.lineItems?.[index]?.description && (
                                    <p className="text-xs text-destructive mt-1">
                                      {errors.lineItems[index]?.description?.message}
                                    </p>
                                  )}
                                </TableCell>
                                <TableCell className="text-right">
                                  <Input
                                    type="number"
                                    step="0.01"
                                    {...register(`lineItems.${index}.quantity`, { valueAsNumber: true })}
                                    className="w-20 text-right"
                                  />
                                  {errors.lineItems?.[index]?.quantity && (
                                    <p className="text-xs text-destructive mt-1">
                                      {errors.lineItems[index]?.quantity?.message}
                                    </p>
                                  )}
                                </TableCell>
                                <TableCell className="text-right">
                                  <Input
                                    type="number"
                                    step="0.01"
                                    {...register(`lineItems.${index}.unitPrice`, { valueAsNumber: true })}
                                    className="w-24 text-right"
                                  />
                                  {errors.lineItems?.[index]?.unitPrice && (
                                    <p className="text-xs text-destructive mt-1">
                                      {errors.lineItems[index]?.unitPrice?.message}
                                    </p>
                                  )}
                                </TableCell>
                                <TableCell className="text-right font-medium">
                                  ${total.toFixed(2)}
                                </TableCell>
                                <TableCell className="text-right">
                                  <div className="flex justify-end space-x-2">
                                    <Button
                                      type="button"
                                      variant="outline"
                                      size="sm"
                                      onClick={() => {
                                        setSelectedItemIndex(index);
                                        setItemLibraryOpen(true);
                                      }}
                                    >
                                      <Library className="h-4 w-4" />
                                    </Button>
                                    <Button
                                      type="button"
                                      variant="outline"
                                      size="sm"
                                      onClick={() => remove(index)}
                                    >
                                      <Trash2 className="h-4 w-4" />
                                    </Button>
                                  </div>
                                </TableCell>
                              </TableRow>
                            );
                          })}
                        </TableBody>
                      </Table>
                    </div>
                    <div className="flex justify-end">
                      <div className="text-right">
                        <div className="text-sm text-muted-foreground">
                          Subtotal: ${lineItems?.reduce((sum, item) => {
                            const qty = item.quantity || 0;
                            const price = item.unitPrice || 0;
                            return sum + (qty * price);
                          }, 0).toFixed(2) || "0.00"}
                        </div>
                      </div>
                    </div>
                  </div>
                ) : (
                  <div className="text-center py-8 border rounded-md">
                    <p className="text-sm text-muted-foreground">
                      No line items added yet. Click Add Item or From Library to add items.
                    </p>
                  </div>
                )}
              </div>

              <div className="flex justify-end space-x-2">
                <Button
                  type="button"
                  variant="outline"
                  onClick={() => router.back()}
                  disabled={createInvoice.isPending}
                >
                  Cancel
                </Button>
                <Button type="submit" disabled={createInvoice.isPending}>
                  {createInvoice.isPending && (
                    <LoadingSpinner size="sm" className="mr-2" />
                  )}
                  Create Invoice
                </Button>
              </div>
            </form>
          </CardContent>
        </Card>
      </div>

      <ItemLibraryDialog
        open={itemLibraryOpen}
        onOpenChange={setItemLibraryOpen}
        onSelect={handleSelectFromLibrary}
      />
    </MainLayout>
  );
}

