"use client";

import { useState } from "react";
import { ProtectedRoute } from "@/components/auth/protected-route";
import { MainLayout } from "@/components/layout/main-layout";
import { useCustomers } from "@/hooks/useCustomers";
import { useDeleteCustomer } from "@/hooks/useCustomers";
import { Button } from "@/components/ui/button";
import {
  Table,
  TableBody,
  TableCell,
  TableHead,
  TableHeader,
  TableRow,
} from "@/components/ui/table";
import { Badge } from "@/components/ui/badge";
import { LoadingSpinner } from "@/components/ui/loading-spinner";
import Link from "next/link";
import { Plus, Edit, Trash2 } from "lucide-react";
import {
  Dialog,
  DialogContent,
  DialogDescription,
  DialogFooter,
  DialogHeader,
  DialogTitle,
} from "@/components/ui/dialog";
import { useRouter } from "next/navigation";

export default function CustomersPage() {
  const [page, setPage] = useState(0);
  const [deleteDialogOpen, setDeleteDialogOpen] = useState(false);
  const [customerToDelete, setCustomerToDelete] = useState<string | null>(null);
  const router = useRouter();

  const { data, isLoading, error } = useCustomers(page, 20, "name");
  const deleteCustomer = useDeleteCustomer();

  const handleDelete = (id: string) => {
    setCustomerToDelete(id);
    setDeleteDialogOpen(true);
  };

  const confirmDelete = () => {
    if (customerToDelete) {
      deleteCustomer.mutate(customerToDelete, {
        onSuccess: () => {
          setDeleteDialogOpen(false);
          setCustomerToDelete(null);
        },
      });
    }
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

  if (error) {
    return (
      <MainLayout>
        <div className="flex items-center justify-center py-12">
          <div className="text-center">
            <p className="text-destructive">Error loading customers</p>
            <p className="text-sm text-muted-foreground mt-2">
              {error instanceof Error ? error.message : "Unknown error"}
            </p>
          </div>
        </div>
      </MainLayout>
    );
  }

  return (
    <ProtectedRoute>
      <MainLayout>
      <div className="space-y-6">
        <div className="flex items-center justify-between">
          <div>
            <h1 className="text-3xl font-bold tracking-tight">Customers</h1>
            <p className="text-muted-foreground">
              Manage your customers
            </p>
          </div>
          <Link href="/customers/new">
            <Button>
              <Plus className="mr-2 h-4 w-4" />
              New Customer
            </Button>
          </Link>
        </div>

        {data && data.content.length > 0 ? (
          <>
            <div className="rounded-md border">
              <Table>
                <TableHeader>
                  <TableRow>
                    <TableHead>Name</TableHead>
                    <TableHead>Email</TableHead>
                    <TableHead>Address</TableHead>
                    <TableHead className="text-right">Actions</TableHead>
                  </TableRow>
                </TableHeader>
                <TableBody>
                  {data.content.map((customer) => (
                    <TableRow key={customer.id}>
                      <TableCell className="font-medium">
                        {customer.name}
                      </TableCell>
                      <TableCell>{customer.email}</TableCell>
                      <TableCell>{customer.address || "-"}</TableCell>
                      <TableCell className="text-right">
                        <div className="flex justify-end space-x-2">
                          <Link href={`/customers/${customer.id}`}>
                            <Button variant="outline" size="sm">
                              <Edit className="h-4 w-4" />
                            </Button>
                          </Link>
                          <Button
                            variant="outline"
                            size="sm"
                            onClick={() => handleDelete(customer.id)}
                          >
                            <Trash2 className="h-4 w-4" />
                          </Button>
                        </div>
                      </TableCell>
                    </TableRow>
                  ))}
                </TableBody>
              </Table>
            </div>

            <div className="flex items-center justify-between">
              <div className="text-sm text-muted-foreground">
                Showing {data.content.length} of {data.totalElements} customers
              </div>
              <div className="flex space-x-2">
                <Button
                  variant="outline"
                  size="sm"
                  onClick={() => setPage((p) => Math.max(0, p - 1))}
                  disabled={!data.hasPrevious || page === 0}
                >
                  Previous
                </Button>
                <Button
                  variant="outline"
                  size="sm"
                  onClick={() => setPage((p) => p + 1)}
                  disabled={!data.hasNext}
                >
                  Next
                </Button>
              </div>
            </div>
          </>
        ) : (
          <div className="text-center py-12">
            <p className="text-muted-foreground">No customers found</p>
            <Link href="/customers/new" className="mt-4 inline-block">
              <Button>
                <Plus className="mr-2 h-4 w-4" />
                Create Your First Customer
              </Button>
            </Link>
          </div>
        )}

        <Dialog open={deleteDialogOpen} onOpenChange={setDeleteDialogOpen}>
          <DialogContent>
            <DialogHeader>
              <DialogTitle>Delete Customer</DialogTitle>
              <DialogDescription>
                Are you sure you want to delete this customer? This action cannot be undone.
              </DialogDescription>
            </DialogHeader>
            <DialogFooter>
              <Button
                variant="outline"
                onClick={() => setDeleteDialogOpen(false)}
              >
                Cancel
              </Button>
              <Button
                variant="destructive"
                onClick={confirmDelete}
                disabled={deleteCustomer.isPending}
              >
                {deleteCustomer.isPending && (
                  <LoadingSpinner size="sm" className="mr-2" />
                )}
                Delete
              </Button>
            </DialogFooter>
          </DialogContent>
        </Dialog>
      </div>
    </MainLayout>
    </ProtectedRoute>
  );
}

