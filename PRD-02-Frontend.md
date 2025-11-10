# Frontend PRD: InvoiceMe — Frontend Implementation

## Document Purpose

This PRD details the frontend implementation for InvoiceMe, covering UI architecture, component development, and user experience. This document focuses on Phases 6-7 and Phase 8 (frontend portion).

**Related Documents**: 
- PRD-00-Master.md (overview and architecture)
- PRD-01-Backend.md (API endpoints and contracts)
- PRD-03-Testing.md (testing requirements)

---

## Tech Stack

- **Framework**: Next.js 14+ (App Router) with TypeScript
- **State Management**: 
  - React Context API (for auth state)
  - React Query (TanStack Query) for server state
- **UI Library**: Tailwind CSS + shadcn/ui (or Radix UI primitives)
- **Form Handling**: React Hook Form + Zod validation
- **HTTP Client**: Axios or native fetch with typed API client
- **Routing**: Next.js App Router
- **Testing**: Vitest + React Testing Library

---

## Phase 6: Frontend Foundation & MVVM Architecture

**Duration**: 2-3 days  
**Goal**: Set up frontend architecture following MVVM principles

### Project Structure

```
frontend/
├── app/                          # Next.js App Router
│   ├── layout.tsx               # Root layout
│   ├── page.tsx                 # Home/Dashboard
│   ├── login/
│   │   └── page.tsx             # Login page
│   ├── customers/
│   │   ├── page.tsx             # Customer list
│   │   ├── [id]/
│   │   │   └── page.tsx         # Customer detail/edit
│   │   └── new/
│   │       └── page.tsx         # Create customer
│   ├── invoices/
│   │   ├── page.tsx             # Invoice list
│   │   ├── [id]/
│   │   │   └── page.tsx         # Invoice detail
│   │   └── new/
│   │       └── page.tsx         # Create invoice
│   └── payments/
│       └── page.tsx             # Payment list
├── components/                   # React components
│   ├── ui/                      # shadcn/ui components
│   ├── customers/               # Customer-specific components
│   ├── invoices/                # Invoice-specific components
│   └── payments/                # Payment-specific components
├── lib/                         # Utilities and services
│   ├── api/                     # API client
│   │   ├── client.ts            # Axios instance
│   │   ├── customers.ts         # Customer API calls
│   │   ├── invoices.ts          # Invoice API calls
│   │   └── payments.ts          # Payment API calls
│   ├── services/                # ViewModels/Services (MVVM)
│   │   ├── CustomerService.ts
│   │   ├── InvoiceService.ts
│   │   └── PaymentService.ts
│   └── utils/                   # Utility functions
├── types/                       # TypeScript types
│   ├── api.ts                   # API request/response types
│   ├── domain.ts                # Domain types
│   └── index.ts
├── hooks/                       # Custom React hooks
│   ├── useAuth.ts               # Authentication hook
│   └── useCustomers.ts          # Customer data hook
└── contexts/                    # React contexts
    └── AuthContext.tsx          # Authentication context
```

---

### Next.js Configuration

**File**: `next.config.js`

- Configure API base URL (environment variable)
- Configure image optimization (if needed)
- Set up TypeScript strict mode

**Environment Variables**: `.env.local`
```
NEXT_PUBLIC_API_URL=http://localhost:8080/api/v1
```

---

### UI Foundation Setup

#### Tailwind CSS Configuration
**File**: `tailwind.config.js`

- Configure theme colors
- Set up custom spacing and typography
- Configure dark mode (optional)

#### shadcn/ui Setup
- Install shadcn/ui components as needed
- Configure component path (`components/ui/`)
- Set up theme configuration

#### Base UI Components
Create reusable components in `components/ui/`:
- `Button` - Primary, secondary, destructive variants
- `Input` - Text input with validation styling
- `Select` - Dropdown select
- `Table` - Data table with sorting
- `Modal/Dialog` - Modal dialogs
- `Card` - Card container
- `Badge` - Status badges (Draft, Sent, Paid)
- `LoadingSpinner` - Loading indicator
- `ErrorBoundary` - Error boundary component
- `Toast/Alert` - Toast notifications

---

### API Client Setup

**File**: `lib/api/client.ts`

**Responsibilities**:
- Create Axios instance with base URL
- Configure request interceptors (add auth token)
- Configure response interceptors (handle errors)
- Handle 401 errors (redirect to login)

