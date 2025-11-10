/**
 * WebSocket client for real-time invoice updates.
 * All functions prefixed with "alexHo" as requested.
 * 
 * This client connects to the AWS API Gateway WebSocket API and handles
 * real-time invoice updates for both admin and customer users.
 */

export interface alexHoInvoiceUpdateMessage {
  type: 'INVOICE_UPDATE';
  eventType: 'INVOICE_CREATED' | 'INVOICE_UPDATED' | 'INVOICE_SENT' | 'INVOICE_PAID' | 'PAYMENT_RECORDED';
  invoice: any;
  timestamp: string;
}

export type alexHoWebSocketStatus = 'CONNECTING' | 'CONNECTED' | 'DISCONNECTED' | 'ERROR';

export interface alexHoWebSocketCallbacks {
  onInvoiceUpdate?: (message: alexHoInvoiceUpdateMessage) => void;
  onStatusChange?: (status: alexHoWebSocketStatus) => void;
  onError?: (error: Error) => void;
}

export class alexHoWebSocketClient {
  private ws: WebSocket | null = null;
  private reconnectAttempts = 0;
  private maxReconnectAttempts = 5;
  private reconnectDelay = 1000; // Start with 1 second
  private reconnectTimer: NodeJS.Timeout | null = null;
  private pingInterval: NodeJS.Timeout | null = null;
  private status: alexHoWebSocketStatus = 'DISCONNECTED';
  private callbacks: alexHoWebSocketCallbacks = {};
  private wsUrl: string;
  private customerId: string | null;
  private userType: 'ADMIN' | 'CUSTOMER';

  constructor(
    wsUrl: string,
    customerId: string | null = null,
    userType: 'ADMIN' | 'CUSTOMER' = 'CUSTOMER'
  ) {
    this.wsUrl = wsUrl;
    this.customerId = customerId;
    this.userType = userType;
  }

  /**
   * Set callbacks for WebSocket events.
   */
  alexHoSetCallbacks(callbacks: alexHoWebSocketCallbacks) {
    this.callbacks = { ...this.callbacks, ...callbacks };
  }

  /**
   * Connect to the WebSocket API.
   */
  alexHoConnect() {
    if (this.ws && this.ws.readyState === WebSocket.OPEN) {
      console.log('alexHoWebSocketClient - Already connected');
      return;
    }

    try {
      // Build connection URL with query parameters
      const url = new URL(this.wsUrl);
      if (this.customerId) {
        url.searchParams.set('customerId', this.customerId);
      }
      url.searchParams.set('userType', this.userType);

      this.alexHoUpdateStatus('CONNECTING');
      this.ws = new WebSocket(url.toString());

      this.ws.onopen = () => {
        console.log('alexHoWebSocketClient - Connected to WebSocket');
        this.alexHoUpdateStatus('CONNECTED');
        this.reconnectAttempts = 0;
        this.reconnectDelay = 1000;
        this.alexHoStartPing();
      };

      this.ws.onmessage = (event) => {
        try {
          const data = JSON.parse(event.data);
          
          // Handle pong response
          if (data.action === 'pong') {
            return;
          }

          // Handle invoice updates
          if (data.type === 'INVOICE_UPDATE' && this.callbacks.onInvoiceUpdate) {
            this.callbacks.onInvoiceUpdate(data as alexHoInvoiceUpdateMessage);
          }
        } catch (error) {
          console.error('alexHoWebSocketClient - Error parsing message:', error);
        }
      };

      this.ws.onerror = (error) => {
        console.error('alexHoWebSocketClient - WebSocket error:', error);
        this.alexHoUpdateStatus('ERROR');
        if (this.callbacks.onError) {
          this.callbacks.onError(new Error('WebSocket connection error'));
        }
      };

      this.ws.onclose = () => {
        console.log('alexHoWebSocketClient - WebSocket closed');
        this.alexHoUpdateStatus('DISCONNECTED');
        this.alexHoStopPing();
        this.alexHoAttemptReconnect();
      };
    } catch (error) {
      console.error('alexHoWebSocketClient - Error connecting:', error);
      this.alexHoUpdateStatus('ERROR');
      if (this.callbacks.onError) {
        this.callbacks.onError(error as Error);
      }
    }
  }

  /**
   * Disconnect from the WebSocket API.
   */
  alexHoDisconnect() {
    if (this.reconnectTimer) {
      clearTimeout(this.reconnectTimer);
      this.reconnectTimer = null;
    }
    this.alexHoStopPing();
    if (this.ws) {
      this.ws.close();
      this.ws = null;
    }
    this.alexHoUpdateStatus('DISCONNECTED');
  }

  /**
   * Send a ping message to keep the connection alive.
   */
  private alexHoStartPing() {
    this.pingInterval = setInterval(() => {
      if (this.ws && this.ws.readyState === WebSocket.OPEN) {
        this.ws.send(JSON.stringify({ action: 'ping' }));
      }
    }, 30000); // Ping every 30 seconds
  }

  /**
   * Stop the ping interval.
   */
  private alexHoStopPing() {
    if (this.pingInterval) {
      clearInterval(this.pingInterval);
      this.pingInterval = null;
    }
  }

  /**
   * Attempt to reconnect with exponential backoff.
   */
  private alexHoAttemptReconnect() {
    if (this.reconnectAttempts >= this.maxReconnectAttempts) {
      console.error('alexHoWebSocketClient - Max reconnect attempts reached');
      return;
    }

    this.reconnectAttempts++;
    const delay = this.reconnectDelay * Math.pow(2, this.reconnectAttempts - 1);
    
    console.log(`alexHoWebSocketClient - Reconnecting in ${delay}ms (attempt ${this.reconnectAttempts})`);
    
    this.reconnectTimer = setTimeout(() => {
      this.alexHoConnect();
    }, delay);
  }

  /**
   * Update connection status and notify callback.
   */
  private alexHoUpdateStatus(status: alexHoWebSocketStatus) {
    this.status = status;
    if (this.callbacks.onStatusChange) {
      this.callbacks.onStatusChange(status);
    }
  }

  /**
   * Get current connection status.
   */
  alexHoGetStatus(): alexHoWebSocketStatus {
    return this.status;
  }

  /**
   * Check if currently connected.
   */
  alexHoIsConnected(): boolean {
    return this.status === 'CONNECTED' && this.ws?.readyState === WebSocket.OPEN;
  }
}

