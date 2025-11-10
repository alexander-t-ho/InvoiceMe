# Phase 7: Manual Testing Checklist

## 🚀 Servers Status

- **Backend**: http://localhost:8081
- **Frontend**: http://localhost:3001

---

## ✅ Test Checklist

### 1. Dashboard (http://localhost:3001)

- [ ] Page loads without errors
- [ ] Navigation bar visible with links (Dashboard, Customers, Invoices, Payments)
- [ ] Customer count card displays (number or loading spinner)
- [ ] Invoice count card displays (number or loading spinner)
- [ ] Payments amount card displays (amount or loading spinner)
- [ ] Revenue card displays (amount or loading spinner)
- [ ] Quick Actions section visible
- [ ] "Create New Customer" button works
- [ ] "Create New Invoice" button works
- [ ] No console errors (F12 → Console tab)

**Status**: ⏳ Not Started / ✅ Pass / ❌ Fail

**Notes**: 
_________________________________________________

---

### 2. Customer Management

#### 2.1 Create Customer
- [ ] Navigate to `/customers`
- [ ] Click "New Customer" button
- [ ] Form displays with fields: Name, Email, Address
- [ ] Fill in valid data:
  - Name: "Test Company"
  - Email: "test@example.com"
  - Address: "123 Test St"
- [ ] Click "Create Customer"
- [ ] Success toast notification appears
- [ ] Redirects to customer list
- [ ] New customer appears in list

**Status**: ⏳ Not Started / ✅ Pass / ❌ Fail

**Notes**: 
_________________________________________________

#### 2.2 List Customers
- [ ] Navigate to `/customers`
- [ ] Table displays with columns: Name, Email, Address, Actions
- [ ] Customers are listed correctly
- [ ] Pagination controls visible (if more than 20 customers)
- [ ] "Previous" and "Next" buttons work
- [ ] Edit button (pencil icon) visible for each customer
- [ ] Delete button (trash icon) visible for each customer

**Status**: ⏳ Not Started / ✅ Pass / ❌ Fail

**Notes**: 
_________________________________________________

#### 2.3 Edit Customer
- [ ] Click "Edit" button on a customer
- [ ] Form pre-populated with customer data
- [ ] Modify name or email
- [ ] Click "Update Customer"
- [ ] Success toast notification appears
- [ ] Redirects to customer list
- [ ] Changes are reflected in list

**Status**: ⏳ Not Started / ✅ Pass / ❌ Fail

**Notes**: 
_________________________________________________

#### 2.4 Delete Customer
- [ ] Click "Delete" button on a customer
- [ ] Confirmation dialog appears
- [ ] Dialog shows warning message
- [ ] Click "Cancel" → Dialog closes, customer still exists
- [ ] Click "Delete" → Customer removed
- [ ] Success toast notification appears
- [ ] Customer no longer in list

**Status**: ⏳ Not Started / ✅ Pass / ❌ Fail

**Notes**: 
_________________________________________________

#### 2.5 Form Validation (Customer)
- [ ] Try to submit form without name → Error message appears
- [ ] Try to submit with invalid email → Error message appears
- [ ] Try to submit with very long name (>255 chars) → Error message appears
- [ ] All error messages are clear and helpful

**Status**: ⏳ Not Started / ✅ Pass / ❌ Fail

**Notes**: 
_________________________________________________

---

### 3. Invoice Management

#### 3.1 Create Invoice
- [ ] Navigate to `/invoices`
- [ ] Click "New Invoice" button
- [ ] Form displays with fields: Customer, Issue Date, Due Date
- [ ] Customer dropdown shows list of customers
- [ ] Select a customer from dropdown
- [ ] Set Issue Date (today)
- [ ] Set Due Date (30 days from today)
- [ ] Click "Create Invoice"
- [ ] Success toast notification appears
- [ ] Redirects to invoice detail page
- [ ] Invoice status is "DRAFT"

**Status**: ⏳ Not Started / ✅ Pass / ❌ Fail

**Notes**: 
_________________________________________________

