/**
 * AWS Lambda function for handling WebSocket disconnection events.
 * Function name: alexHoWebSocketDisconnect
 * 
 * This function is called when a client disconnects from the WebSocket API.
 * It removes the connection ID from DynamoDB.
 */

const AWS = require('aws-sdk');
const dynamodb = new AWS.DynamoDB.DocumentClient();

const CONNECTIONS_TABLE = process.env.CONNECTIONS_TABLE || 'alexHoWebSocketConnections';

exports.handler = async (event) => {
    console.log('alexHoWebSocketDisconnect - Disconnection event:', JSON.stringify(event, null, 2));
    
    const connectionId = event.requestContext.connectionId;
    
    // Remove connection from DynamoDB
    const params = {
        TableName: CONNECTIONS_TABLE,
        Key: {
            connectionId: connectionId
        }
    };
    
    try {
        await dynamodb.delete(params).promise();
        console.log(`alexHoWebSocketDisconnect - Removed connection: ${connectionId}`);
        
        return {
            statusCode: 200,
            body: JSON.stringify({ message: 'Disconnected successfully' })
        };
    } catch (error) {
        console.error('alexHoWebSocketDisconnect - Error removing connection:', error);
        return {
            statusCode: 500,
            body: JSON.stringify({ error: 'Failed to remove connection' })
        };
    }
};

