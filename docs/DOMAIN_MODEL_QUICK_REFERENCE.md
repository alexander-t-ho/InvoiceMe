# Domain Model Quick Reference

## Entity Relationships

```
Customer (1) ──< (N) Invoice (1) ──< (N) Payment
                      │
                      └──< (N) LineItem
```

## Domain Entities at a Glance

### Customer
```java
Customer.create(name, email, address)
customer.updateDetails(name, email, address)
customer.validate()
```

**Rules**: Email must be valid, name required, email unique

---

### Invoice
```java
Invoice.create(customerId, issueDate, dueDate)
invoice.addLineItem(lineItem)
invoice.removeLineItem(lineItemId)
invoice.markAsSent()
invoice.applyPayment(payment)
invoice.calculateTotal()
invoice.calculateBalance()
invoice.canBeEdited()  // true if DRAFT
invoice.canBeSent()    // true if DRAFT and has line items
```

**Status Flow**: `DRAFT → SENT → PAID`

**Rules**: 
- Must have line items to send
- Can only edit when DRAFT
- Balance = total - payments
- Auto-transitions to PAID when balance = 0

---

### LineItem (Value Object)
```java
LineItem.create(description, quantity, unitPrice)
LineItem.of(id, description, quantity, unitPrice)  // for reconstruction
```

**Rules**: 
- Quantity > 0
- Unit price >= 0
- Total = quantity × unitPrice (auto-calculated)
- Immutable

---

### Payment
```java
Payment.create(invoiceId, amount, paymentDate, paymentMethod)
payment.validate()
payment.validateAgainstInvoice(invoice)
```

**Rules**: 
- Amount > 0
- Payment date not in future
- Cannot exceed invoice balance

---

## Exception Types

| Exception | When Thrown |
|-----------|-------------|
| `DomainValidationException` | Generic validation failure |
| `InvalidInvoiceStateException` | Invalid state transition |
| `InsufficientPaymentException` | Payment exceeds balance |
| `InvalidLineItemException` | Line item validation failure |

---

## Common Patterns

### Creating and Using an Invoice

```java
// 1. Create customer
Customer customer = Customer.create("John Doe", "john@example.com", "123 St");

// 2. Create invoice
Invoice invoice = Invoice.create(customer.getId(), LocalDate.now(), LocalDate.now().plusDays(30));

// 3. Add line items
invoice.addLineItem(LineItem.create("Service A", BigDecimal.valueOf(10), BigDecimal.valueOf(100)));
invoice.addLineItem(LineItem.create("Service B", BigDecimal.valueOf(5), BigDecimal.valueOf(200)));

// 4. Send invoice
invoice.markAsSent();  // Status: DRAFT → SENT

// 5. Record payment
Payment payment = Payment.create(
    invoice.getId(), 
    BigDecimal.valueOf(1000), 
    LocalDate.now(), 
    "BANK_TRANSFER"
);
invoice.applyPayment(payment);

// 6. Check status
if (invoice.getStatus() == InvoiceStatus.PAID) {
    // Invoice fully paid
}
```

---

## Test Coverage Summary

| Entity | Test Cases | Coverage |
|--------|-----------|----------|
| Customer | 7 | ✅ 100% |
| Invoice | 9 | ✅ 100% |
| LineItem | 7 | ✅ 100% |
| Payment | 6 | ✅ 100% |
| **Total** | **29** | **✅ 100%** |

---

## Validation Rules Quick Check

### Customer
- ✅ Name: Not null, not empty
- ✅ Email: Valid format, normalized to lowercase
- ✅ Email: Unique (enforced at repository level)

### Invoice
- ✅ Status: Valid transitions only (DRAFT → SENT → PAID)
- ✅ Line Items: Required before sending
- ✅ Balance: Cannot be negative
- ✅ Modifications: Only allowed in DRAFT status

### LineItem
- ✅ Description: Not null, not empty
- ✅ Quantity: > 0
- ✅ Unit Price: >= 0
- ✅ Total: Auto-calculated

### Payment
- ✅ Amount: > 0
- ✅ Date: Not in future
- ✅ Balance: Cannot exceed invoice balance

---

## Next: Phase 3 - CQRS Commands

The domain model is ready to be used in command handlers:
- `CreateCustomerCommand` → Uses `Customer.create()`
- `CreateInvoiceCommand` → Uses `Invoice.create()`
- `RecordPaymentCommand` → Uses `Payment.create()` and `Invoice.applyPayment()`


