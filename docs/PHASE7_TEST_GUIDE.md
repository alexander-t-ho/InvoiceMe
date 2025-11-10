# Phase 7: Testing Guide

## Quick Start

### 1. Start Backend
```bash
cd backend
export JAVA_HOME=$(brew --prefix openjdk@17)
export PATH="$JAVA_HOME/bin:$PATH"
./gradlew bootRun
```

Wait for: `Started InvoiceMeApplication`

### 2. Start Frontend (in another terminal)
```bash
cd frontend
npm run dev
```

Wait for: `Ready in X.Xs` and `Local: http://localhost:3000`

### 3. Run API Tests (optional)
```bash
./test-api-endpoints.sh
```

This will create test data (customer, invoice, payment) for manual testing.

---

## Manual Testing Checklist

### ✅ Dashboard (http://localhost:3000)

- [ ] Dashboard loads without errors
- [ ] Customer count displays (or loading spinner)
- [ ] Invoice count displays (or loading spinner)
- [ ] Payments amount displays (or loading spinner)
- [ ] Revenue displays (or loading spinner)
- [ ] Navigation links work
- [ ] Quick action buttons work

---

### ✅ Customer Management

#### Create Customer
1. Navigate to `/customers`
2. Click "New Customer"
3. Fill in form:
   - Name: "John Doe"
   - Email: "john@example.com"
   - Address: "123 Main St" (optional)
4. Click "Create Customer"
5. **Expected**: Redirects to customer list, toast shows success

#### List Customers
1. Navigate to `/customers`
2. **Expected**: 
   - Table shows all customers
   - Pagination controls work
   - Edit and Delete buttons visible

#### Edit Customer
1. Click "Edit" button on a customer
2. Modify name or email
3. Click "Update Customer"
4. **Expected**: Redirects to customer list, toast shows success

#### Delete Customer
1. Click "Delete" button on a customer
2. Confirm deletion in dialog
3. **Expected**: Customer removed, toast shows success

---

### ✅ Invoice Management

#### Create Invoice
1. Navigate to `/invoices`
2. Click "New Invoice"
3. Fill in form:
   - Select a customer from dropdown
   - Issue Date: Today
   - Due Date: 30 days from today
4. Click "Create Invoice"
5. **Expected**: Redirects to invoice detail page

#### Add Line Items
1. On invoice detail page, click "Add Item"
2. Fill in:
   - Description: "Web Development"
   - Quantity: 10
   - Unit Price: 100.00
3. Click "Add Item"
4. **Expected**: Line item appears in table, total updates

#### Remove Line Item
1. Click trash icon on a line item
2. **Expected**: Line item removed, total updates

#### Mark Invoice as Sent
1. Ensure invoice has at least one line item
2. Click "Mark as Sent" button
3. **Expected**: 
   - Status changes to "SENT"
   - Button disappears
   - Toast shows success

#### Filter Invoices
1. Navigate to `/invoices`
2. Use status filter dropdown
3. Select "Draft", "Sent", or "Paid"
4. **Expected**: Table updates to show filtered invoices

---

### ✅ Payment Management

#### Record Payment
1. Navigate to an invoice with status "SENT" or "PAID"
2. Click "Record Payment" button
3. Fill in form:
   - Amount: 100.00
   - Payment Date: Today
   - Payment Method: "BANK_TRANSFER"
4. Click "Record Payment"
5. **Expected**: 
   - Payment appears in payment history
   - Balance updates
   - Toast shows success

#### View Payments
1. Navigate to `/payments`
2. **Expected**: 
   - Cards show invoices with payments
   - Payment amounts displayed
   - Links to invoice details work

---

## Error Testing

### Form Validation
- [ ] Try to create customer without name → Shows error
- [ ] Try to create customer with invalid email → Shows error
- [ ] Try to create invoice without customer → Shows error
- [ ] Try to add line item with negative quantity → Shows error

### Business Rules
- [ ] Try to mark invoice as sent without line items → Shows error
- [ ] Try to edit invoice that's already sent → Edit disabled
- [ ] Try to record payment for draft invoice → Button not visible

### API Errors
- [ ] Stop backend server
- [ ] Try to load customers → Shows error message
- [ ] Try to create customer → Shows error toast

---

## Browser Console Checks

Open browser DevTools (F12) and check:

- [ ] No console errors
- [ ] Network requests succeed (200 status)
- [ ] React Query cache working (no unnecessary refetches)
- [ ] Toast notifications appear

---

## Performance Checks

- [ ] Page loads quickly (< 2 seconds)
- [ ] Navigation is smooth
- [ ] Forms respond immediately
- [ ] No lag when typing in inputs
- [ ] Pagination loads quickly

---

## Responsive Design

Test on different screen sizes:

- [ ] Mobile (< 768px): Layout stacks vertically
- [ ] Tablet (768px - 1024px): 2-column layout
- [ ] Desktop (> 1024px): Full layout

---

## Test Data

After running `./test-api-endpoints.sh`, you'll have:
- 1 test customer
- 1 test invoice (with line item, marked as sent)
- 1 test payment

You can use this data to test the frontend features.

---

## Common Issues & Solutions

### Backend not starting
- Check Java 17 is installed: `java -version`
- Check port 8080 is not in use: `lsof -i :8080`

### Frontend not starting
- Check Node.js is installed: `node -version`
- Check dependencies: `cd frontend && npm install`

### API connection errors
- Verify backend is running: `curl http://localhost:8080/actuator/health`
- Check CORS settings in backend
- Check API_BASE_URL in frontend

### Forms not submitting
- Check browser console for errors
- Verify validation schemas
- Check network tab for API calls

---

## Success Criteria

✅ All CRUD operations work
✅ Forms validate correctly
✅ Business rules enforced
✅ Error handling works
✅ Loading states display
✅ Toast notifications appear
✅ Navigation works
✅ Dashboard shows real data
✅ No console errors
✅ Responsive design works

---

**Happy Testing! 🎉**

