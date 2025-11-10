"use client";

import { useForm } from "react-hook-form";
import { zodResolver } from "@hookform/resolvers/zod";
import { Button } from "@/components/ui/button";
import { Input } from "@/components/ui/input";
import { Label } from "@/components/ui/label";
import {
  createCustomerSchema,
  updateCustomerSchema,
  type CreateCustomerFormData,
  type UpdateCustomerFormData,
} from "@/lib/validations/customer";
import { LoadingSpinner } from "@/components/ui/loading-spinner";

interface CustomerFormProps {
  initialData?: {
    name: string;
    email: string;
    address?: string | null;
  };
  onSubmit: (data: CreateCustomerFormData | UpdateCustomerFormData) => void;
  isLoading?: boolean;
  submitLabel?: string;
}

export function CustomerForm({
  initialData,
  onSubmit,
  isLoading = false,
  submitLabel = "Save",
}: CustomerFormProps) {
  const schema = initialData ? updateCustomerSchema : createCustomerSchema;
  
  const {
    register,
    handleSubmit,
    formState: { errors },
  } = useForm<CreateCustomerFormData | UpdateCustomerFormData>({
    resolver: zodResolver(schema),
    defaultValues: initialData || {
      name: "",
      email: "",
      address: "",
    },
  });

  return (
    <form onSubmit={handleSubmit(onSubmit)} className="space-y-4">
      <div className="space-y-2">
        <Label htmlFor="name">Name *</Label>
        <Input
          id="name"
          {...register("name")}
          placeholder="John Doe"
          disabled={isLoading}
        />
        {errors.name && (
          <p className="text-sm text-destructive">{errors.name.message}</p>
        )}
      </div>

      <div className="space-y-2">
        <Label htmlFor="email">Email *</Label>
        <Input
          id="email"
          type="email"
          {...register("email")}
          placeholder="john@example.com"
          disabled={isLoading}
        />
        {errors.email && (
          <p className="text-sm text-destructive">{errors.email.message}</p>
        )}
      </div>

      <div className="space-y-2">
        <Label htmlFor="address">Address</Label>
        <Input
          id="address"
          {...register("address")}
          placeholder="123 Main St"
          disabled={isLoading}
        />
        {errors.address && (
          <p className="text-sm text-destructive">{errors.address.message}</p>
        )}
      </div>

      {!initialData && (
        <div className="rounded-md bg-blue-50 border border-blue-200 p-3">
          <p className="text-sm text-blue-800">
            <strong>Login Information:</strong> The customer will be able to log in immediately using their email and password <strong>123456</strong>.
          </p>
        </div>
      )}

      <div className="flex justify-end space-x-2">
        <Button type="submit" disabled={isLoading}>
          {isLoading && <LoadingSpinner size="sm" className="mr-2" />}
          {submitLabel}
        </Button>
      </div>
    </form>
  );
}