**Example Structure**:
```typescript
const apiClient = axios.create({
  baseURL: process.env.NEXT_PUBLIC_API_URL,
  headers: {
    'Content-Type': 'application/json',
  },
});

// Request interceptor: Add auth token
apiClient.interceptors.request.use((config) => {
  const token = getAuthToken();
  if (token) {
    config.headers.Authorization = `Bearer ${token}`;
  }
  return config;
});

// Response interceptor: Handle errors
apiClient.interceptors.response.use(
  (response) => response,
  (error) => {
    if (error.response?.status === 401) {
      // Redirect to login
    }
    return Promise.reject(error);
  }
);
```

---

### ViewModels/Services Layer (MVVM)

#### CustomerService
**File**: `lib/services/CustomerService.ts`

**Methods**:
- `getAllCustomers(page, size)` - Fetch all customers
- `getCustomerById(id)` - Fetch customer by ID
- `createCustomer(data)` - Create new customer
- `updateCustomer(id, data)` - Update customer
- `deleteCustomer(id)` - Delete customer

**Implementation**: Uses API client, returns typed responses

#### InvoiceService
**File**: `lib/services/InvoiceService.ts`

**Methods**:
- `getAllInvoices(filters, page, size)` - Fetch invoices with filters
- `getInvoiceById(id)` - Fetch invoice by ID
- `createInvoice(data)` - Create new invoice (Draft)
- `updateInvoice(id, data)` - Update invoice
- `markInvoiceAsSent(id)` - Mark invoice as sent
- `addLineItem(invoiceId, lineItem)` - Add line item to invoice
- `removeLineItem(invoiceId, lineItemId)` - Remove line item
- `getInvoicesByStatus(status, page, size)` - Filter by status
- `getInvoicesByCustomer(customerId, page, size)` - Filter by customer

#### PaymentService
**File**: `lib/services/PaymentService.ts`

**Methods**:
- `recordPayment(data)` - Record new payment
- `getPaymentById(id)` - Fetch payment by ID
- `getPaymentsByInvoice(invoiceId)` - Fetch payments for invoice

---

### React Query Setup

**File**: `lib/providers/QueryProvider.tsx`

- Wrap app with QueryClientProvider
- Configure default query options (staleTime, cacheTime)
- Set up global error handling

**Custom Hooks** (using React Query):
- `useCustomers()` - Customer data fetching
- `useCustomer(id)` - Single customer
- `useInvoices(filters)` - Invoice list
- `useInvoice(id)` - Single invoice
- `usePayments(invoiceId)` - Payments for invoice

---

### Routing Structure

**Routes**:
- `/` - Dashboard/Landing page (redirects to invoices or shows summary)
- `/login` - Login page
- `/customers` - Customer list page
- `/customers/new` - Create customer page
- `/customers/[id]` - Customer detail/edit page
- `/invoices` - Invoice list page
- `/invoices/new` - Create invoice page
- `/invoices/[id]` - Invoice detail page
- `/payments` - Payment list page (optional)

---

### Success Criteria

- ✅ Frontend connects to backend API successfully
- ✅ Basic UI components render correctly
- ✅ Navigation between routes works
- ✅ Error handling displays user-friendly messages
- ✅ API client properly handles authentication
- ✅ React Query is configured and working

---

## Phase 7: Frontend Feature Implementation

**Duration**: 4-5 days  
**Goal**: Build complete UI for all business operations

---

### Customer Management UI

#### Customer List Page
**File**: `app/customers/page.tsx`

**Features**:
- Table displaying: Name, Email, Address, Created Date
- "Create Customer" button (navigates to `/customers/new`)
- Edit button for each row (navigates to `/customers/[id]`)
- Delete button with confirmation dialog
- Pagination controls
- Search/filter functionality (optional)

**Components**:
- `CustomerTable` - Table component
- `CustomerRow` - Table row component
- `DeleteCustomerDialog` - Confirmation dialog

**State Management**:
- Use `useCustomers()` hook (React Query)
- Mutations for create/update/delete

---

#### Create Customer Page
**File**: `app/customers/new/page.tsx`

**Features**:
- Form with fields: Name, Email, Address
- Client-side validation (Zod schema)
- Submit button (creates customer, redirects to list)
- Cancel button (navigates back to list)
- Loading state during submission
- Error display for validation/API errors

**Components**:
- `CustomerForm` - Reusable form component
- Uses React Hook Form + Zod validation

---

#### Edit Customer Page
**File**: `app/customers/[id]/page.tsx`

**Features**:
- Pre-populated form with existing customer data
- Same validation as create form
- Update button (updates customer, redirects to list)
- Cancel button (navigates back to list)
- Loading state during fetch and submission

**Components**:
- Reuses `CustomerForm` component

---

### Invoice Management UI

