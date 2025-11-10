/**
 * AWS Lambda function for handling default WebSocket messages.
 * Function name: alexHoWebSocketDefault
 * 
 * This function handles incoming messages from clients.
 * Currently, it just echoes back a confirmation.
 */

exports.handler = async (event) => {
    console.log('alexHoWebSocketDefault - Message event:', JSON.stringify(event, null, 2));
    
    const connectionId = event.requestContext.connectionId;
    const body = JSON.parse(event.body || '{}');
    
    // Handle ping/pong for keep-alive
    if (body.action === 'ping') {
        return {
            statusCode: 200,
            body: JSON.stringify({ action: 'pong', timestamp: new Date().toISOString() })
        };
    }
    
    // Echo back the message
    return {
        statusCode: 200,
        body: JSON.stringify({ 
            message: 'Message received',
            connectionId: connectionId,
            received: body
        })
    };
};

