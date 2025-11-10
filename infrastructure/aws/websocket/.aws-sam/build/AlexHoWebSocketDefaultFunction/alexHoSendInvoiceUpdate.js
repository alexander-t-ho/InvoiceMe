/**
 * AWS Lambda function for sending invoice updates to connected WebSocket clients.
 * Function name: alexHoSendInvoiceUpdate
 * 
 * This function is triggered when an invoice is created, updated, or paid.
 * It sends real-time updates to all relevant connected clients.
 */

const AWS = require('aws-sdk');
const dynamodb = new AWS.DynamoDB.DocumentClient();
const apigwManagementApi = new AWS.ApiGatewayManagementApi({
    endpoint: process.env.WEBSOCKET_API_ENDPOINT
});

const CONNECTIONS_TABLE = process.env.CONNECTIONS_TABLE || 'alexHoWebSocketConnections';

/**
 * Send message to a specific WebSocket connection
 */
async function sendToConnection(connectionId, message) {
    try {
        await apigwManagementApi.postToConnection({
            ConnectionId: connectionId,
            Data: JSON.stringify(message)
        }).promise();
        return true;
    } catch (error) {
        if (error.statusCode === 410) {
            // Connection no longer exists, remove it from DynamoDB
            console.log(`alexHoSendInvoiceUpdate - Removing stale connection: ${connectionId}`);
            await dynamodb.delete({
                TableName: CONNECTIONS_TABLE,
                Key: { connectionId }
            }).promise();
        } else {
            console.error(`alexHoSendInvoiceUpdate - Error sending to connection ${connectionId}:`, error);
        }
        return false;
    }
}

/**
 * Get all active connections for a customer
 */
async function getCustomerConnections(customerId) {
    const params = {
        TableName: CONNECTIONS_TABLE,
        FilterExpression: 'customerId = :customerId',
        ExpressionAttributeValues: {
            ':customerId': customerId
        }
    };
    
    const result = await dynamodb.scan(params).promise();
    return result.Items.map(item => item.connectionId);
}

/**
 * Get all admin connections
 */
async function getAdminConnections() {
    const params = {
        TableName: CONNECTIONS_TABLE,
        FilterExpression: 'userType = :userType',
        ExpressionAttributeValues: {
            ':userType': 'ADMIN'
        }
    };
    
    const result = await dynamodb.scan(params).promise();
    return result.Items.map(item => item.connectionId);
}

exports.handler = async (event) => {
    console.log('alexHoSendInvoiceUpdate - Invoice update event:', JSON.stringify(event, null, 2));
    
    // Parse the invoice update from the event
    // This can be triggered by SNS, EventBridge, or directly invoked
    let invoiceUpdate;
    if (event.Records && event.Records[0] && event.Records[0].Sns) {
        // SNS event
        invoiceUpdate = JSON.parse(event.Records[0].Sns.Message);
    } else if (event.invoice) {
        // Direct invocation
        invoiceUpdate = event;
    } else {
        invoiceUpdate = event;
    }
    
    const { invoice, eventType, customerId } = invoiceUpdate;
    // eventType: 'INVOICE_CREATED', 'INVOICE_UPDATED', 'INVOICE_SENT', 'INVOICE_PAID', 'PAYMENT_RECORDED'
    
    const message = {
        type: 'INVOICE_UPDATE',
        eventType: eventType || 'INVOICE_UPDATED',
        invoice: invoice,
        timestamp: new Date().toISOString()
    };
    
    const connectionIds = new Set();
    
    try {
        // Send to customer if invoice belongs to a customer
        if (customerId) {
            const customerConnections = await getCustomerConnections(customerId);
            customerConnections.forEach(id => connectionIds.add(id));
        }
        
        // Send to all admins
        const adminConnections = await getAdminConnections();
        adminConnections.forEach(id => connectionIds.add(id));
        
        // Send message to all relevant connections
        const sendPromises = Array.from(connectionIds).map(connectionId => 
            sendToConnection(connectionId, message)
        );
        
        const results = await Promise.allSettled(sendPromises);
        const successCount = results.filter(r => r.status === 'fulfilled' && r.value).length;
        
        console.log(`alexHoSendInvoiceUpdate - Sent to ${successCount}/${connectionIds.size} connections`);
        
        return {
            statusCode: 200,
            body: JSON.stringify({
                message: 'Invoice update sent',
                sentTo: successCount,
                totalConnections: connectionIds.size
            })
        };
    } catch (error) {
        console.error('alexHoSendInvoiceUpdate - Error:', error);
        return {
            statusCode: 500,
            body: JSON.stringify({ error: 'Failed to send invoice update' })
        };
    }
};