#### Invoice List Page
**File**: `app/invoices/page.tsx`

**Features**:
- Table displaying: Invoice #, Customer, Status, Total, Balance, Issue Date
- Status badges (Draft, Sent, Paid) with color coding
- Filter by status (dropdown: All, Draft, Sent, Paid)
- Filter by customer (dropdown with customer list)
- "Create Invoice" button
- View/Edit button for each row
- Pagination controls

**Components**:
- `InvoiceTable` - Table component
- `InvoiceStatusBadge` - Status badge component
- `InvoiceFilters` - Filter controls

**State Management**:
- Use `useInvoices()` hook with filters
- Real-time filter updates

---

#### Create Invoice Page
**File**: `app/invoices/new/page.tsx`

**Features**:
- **Step 1: Invoice Details**
  - Customer selection (dropdown with search)
  - Issue Date (date picker)
  - Due Date (date picker, optional)
- **Step 2: Line Items**
  - Dynamic table for line items
  - Add row button
  - Remove row button for each line item
  - Fields per line item: Description, Quantity, Unit Price
  - Auto-calculate Total per line (Quantity × Unit Price)
  - Auto-calculate Invoice Total (sum of all line totals)
  - Real-time total updates
- **Step 3: Review**
  - Display invoice summary
  - Display line items table
  - Submit button (creates invoice, redirects to detail page)
- Form validation:
  - Customer is required
  - At least one line item is required
  - All line items must have description, quantity > 0, unit price >= 0

**Components**:
- `InvoiceForm` - Main form component
- `LineItemsTable` - Dynamic line items table
- `LineItemRow` - Single line item row
- `InvoiceSummary` - Summary display

**State Management**:
- Local state for form data
- Mutation for invoice creation

---

#### Invoice Detail Page
**File**: `app/invoices/[id]/page.tsx`

**Features**:
- **Invoice Header Section**:
  - Invoice number/ID
  - Customer name (link to customer detail)
  - Status badge
  - Issue Date, Due Date
  - Total Amount, Remaining Balance
- **Line Items Section**:
  - Table displaying all line items
  - Columns: Description, Quantity, Unit Price, Total
- **Payments Section**:
  - Table displaying all payments
  - Columns: Date, Amount, Payment Method
  - "Record Payment" button (opens modal)
- **Actions**:
  - "Mark as Sent" button (only if status is Draft)
  - "Edit Invoice" button (only if status is Draft, navigates to edit page)
  - "Delete Invoice" button (only if status is Draft, with confirmation)
- Real-time balance calculation display

**Components**:
- `InvoiceHeader` - Header section
- `InvoiceLineItems` - Line items table
- `InvoicePayments` - Payments table
- `RecordPaymentModal` - Modal for recording payment
- `MarkAsSentButton` - Action button

**State Management**:
- Use `useInvoice(id)` hook
- Use `usePayments(invoiceId)` hook
- Mutations for mark as sent, record payment

---

#### Edit Invoice Page (Optional)
**File**: `app/invoices/[id]/edit/page.tsx`

**Features**:
- Similar to create page, but pre-populated
- Only accessible if invoice status is Draft
- Can update issue date, due date, and line items
- Save button (updates invoice, redirects to detail)

---

### Payment Management UI

#### Record Payment Modal/Page
**Location**: Modal on invoice detail page, or separate page

**Features**:
- Form with fields:
  - Invoice ID (pre-filled if from invoice page)
  - Amount (required, must be <= invoice balance)
  - Payment Date (date picker, default: today)
  - Payment Method (dropdown: Cash, Credit Card, Bank Transfer, etc.)
- Validation:
  - Amount > 0
  - Amount <= invoice remaining balance
  - Payment date not in future
- Submit button (records payment, closes modal, refreshes invoice data)
- Cancel button

**Components**:
- `RecordPaymentForm` - Payment form component
- `RecordPaymentModal` - Modal wrapper (if using modal)

---

#### Payment List Page (Optional)
**File**: `app/payments/page.tsx`

**Features**:
- Table displaying: Payment #, Invoice #, Customer, Amount, Date, Method
- Filter by invoice or customer
- Link to invoice detail

---

### Form Validation

**Implementation**: React Hook Form + Zod

**Zod Schemas** (in `lib/validations/`):
- `customerSchema` - Customer form validation
- `invoiceSchema` - Invoice form validation
- `lineItemSchema` - Line item validation
- `paymentSchema` - Payment form validation

**Example**:
```typescript
const customerSchema = z.object({
  name: z.string().min(1, "Name is required"),
  email: z.string().email("Invalid email address"),
  address: z.string().optional(),
});
```

