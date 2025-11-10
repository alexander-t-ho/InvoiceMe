import { z } from "zod";

export const createCustomerSchema = z.object({
  name: z.string().min(1, "Name is required").max(255, "Name must not exceed 255 characters"),
  email: z.string().email("Invalid email address").max(255, "Email must not exceed 255 characters"),
  address: z.string().max(500, "Address must not exceed 500 characters").optional().nullable(),
});

export const updateCustomerSchema = createCustomerSchema;

export type CreateCustomerFormData = z.infer<typeof createCustomerSchema>;
export type UpdateCustomerFormData = z.infer<typeof updateCustomerSchema>;

