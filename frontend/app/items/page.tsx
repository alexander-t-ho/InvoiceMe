"use client";

import { useState } from "react";
import { ProtectedRoute } from "@/components/auth/protected-route";
import { MainLayout } from "@/components/layout/main-layout";
import { useItems, useDeleteItem } from "@/hooks/useItems";
import { Button } from "@/components/ui/button";
import {
  Table,
  TableBody,
  TableCell,
  TableHead,
  TableHeader,
  TableRow,
} from "@/components/ui/table";
import { LoadingSpinner } from "@/components/ui/loading-spinner";
import { Plus, Edit, Trash2 } from "lucide-react";
import {
  Dialog,
  DialogContent,
  DialogDescription,
  DialogFooter,
  DialogHeader,
  DialogTitle,
} from "@/components/ui/dialog";
import { ItemForm } from "@/components/items/item-form";

export default function ItemsPage() {
  const [page, setPage] = useState(0);
  const [deleteDialogOpen, setDeleteDialogOpen] = useState(false);
  const [itemToDelete, setItemToDelete] = useState<string | null>(null);
  const [editDialogOpen, setEditDialogOpen] = useState(false);
  const [itemToEdit, setItemToEdit] = useState<{ id: string; description: string; unitPrice: number } | null>(null);
  const [createDialogOpen, setCreateDialogOpen] = useState(false);

  const { data, isLoading, error } = useItems(page, 20);
  const deleteItem = useDeleteItem();

  const handleDelete = (id: string) => {
    setItemToDelete(id);
    setDeleteDialogOpen(true);
  };

  const confirmDelete = () => {
    if (itemToDelete) {
      deleteItem.mutate(itemToDelete, {
        onSuccess: () => {
          setDeleteDialogOpen(false);
          setItemToDelete(null);
        },
      });
    }
  };

  const handleEdit = (id: string, description: string, unitPrice: number) => {
    setItemToEdit({ id, description, unitPrice });
    setEditDialogOpen(true);
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
            <p className="text-destructive">Error loading items</p>
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
              <h1 className="text-3xl font-bold tracking-tight">Item Library</h1>
              <p className="text-muted-foreground">
                Manage reusable items for your invoices
              </p>
            </div>
            <Button onClick={() => setCreateDialogOpen(true)}>
              <Plus className="mr-2 h-4 w-4" />
              New Item
            </Button>
          </div>

          {data && data.content.length > 0 ? (
            <>
              <div className="rounded-md border">
                <Table>
                  <TableHeader>
                    <TableRow>
                      <TableHead>Description</TableHead>
                      <TableHead className="text-right">Unit Price</TableHead>
                      <TableHead className="text-right">Actions</TableHead>
                    </TableRow>
                  </TableHeader>
                  <TableBody>
                    {data.content.map((item) => (
                      <TableRow key={item.id}>
                        <TableCell className="font-medium">
                          {item.description}
                        </TableCell>
                        <TableCell className="text-right">
                          ${item.unitPrice.toFixed(2)}
                        </TableCell>
                        <TableCell className="text-right">
                          <div className="flex justify-end space-x-2">
                            <Button
                              variant="outline"
                              size="sm"
                              onClick={() => handleEdit(item.id, item.description, item.unitPrice)}
                            >
                              <Edit className="h-4 w-4" />
                            </Button>
                            <Button
                              variant="outline"
                              size="sm"
                              onClick={() => handleDelete(item.id)}
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
                  Showing {data.content.length} of {data.totalElements} items
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
              <p className="text-muted-foreground">No items found</p>
              <Button
                onClick={() => setCreateDialogOpen(true)}
                className="mt-4"
              >
                <Plus className="mr-2 h-4 w-4" />
                Create Your First Item
              </Button>
            </div>
          )}

          <Dialog open={createDialogOpen} onOpenChange={setCreateDialogOpen}>
            <DialogContent>
              <DialogHeader>
                <DialogTitle>Create Item</DialogTitle>
                <DialogDescription>
                  Create a new reusable item for your invoices
                </DialogDescription>
              </DialogHeader>
              <ItemForm
                onSuccess={() => setCreateDialogOpen(false)}
                onCancel={() => setCreateDialogOpen(false)}
              />
            </DialogContent>
          </Dialog>

          <Dialog open={editDialogOpen} onOpenChange={setEditDialogOpen}>
            <DialogContent>
              <DialogHeader>
                <DialogTitle>Edit Item</DialogTitle>
                <DialogDescription>
                  Update item details
                </DialogDescription>
              </DialogHeader>
              {itemToEdit && (
                <ItemForm
                  itemId={itemToEdit.id}
                  initialDescription={itemToEdit.description}
                  initialUnitPrice={itemToEdit.unitPrice}
                  onSuccess={() => {
                    setEditDialogOpen(false);
                    setItemToEdit(null);
                  }}
                  onCancel={() => {
                    setEditDialogOpen(false);
                    setItemToEdit(null);
                  }}
                />
              )}
            </DialogContent>
          </Dialog>

          <Dialog open={deleteDialogOpen} onOpenChange={setDeleteDialogOpen}>
            <DialogContent>
              <DialogHeader>
                <DialogTitle>Delete Item</DialogTitle>
                <DialogDescription>
                  Are you sure you want to delete this item? This action cannot be undone.
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
                  disabled={deleteItem.isPending}
                >
                  {deleteItem.isPending && (
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