#### 3.2 List Invoices
- [ ] Navigate to `/invoices`
- [ ] Table displays with columns: Customer, Status, Issue Date, Due Date, Total, Balance, Actions
- [ ] Invoices are listed correctly
- [ ] Status badges display correctly (DRAFT, SENT, PAID)
- [ ] Dates formatted correctly
- [ ] Amounts formatted as currency ($X.XX)
- [ ] Status filter dropdown visible
- [ ] Pagination controls work

**Status**: ⏳ Not Started / ✅ Pass / ❌ Fail

**Notes**: 
_________________________________________________

#### 3.3 Filter Invoices by Status
- [ ] Select "Draft" from status filter → Only draft invoices shown
- [ ] Select "Sent" from status filter → Only sent invoices shown
- [ ] Select "Paid" from status filter → Only paid invoices shown
- [ ] Select "All Statuses" → All invoices shown
- [ ] Filter updates immediately

**Status**: ⏳ Not Started / ✅ Pass / ❌ Fail

**Notes**: 
_________________________________________________

#### 3.4 Add Line Items
- [ ] Navigate to invoice detail page (DRAFT invoice)
- [ ] Click "Add Item" button
- [ ] Dialog/modal opens with form
- [ ] Fill in:
  - Description: "Web Development"
  - Quantity: 10
  - Unit Price: 150.00
- [ ] Click "Add Item"
- [ ] Dialog closes
- [ ] Line item appears in table
- [ ] Total amount updates correctly (10 × 150 = $1,500.00)
- [ ] Can add multiple line items
- [ ] Total updates with each addition

**Status**: ⏳ Not Started / ✅ Pass / ❌ Fail

**Notes**: 
_________________________________________________

#### 3.5 Remove Line Items
- [ ] On invoice detail page with line items
- [ ] Click trash icon on a line item
- [ ] Line item removed immediately
- [ ] Total amount updates correctly
- [ ] Success toast notification appears

**Status**: ⏳ Not Started / ✅ Pass / ❌ Fail

**Notes**: 
_________________________________________________

#### 3.6 Mark Invoice as Sent
- [ ] On invoice detail page (DRAFT invoice with line items)
- [ ] "Mark as Sent" button visible
- [ ] Click "Mark as Sent"
- [ ] Success toast notification appears
- [ ] Status changes to "SENT"
- [ ] "Mark as Sent" button disappears
- [ ] "Record Payment" button appears
- [ ] Cannot add/remove line items anymore

**Status**: ⏳ Not Started / ✅ Pass / ❌ Fail

**Notes**: 
_________________________________________________

#### 3.7 View Invoice Details
- [ ] Navigate to invoice detail page
- [ ] Invoice information section shows:
  - Status badge
  - Customer name
  - Issue date
  - Due date
  - Total amount
  - Balance
- [ ] Line items table shows all items
- [ ] Payments section shows payment history (if any)
- [ ] All data is accurate

**Status**: ⏳ Not Started / ✅ Pass / ❌ Fail

**Notes**: 
_________________________________________________

#### 3.8 Business Rules - Invoice
- [ ] Try to mark invoice as sent without line items → Error message
- [ ] Try to edit line items on SENT invoice → Edit buttons not visible
- [ ] Try to add line items to SENT invoice → "Add Item" button not visible
- [ ] All business rules enforced correctly

**Status**: ⏳ Not Started / ✅ Pass / ❌ Fail

**Notes**: 
_________________________________________________

---

### 4. Payment Management

#### 4.1 Record Payment
- [ ] Navigate to invoice detail page (SENT or PAID invoice)
- [ ] Click "Record Payment" button
- [ ] Dialog/modal opens with form
- [ ] Form fields: Amount, Payment Date, Payment Method
- [ ] Fill in:
  - Amount: 500.00
  - Payment Date: Today
  - Payment Method: "BANK_TRANSFER"
- [ ] Click "Record Payment"
- [ ] Dialog closes
- [ ] Success toast notification appears
- [ ] Payment appears in payment history table
- [ ] Balance updates correctly (reduced by payment amount)
- [ ] If balance reaches 0, status changes to "PAID"

**Status**: ⏳ Not Started / ✅ Pass / ❌ Fail

**Notes**: 
_________________________________________________

