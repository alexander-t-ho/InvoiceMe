# Real-time Invoice Updates - Implementation Guide

This document describes the complete implementation of real-time invoice updates using AWS API Gateway WebSocket API with the "alexHo" prefix.

## Architecture Overview

```
Backend (Spring Boot)
    ↓ (Publishes events)
AWS SNS Topic (alexHoInvoiceEvents)
    ↓ (Triggers)
Lambda Function (alexHoSendInvoiceUpdate)
    ↓ (Sends via)
API Gateway WebSocket API (alexHoInvoiceWebSocket)
    ↓ (Delivers to)
Frontend (Next.js) - Connected clients
```

## Components

### AWS Infrastructure (all prefixed with "alexHo")

1. **API Gateway WebSocket API**: `alexHoInvoiceWebSocket`
2. **DynamoDB Table**: `alexHoWebSocketConnections`
3. **Lambda Functions**:
   - `alexHoWebSocketConnect` - Handles connections
   - `alexHoWebSocketDisconnect` - Handles disconnections
   - `alexHoWebSocketDefault` - Handles messages
   - `alexHoSendInvoiceUpdate` - Sends invoice updates
4. **SNS Topic**: `alexHoInvoiceEvents`

### Backend Components

1. **alexHoInvoiceEventPublisher** - Publishes events to SNS
2. **Updated Handlers** - Publish events on invoice operations

### Frontend Components

1. **alexHoWebSocketClient** - WebSocket client class
2. **useAlexHoWebSocket** - React hook for WebSocket connection

## Setup Instructions

### 1. Deploy AWS Infrastructure

```bash
cd infrastructure/aws/websocket
npm install
sam build
sam deploy --guided
```

After deployment, note:
- WebSocket API endpoint (wss://...)
- SNS Topic ARN

### 2. Configure Backend

Add to `backend/src/main/resources/application.yml`:

```yaml
aws:
  region: us-east-1  # Change to your AWS region
  sns:
    invoice-events-topic-arn: arn:aws:sns:us-east-1:123456789012:alexHoInvoiceEvents
```

Set AWS credentials (via environment variables or IAM role):
```bash
export AWS_ACCESS_KEY_ID=your-access-key
export AWS_SECRET_ACCESS_KEY=your-secret-key
export AWS_REGION=us-east-1
```

### 3. Configure Frontend

Add to `.env.local`:

```env
NEXT_PUBLIC_WEBSOCKET_URL=wss://<api-id>.execute-api.<region>.amazonaws.com/production
```

### 4. Update Invoice Handlers

The handlers need to be updated to publish events. See the integration examples below.

## Integration Examples

### Backend: Publishing Events

```java
// In MarkInvoiceAsSentHandler
@Autowired
private alexHoInvoiceEventPublisher eventPublisher;

// After marking invoice as sent
Map<String, Object> invoiceData = convertInvoiceToMap(invoice);
eventPublisher.alexHoPublishInvoiceSent(invoice.getId(), invoice.getCustomerId(), invoiceData);
```

### Frontend: Using WebSocket Hook

```typescript
// In invoice list page
import { useAlexHoWebSocket } from '@/hooks/useAlexHoWebSocket';

export default function InvoicesPage() {
  const { userType } = useAuth();
  
  // Connect to WebSocket for real-time updates
  useAlexHoWebSocket({
    enabled: true,
    userType: userType || 'ADMIN',
    showNotifications: true,
  });
  
  // ... rest of component
}
```

## Event Types

- `INVOICE_CREATED` - New invoice created
- `INVOICE_UPDATED` - Invoice details updated
- `INVOICE_SENT` - Invoice status changed to SENT
- `INVOICE_PAID` - Invoice status changed to PAID
- `PAYMENT_RECORDED` - Payment recorded on invoice

## Testing

1. Deploy infrastructure
2. Configure backend and frontend
3. Create an invoice as admin
4. Open customer portal in another browser
5. Send the invoice - customer should see it appear in real-time

## Troubleshooting

- Check CloudWatch logs for Lambda functions
- Verify SNS topic ARN is correct in backend config
- Check WebSocket URL is correct in frontend config
- Verify AWS credentials are configured
- Check DynamoDB table for active connections

