"use client";

import { useForm } from "react-hook-form";
import { zodResolver } from "@hookform/resolvers/zod";
import { z } from "zod";
import { Button } from "@/components/ui/button";
import { Input } from "@/components/ui/input";
import { Label } from "@/components/ui/label";
import { useCreateItem, useUpdateItem } from "@/hooks/useItems";
import { LoadingSpinner } from "@/components/ui/loading-spinner";
import { useEffect } from "react";

const itemSchema = z.object({
  description: z.string().min(1, "Description is required"),
  unitPrice: z.number().min(0, "Unit price must be greater than or equal to 0"),
});

type ItemFormData = z.infer<typeof itemSchema>;

interface ItemFormProps {
  itemId?: string;
  initialDescription?: string;
  initialUnitPrice?: number;
  onSuccess: () => void;
  onCancel: () => void;
}

export function ItemForm({
  itemId,
  initialDescription = "",
  initialUnitPrice = 0,
  onSuccess,
  onCancel,
}: ItemFormProps) {
  const createItem = useCreateItem();
  const updateItem = useUpdateItem();

  const {
    register,
    handleSubmit,
    reset,
    formState: { errors },
  } = useForm<ItemFormData>({
    resolver: zodResolver(itemSchema),
    defaultValues: {
      description: initialDescription,
      unitPrice: initialUnitPrice,
    },
  });

  useEffect(() => {
    reset({
      description: initialDescription,
      unitPrice: initialUnitPrice,
    });
  }, [initialDescription, initialUnitPrice, reset]);

  const onSubmit = (data: ItemFormData) => {
    if (itemId) {
      updateItem.mutate(
        {
          id: itemId,
          data: {
            description: data.description,
            unitPrice: data.unitPrice,
          },
        },
        {
          onSuccess,
        }
      );
    } else {
      createItem.mutate(
        {
          description: data.description,
          unitPrice: data.unitPrice,
        },
        {
          onSuccess,
        }
      );
    }
  };

  const isPending = createItem.isPending || updateItem.isPending;

  return (
    <form onSubmit={handleSubmit(onSubmit)} className="space-y-4">
      <div className="space-y-2">
        <Label htmlFor="description">Description *</Label>
        <Input
          id="description"
          {...register("description")}
          disabled={isPending}
        />
        {errors.description && (
          <p className="text-sm text-destructive">
            {errors.description.message}
          </p>
        )}
      </div>

      <div className="space-y-2">
        <Label htmlFor="unitPrice">Unit Price *</Label>
        <Input
          id="unitPrice"
          type="number"
          step="0.01"
          {...register("unitPrice", { valueAsNumber: true })}
          disabled={isPending}
        />
        {errors.unitPrice && (
          <p className="text-sm text-destructive">
            {errors.unitPrice.message}
          </p>
        )}
      </div>

      <div className="flex justify-end space-x-2">
        <Button
          type="button"
          variant="outline"
          onClick={onCancel}
          disabled={isPending}
        >
          Cancel
        </Button>
        <Button type="submit" disabled={isPending}>
          {isPending && <LoadingSpinner size="sm" className="mr-2" />}
          {itemId ? "Update" : "Create"}
        </Button>
      </div>
    </form>
  );
}