#### 4.2 View Payment History
- [ ] On invoice detail page with payments
- [ ] Payments section visible
- [ ] Table shows: Date, Method, Amount
- [ ] All payments listed correctly
- [ ] Dates formatted correctly
- [ ] Amounts formatted as currency

**Status**: ⏳ Not Started / ✅ Pass / ❌ Fail

**Notes**: 
_________________________________________________

#### 4.3 Payments Page
- [ ] Navigate to `/payments`
- [ ] Page displays invoices with payments
- [ ] Cards show: Customer name, Status, Due date, Total, Balance, Paid amount
- [ ] "View Invoice" button works
- [ ] Links to invoice detail pages

**Status**: ⏳ Not Started / ✅ Pass / ❌ Fail

**Notes**: 
_________________________________________________

---

### 5. Error Handling

#### 5.1 Form Validation Errors
- [ ] Customer form: Empty name → Error message
- [ ] Customer form: Invalid email → Error message
- [ ] Invoice form: No customer selected → Error message
- [ ] Invoice form: Due date before issue date → Error message
- [ ] Line item form: Negative quantity → Error message
- [ ] Payment form: Negative amount → Error message
- [ ] All error messages are clear and helpful

**Status**: ⏳ Not Started / ✅ Pass / ❌ Fail

**Notes**: 
_________________________________________________

#### 5.2 API Error Handling
- [ ] Stop backend server
- [ ] Try to load customers → Error message displayed
- [ ] Try to create customer → Error toast appears
- [ ] Error messages are user-friendly
- [ ] Restart backend server
- [ ] Operations work again

**Status**: ⏳ Not Started / ✅ Pass / ❌ Fail

**Notes**: 
_________________________________________________

---

### 6. UI/UX

#### 6.1 Loading States
- [ ] Loading spinner shows when fetching data
- [ ] Buttons disabled during submission
- [ ] Forms show loading state during save
- [ ] No flickering or layout shifts

**Status**: ⏳ Not Started / ✅ Pass / ❌ Fail

**Notes**: 
_________________________________________________

#### 6.2 Toast Notifications
- [ ] Success toasts appear on create/update/delete
- [ ] Error toasts appear on failures
- [ ] Toasts auto-dismiss after a few seconds
- [ ] Toasts are visible and readable

**Status**: ⏳ Not Started / ✅ Pass / ❌ Fail

**Notes**: 
_________________________________________________

#### 6.3 Navigation
- [ ] All navigation links work
- [ ] Active route highlighted in navigation
- [ ] Back button works correctly
- [ ] Breadcrumbs or clear page context

**Status**: ⏳ Not Started / ✅ Pass / ❌ Fail

**Notes**: 
_________________________________________________

#### 6.4 Responsive Design
- [ ] Test on mobile viewport (< 768px)
- [ ] Test on tablet viewport (768px - 1024px)
- [ ] Test on desktop viewport (> 1024px)
- [ ] Layout adapts correctly
- [ ] Tables scroll horizontally on mobile
- [ ] Forms are usable on all sizes

**Status**: ⏳ Not Started / ✅ Pass / ❌ Fail

**Notes**: 
_________________________________________________

---

### 7. Browser Console

- [ ] Open DevTools (F12)
- [ ] Check Console tab
- [ ] No red errors
- [ ] No warnings (or only minor ones)
- [ ] Network requests succeed (200 status)
- [ ] React Query cache working

**Status**: ⏳ Not Started / ✅ Pass / ❌ Fail

**Notes**: 
_________________________________________________

---

## 🎯 Overall Test Results

**Total Tests**: __ / __

**Passed**: __
**Failed**: __
**Not Started**: __

**Overall Status**: ⏳ In Progress / ✅ Pass / ❌ Needs Fixes

---

## 📝 Issues Found

### Critical Issues
1. 
2. 
3. 

### High Priority Issues
1. 
2. 
3. 

### Medium Priority Issues
1. 
2. 
3. 

### Low Priority / Suggestions
1. 
2. 
3. 

---

## ✅ Sign-off

**Tester**: _________________

**Date**: _________________

**Comments**: 
_________________________________________________
_________________________________________________
_________________________________________________

---

**Happy Testing! 🎉**