---

### Loading States & Optimistic Updates

**Loading States**:
- Show loading spinner during data fetching
- Disable buttons during mutations
- Show skeleton loaders for tables

**Optimistic Updates**:
- Update UI immediately on create/update/delete
- Revert on error
- Use React Query's optimistic update features

---

### Error Handling

**Error Display**:
- Toast notifications for success/error messages
- Inline error messages for form validation
- Error boundary for unexpected errors
- User-friendly error messages (map API errors)

**Error Messages**:
- "Failed to load customers. Please try again."
- "Invalid email address."
- "Payment amount exceeds invoice balance."
- "Invoice cannot be modified after being sent."

---

### Responsive Design

**Breakpoints**:
- Mobile: < 768px
- Tablet: 768px - 1024px
- Desktop: > 1024px

**Responsive Features**:
- Tables scroll horizontally on mobile
- Forms stack vertically on mobile
- Navigation menu collapses on mobile
- Touch-friendly button sizes

---

### Success Criteria

- ✅ All CRUD operations work from UI
- ✅ Invoice creation with multiple line items works
- ✅ Payment recording updates invoice balance correctly
- ✅ UI is responsive and user-friendly
- ✅ No noticeable lag in interactions
- ✅ Form validation works correctly
- ✅ Error messages are user-friendly
- ✅ Loading states are properly displayed

---

## Phase 8: Authentication & Authorization (Frontend)

**Duration**: 1-2 days (frontend portion)  
**Goal**: Implement authentication UI and protected routes

---

### Authentication Context

**File**: `contexts/AuthContext.tsx`

**Responsibilities**:
- Store authentication state (user, token)
- Provide login/logout functions
- Persist token in localStorage or httpOnly cookie
- Check if user is authenticated

**API**:
```typescript
interface AuthContextType {
  user: User | null;
  token: string | null;
  isAuthenticated: boolean;
  login: (username: string, password: string) => Promise<void>;
  logout: () => void;
}
```

---

### Login Page

**File**: `app/login/page.tsx`

**Features**:
- Form with fields: Username, Password
- Submit button (calls login API, stores token, redirects to dashboard)
- Error display for invalid credentials
- Loading state during login
- Redirect to dashboard if already authenticated

**Components**:
- `LoginForm` - Login form component

**API Integration**:
- POST `/api/v1/auth/login`
- Store JWT token in localStorage
- Update AuthContext

---

### Protected Route Wrapper

**File**: `components/ProtectedRoute.tsx` or middleware

**Responsibilities**:
- Check if user is authenticated
- Redirect to `/login` if not authenticated
- Render children if authenticated

**Implementation Options**:
1. Component wrapper (wrap each protected page)
2. Next.js middleware (automatic protection)

**Example** (Component wrapper):
```typescript
export function ProtectedRoute({ children }: { children: React.ReactNode }) {
  const { isAuthenticated } = useAuth();
  
  if (!isAuthenticated) {
    redirect('/login');
  }
  
  return <>{children}</>;
}
```

---

### Navigation Updates

**File**: `components/Navigation.tsx` or layout

**Features**:
- Show user name/username when authenticated
- Logout button (calls logout, redirects to login)
- Hide navigation on login page

---

### API Client Updates

**Update**: `lib/api/client.ts`

- Add auth token to all requests (already configured in Phase 6)
- Handle 401 responses (clear token, redirect to login)

---

### Success Criteria

- ✅ Unauthenticated users cannot access protected pages
- ✅ Login/logout flow works correctly
- ✅ API calls include authentication tokens
- ✅ Session persists across page refreshes
- ✅ Redirect to login on 401 errors

---

## UI/UX Guidelines

### Design Principles
- Clean, modern interface
- Consistent spacing and typography
- Clear visual hierarchy
- Accessible (WCAG 2.1 AA compliance)
- Intuitive navigation

### Color Scheme
- Primary: Blue (#3B82F6)
- Success: Green (#10B981)
- Warning: Yellow (#F59E0B)
- Error: Red (#EF4444)
- Status colors:
  - Draft: Gray
  - Sent: Blue
  - Paid: Green

### Typography
- Headings: Bold, clear hierarchy
- Body: Readable font size (16px minimum)
- Monospace: For IDs, numbers

---

## Testing Requirements

See **PRD-03-Testing.md** for detailed testing requirements. Frontend testing includes:

- Unit tests for components
- Integration tests for forms and user flows
- E2E tests for critical paths (optional)

---

**Document Version**: 1.0  
**Last Updated**: [Current Date]  
**Author**: Development Team


