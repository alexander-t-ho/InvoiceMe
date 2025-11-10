/**
 * AWS Lambda function for handling WebSocket connection events.
 * Function name: alexHoWebSocketConnect
 * 
 * This function is called when a client connects to the WebSocket API.
 * It stores the connection ID in DynamoDB for later message routing.
 */

const AWS = require('aws-sdk');
const dynamodb = new AWS.DynamoDB.DocumentClient();

const CONNECTIONS_TABLE = process.env.CONNECTIONS_TABLE || 'alexHoWebSocketConnections';

exports.handler = async (event) => {
    console.log('alexHoWebSocketConnect - Connection event:', JSON.stringify(event, null, 2));
    
    const connectionId = event.requestContext.connectionId;
    const customerId = event.queryStringParameters?.customerId || null;
    const userType = event.queryStringParameters?.userType || 'CUSTOMER'; // CUSTOMER or ADMIN
    
    // Store connection in DynamoDB
    const params = {
        TableName: CONNECTIONS_TABLE,
        Item: {
            connectionId: connectionId,
            customerId: customerId,
            userType: userType,
            connectedAt: new Date().toISOString(),
            ttl: Math.floor(Date.now() / 1000) + 3600 // 1 hour TTL
        }
    };
    
    try {
        await dynamodb.put(params).promise();
        console.log(`alexHoWebSocketConnect - Stored connection: ${connectionId} for customer: ${customerId}`);
        
        return {
            statusCode: 200,
            body: JSON.stringify({ message: 'Connected successfully' })
        };
    } catch (error) {
        console.error('alexHoWebSocketConnect - Error storing connection:', error);
        return {
            statusCode: 500,
            body: JSON.stringify({ error: 'Failed to store connection' })
        };
    }
};

