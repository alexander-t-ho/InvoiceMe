# Phase 7: Testing Instructions

## Quick Start

### 1. Start Backend Server

```bash
cd backend
export JAVA_HOME=$(brew --prefix openjdk@17)
export PATH="$JAVA_HOME/bin:$PATH"
./gradlew bootRun
```

**Wait for**: `Started InvoiceMeApplication in X.XXX seconds`

### 2. Start Frontend Server (in a new terminal)

```bash
cd frontend
npm run dev
```

**Wait for**: `Ready in X.Xs` and `Local: http://localhost:3000`

### 3. Open Browser

Navigate to: **http://localhost:3001**

---

## Automated API Tests (Optional)

Run the automated API test script to create test data:

```bash
./test-api-endpoints.sh
```

This will:
- Create a test customer
- Create a test invoice
- Add a line item
- Mark invoice as sent
- Record a payment

You can then use this data to test the frontend.

---

## Manual Testing Workflow

### Step 1: Test Dashboard

1. Open http://localhost:3001
2. **Verify**:
   - ✅ Dashboard loads
   - ✅ Customer count displays (or loading spinner)
   - ✅ Invoice count displays
   - ✅ Payments amount displays
   - ✅ Revenue displays
   - ✅ Navigation bar visible
   - ✅ No console errors

### Step 2: Create a Customer

1. Click "Customers" in navigation
2. Click "New Customer" button
3. Fill in:
   - Name: "Acme Corporation"
   - Email: "contact@acme.com"
   - Address: "123 Business St, City, State"
4. Click "Create Customer"
5. **Verify**:
   - ✅ Redirects to customer list
   - ✅ Toast notification shows success
   - ✅ New customer appears in list

### Step 3: Create an Invoice

1. Click "Invoices" in navigation
2. Click "New Invoice" button
3. Fill in:
   - Customer: Select "Acme Corporation"
   - Issue Date: Today's date
   - Due Date: 30 days from today
4. Click "Create Invoice"
5. **Verify**:
   - ✅ Redirects to invoice detail page
   - ✅ Invoice status is "DRAFT"
   - ✅ Can add line items

### Step 4: Add Line Items

1. On invoice detail page, click "Add Item"
2. Fill in:
   - Description: "Web Development Services"
   - Quantity: 10
   - Unit Price: 150.00
3. Click "Add Item"
4. **Verify**:
   - ✅ Line item appears in table
   - ✅ Total amount updates to $1,500.00
   - ✅ Can add more items

5. Add another line item:
   - Description: "Consulting"
   - Quantity: 5
   - Unit Price: 200.00
6. **Verify**:
   - ✅ Total updates to $2,500.00

### Step 5: Mark Invoice as Sent

1. Click "Mark as Sent" button
2. **Verify**:
   - ✅ Status changes to "SENT"
   - ✅ "Mark as Sent" button disappears
   - ✅ "Record Payment" button appears
   - ✅ Toast notification shows success

### Step 6: Record a Payment

1. Click "Record Payment" button
2. Fill in:
   - Amount: 1000.00
   - Payment Date: Today
   - Payment Method: "BANK_TRANSFER"
3. Click "Record Payment"
4. **Verify**:
   - ✅ Payment appears in payment history table
   - ✅ Balance updates (from $2,500.00 to $1,500.00)
   - ✅ Toast notification shows success

### Step 7: Test Invoice List

1. Click "Invoices" in navigation
2. **Verify**:
   - ✅ Invoice appears in list
   - ✅ Status badge shows "SENT"
   - ✅ Total and balance displayed correctly
   - ✅ Can filter by status

3. Test status filter:
   - Select "Sent" from dropdown
   - **Verify**: Only sent invoices shown

### Step 8: Test Customer List

1. Click "Customers" in navigation
2. **Verify**:
   - ✅ Customer appears in list
   - ✅ Can edit customer
   - ✅ Can delete customer

3. Test edit:
   - Click "Edit" button
   - Change email
   - Click "Update Customer"
   - **Verify**: Changes saved

### Step 9: Test Payments Page

1. Click "Payments" in navigation
2. **Verify**:
   - ✅ Invoice with payment appears
   - ✅ Payment amount displayed
   - ✅ Can click to view invoice

### Step 10: Test Error Handling

1. **Form Validation**:
   - Try to create customer without name
   - **Verify**: Error message appears

2. **Business Rules**:
   - Try to mark invoice as sent without line items
   - **Verify**: Error message appears

3. **API Errors**:
   - Stop backend server
   - Try to load customers
   - **Verify**: Error message displayed

---

## Browser Console Checks

Open DevTools (F12) and verify:

- ✅ No red errors in console
- ✅ Network requests succeed (200 status)
- ✅ React Query cache working
- ✅ Toast notifications appear

---

## Success Criteria

All of these should work:

- ✅ Dashboard displays real data
- ✅ Create customer works
- ✅ Edit customer works
- ✅ Delete customer works
- ✅ Create invoice works
- ✅ Add line items works
- ✅ Remove line items works
- ✅ Mark as sent works
- ✅ Record payment works
- ✅ Filter invoices works
- ✅ View payments works
- ✅ Form validation works
- ✅ Error handling works
- ✅ Loading states work
- ✅ Toast notifications work
- ✅ Navigation works
- ✅ No console errors

---

## Troubleshooting

### Backend won't start
- Check Java 17: `java -version`
- Check port 8080: `lsof -i :8080`
- Check logs: `./gradlew bootRun`

### Frontend won't start
- Check Node.js: `node -version`
- Install dependencies: `cd frontend && npm install`
- Check port 3000: `lsof -i :3000`

### API connection errors
- Verify backend running: `curl http://localhost:8080/actuator/health`
- Check browser console for CORS errors
- Verify API URL in frontend code

### Forms not working
- Check browser console for errors
- Verify validation schemas
- Check network tab for API calls

---

## Test Data Summary

After running `./test-api-endpoints.sh`, you'll have:

- **Customer**: "Test Customer" (test@example.com)
- **Invoice**: With 1 line item, marked as SENT
- **Payment**: $150.00 recorded

You can use this data or create your own.

---

**Happy Testing! 🎉**

If you encounter any issues, check the browser console and network tab for details.

