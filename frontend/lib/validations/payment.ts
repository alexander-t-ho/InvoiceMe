import { z } from "zod";

export const recordPaymentSchema = z.object({
  invoiceId: z.string().uuid("Invalid invoice ID"),
  amount: z.number().positive("Amount must be greater than 0"),
  paymentDate: z.string().min(1, "Payment date is required"),
  paymentMethod: z.string().min(1, "Payment method is required"),
});

export type RecordPaymentFormData = z.infer<typeof recordPaymentSchema>;

