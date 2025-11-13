// API Request/Response Types

export interface CustomerResponse {
  id: string;
  name: string;
  email: string;
  address: string | null;
  createdAt: string;
  updatedAt: string;
}

export interface CreateCustomerRequest {
  name: string;
  email: string;
  address?: string | null;
}

export interface UpdateCustomerRequest {
  name: string;
  email: string;
  address?: string | null;
}

export interface PagedResponse<T> {
  content: T[];
  page: number;
  size: number;
  totalElements: number;
  totalPages: number;
  hasNext: boolean;
  hasPrevious: boolean;
}

export type InvoiceStatus = "DRAFT" | "SENT" | "PAID";

export interface LineItemResponse {
  id: string;
  description: string;
  quantity: number;
  unitPrice: number;
  total: number;
}

export interface PaymentResponse {
  id: string;
  amount: number;
  paymentDate: string;
  paymentMethod: string;
  createdAt: string;
}

export interface InvoiceResponse {
  id: string;
  customerId: string;
  customerName: string;
  status: InvoiceStatus;
  issueDate: string;
  dueDate: string;
  paymentPlan: PaymentPlan;
  discountCode: string | null;
  discountAmount: number;
  subtotal: number;
  totalAmount: number;
  balance: number;
  lineItems: LineItemResponse[];
  payments: PaymentResponse[];
  createdAt: string;
  updatedAt: string;
}

export interface PaymentScheduleResponse {
  id: string;
  invoiceId: string;
  installmentNumber: number;
  amount: number;
  dueDate: string;
  status: "PENDING" | "PAID" | "OVERDUE";
  createdAt: string;
}

export interface InvoiceSummaryResponse {
  id: string;
  customerId: string;
  customerName: string;
  status: InvoiceStatus;
  issueDate: string;
  dueDate: string;
  totalAmount: number;
  balance: number;
  createdAt?: string; // Optional until backend is updated
}

export type PaymentPlan = "FULL" | "PAY_IN_4";

export interface CreateInvoiceRequest {
  customerId: string;
  issueDate: string;
  dueDate: string;
  paymentPlan?: PaymentPlan;
}

export interface UpdateInvoiceRequest {
  issueDate: string;
  dueDate: string;
}

export interface AddLineItemRequest {
  itemId?: string | null;
  description: string;
  quantity: number;
  unitPrice: number;
}

export interface PaymentDetailResponse {
  id: string;
  invoiceId: string;
  invoiceNumber: string;
  amount: number;
  paymentDate: string;
  paymentMethod: string;
  createdAt: string;
}

export interface RecordPaymentRequest {
  invoiceId: string;
  amount: number;
  paymentDate: string;
  paymentMethod: string;
}

export interface ErrorResponse {
  timestamp: string;
  status: number;
  error: string;
  message: string;
  path: string;
  errors?: Record<string, string>;
}

export interface ItemResponse {
  id: string;
  userId: string;
  description: string;
  unitPrice: number;
  createdAt: string;
  updatedAt: string;
}

export interface CreateItemRequest {
  description: string;
  unitPrice: number;
}

export interface UpdateItemRequest {
  description: string;
  unitPrice: number;
}

