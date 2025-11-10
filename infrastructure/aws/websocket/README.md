# InvoiceMe WebSocket API - Real-time Invoice Updates

This directory contains AWS infrastructure for real-time invoice updates using API Gateway WebSocket API.

## Architecture

- **API Gateway WebSocket API**: `alexHoInvoiceWebSocket`
- **DynamoDB Table**: `alexHoWebSocketConnections` (stores active connections)
- **Lambda Functions** (all prefixed with `alexHo`):
  - `alexHoWebSocketConnect`: Handles new WebSocket connections
  - `alexHoWebSocketDisconnect`: Handles WebSocket disconnections
  - `alexHoWebSocketDefault`: Handles incoming messages
  - `alexHoSendInvoiceUpdate`: Sends invoice updates to connected clients
- **SNS Topic**: `alexHoInvoiceEvents` (for backend to publish invoice events)

## Deployment

### Prerequisites

1. AWS CLI configured with appropriate credentials
2. AWS SAM CLI installed: `brew install aws-sam-cli` (Mac) or see [AWS SAM docs](https://docs.aws.amazon.com/serverless-application-model/latest/developerguide/install-sam-cli.html)

### Deploy

```bash
cd infrastructure/aws/websocket
npm install
sam build
sam deploy --guided
```

The first deployment will prompt you for:
- Stack name (e.g., `alexho-invoiceme-websocket`)
- AWS Region
- Confirm changes before deploy
- Allow SAM CLI IAM role creation
- Disable rollback
- Save arguments to configuration file

### Get WebSocket Endpoint

After deployment, get the WebSocket endpoint:

```bash
aws cloudformation describe-stacks \
  --stack-name alexho-invoiceme-websocket \
  --query 'Stacks[0].Outputs[?OutputKey==`WebSocketApiEndpoint`].OutputValue' \
  --output text
```

Or check the AWS Console:
1. Go to API Gateway
2. Find `alexHoInvoiceWebSocket`
3. Copy the WebSocket URL (wss://...)

## Usage

### Frontend Connection

```javascript
const ws = new WebSocket('wss://<api-id>.execute-api.<region>.amazonaws.com/production?customerId=<customer-id>&userType=CUSTOMER');

ws.onmessage = (event) => {
  const data = JSON.parse(event.data);
  if (data.type === 'INVOICE_UPDATE') {
    // Handle invoice update
    console.log('Invoice updated:', data.invoice);
  }
};
```

### Backend Publishing Events

The backend should publish invoice events to the SNS topic:

```java
// Publish to SNS topic: alexHoInvoiceEvents
// Message format:
{
  "eventType": "INVOICE_CREATED" | "INVOICE_UPDATED" | "INVOICE_SENT" | "INVOICE_PAID" | "PAYMENT_RECORDED",
  "invoice": { /* invoice data */ },
  "customerId": "uuid"
}
```

## Event Types

- `INVOICE_CREATED`: New invoice created
- `INVOICE_UPDATED`: Invoice details updated
- `INVOICE_SENT`: Invoice status changed to SENT
- `INVOICE_PAID`: Invoice status changed to PAID
- `PAYMENT_RECORDED`: Payment recorded on invoice

## Cleanup

To delete all resources:

```bash
sam delete --stack-name alexho-invoiceme-websocket
```

