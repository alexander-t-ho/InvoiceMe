"use client";

import { useRouter } from "next/navigation";
import { MainLayout } from "@/components/layout/main-layout";
import { CustomerForm } from "@/components/customers/customer-form";
import { useCreateCustomer } from "@/hooks/useCustomers";
import type { CreateCustomerFormData } from "@/lib/validations/customer";
import { Card, CardContent, CardDescription, CardHeader, CardTitle } from "@/components/ui/card";

export default function NewCustomerPage() {
  const router = useRouter();
  const createCustomer = useCreateCustomer();

  const handleSubmit = (data: CreateCustomerFormData) => {
    createCustomer.mutate(data, {
      onSuccess: () => {
        // Redirect to customers list so user can see the newly created customer
        router.push("/customers");
      },
    });
  };

  return (
    <MainLayout>
      <div className="space-y-6">
        <div>
          <h1 className="text-3xl font-bold tracking-tight">New Customer</h1>
          <p className="text-muted-foreground">
            Create a new customer
          </p>
        </div>

        <Card>
          <CardHeader>
            <CardTitle>Customer Information</CardTitle>
            <CardDescription>
              Enter the customer details below. The customer will be able to log in immediately after creation.
            </CardDescription>
          </CardHeader>
          <CardContent>
            <CustomerForm
              onSubmit={handleSubmit}
              isLoading={createCustomer.isPending}
              submitLabel="Create Customer"
            />
          </CardContent>
        </Card>
      </div>
    </MainLayout>
  );
}

