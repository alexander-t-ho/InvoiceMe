"use client";

import { useState } from "react";
import {
  Dialog,
  DialogContent,
  DialogDescription,
  DialogFooter,
  DialogHeader,
  DialogTitle,
} from "@/components/ui/dialog";
import { Button } from "@/components/ui/button";
import { Input } from "@/components/ui/input";
import { Label } from "@/components/ui/label";
import { useItems } from "@/hooks/useItems";
import { LoadingSpinner } from "@/components/ui/loading-spinner";
import {
  Table,
  TableBody,
  TableCell,
  TableHead,
  TableHeader,
  TableRow,
} from "@/components/ui/table";
import type { ItemResponse } from "@/types/api";

interface ItemLibraryDialogProps {
  open: boolean;
  onOpenChange: (open: boolean) => void;
  onSelect: (item: ItemResponse) => void;
}

export function ItemLibraryDialog({
  open,
  onOpenChange,
  onSelect,
}: ItemLibraryDialogProps) {
  const [page, setPage] = useState(0);
  const { data, isLoading } = useItems(page, 10);

  const handleSelect = (item: ItemResponse) => {
    onSelect(item);
    onOpenChange(false);
  };

  return (
    <Dialog open={open} onOpenChange={onOpenChange}>
      <DialogContent className="max-w-2xl">
        <DialogHeader>
          <DialogTitle>Select from Item Library</DialogTitle>
          <DialogDescription>
            Choose an item to add to your invoice. The description and unit price will be automatically filled.
          </DialogDescription>
        </DialogHeader>

        {isLoading ? (
          <div className="flex items-center justify-center py-8">
            <LoadingSpinner size="lg" />
          </div>
        ) : data && data.content.length > 0 ? (
          <>
            <div className="max-h-[400px] overflow-y-auto">
              <Table>
                <TableHeader>
                  <TableRow>
                    <TableHead>Description</TableHead>
                    <TableHead className="text-right">Unit Price</TableHead>
                    <TableHead className="text-right">Action</TableHead>
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
                        <Button
                          variant="outline"
                          size="sm"
                          onClick={() => handleSelect(item)}
                        >
                          Select
                        </Button>
                      </TableCell>
                    </TableRow>
                  ))}
                </TableBody>
              </Table>
            </div>

            {data.totalPages > 1 && (
              <div className="flex items-center justify-between">
                <div className="text-sm text-muted-foreground">
                  Page {page + 1} of {data.totalPages}
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
            )}
          </>
        ) : (
          <div className="text-center py-8">
            <p className="text-muted-foreground">No items in library</p>
            <p className="text-sm text-muted-foreground mt-2">
              Create items in the Item Library page to use them here.
            </p>
          </div>
        )}

        <DialogFooter>
          <Button variant="outline" onClick={() => onOpenChange(false)}>
            Cancel
          </Button>
        </DialogFooter>
      </DialogContent>
    </Dialog>
  );
}



