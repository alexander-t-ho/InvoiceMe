import { z } from "zod";

export const addLineItemSchema = z.object({
  itemId: z.string().uuid().optional().nullable(),
  description: z.string().min(1, "Description is required").max(500, "Description must not exceed 500 characters"),
  quantity: z.number().positive("Quantity must be greater than 0"),
  unitPrice: z.number().positive("Unit price must be greater than 0"),
});

export const createInvoiceSchema = z.object({
  customerId: z.string().uuid("Invalid customer ID"),
  issueDate: z.string().min(1, "Issue date is required"),
  dueDate: z.string().min(1, "Due date is required"),
  paymentPlan: z.enum(["FULL", "PAY_IN_4"]).optional(),
  lineItems: z.array(addLineItemSchema).optional().default([]),
}).refine(
  (data) => {
    const issueDate = new Date(data.issueDate);
    const dueDate = new Date(data.dueDate);
    return dueDate > issueDate;
  },
  {
    message: "Due date must be after issue date",
    path: ["dueDate"],
  }
);

export const updateInvoiceSchema = z.object({
  issueDate: z.string().min(1, "Issue date is required"),
  dueDate: z.string().min(1, "Due date is required"),
}).refine(
  (data) => {
    const issueDate = new Date(data.issueDate);
    const dueDate = new Date(data.dueDate);
    return dueDate > issueDate;
  },
  {
    message: "Due date must be after issue date",
    path: ["dueDate"],
  }
);

export type CreateInvoiceFormData = z.infer<typeof createInvoiceSchema>;
export type UpdateInvoiceFormData = z.infer<typeof updateInvoiceSchema>;
export type AddLineItemFormData = z.infer<typeof addLineItemSchema>;

