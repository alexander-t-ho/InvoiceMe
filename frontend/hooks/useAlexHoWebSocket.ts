/**
 * React hook for WebSocket connection with real-time invoice updates.
 * All functions prefixed with "alexHo" as requested.
 */

import { useEffect, useRef, useState } from 'react';
import { alexHoWebSocketClient, alexHoInvoiceUpdateMessage, alexHoWebSocketStatus } from '@/lib/websocket/alexHoWebSocketClient';
import { useQueryClient } from '@tanstack/react-query';
import { useToast } from '@/hooks/use-toast';

interface alexHoUseWebSocketOptions {
  enabled?: boolean;
  customerId?: string | null;
  userType?: 'ADMIN' | 'CUSTOMER';
  showNotifications?: boolean;
}

/**
 * React hook for managing WebSocket connection and real-time invoice updates.
 */
export function useAlexHoWebSocket(options: alexHoUseWebSocketOptions = {}) {
  const {
    enabled = true,
    customerId = null,
    userType = 'CUSTOMER',
    showNotifications = true,
  } = options;

  const queryClient = useQueryClient();
  const { toast } = useToast();
  const clientRef = useRef<alexHoWebSocketClient | null>(null);
  const [status, setStatus] = useState<alexHoWebSocketStatus>('DISCONNECTED');

  useEffect(() => {
    if (!enabled) {
      return;
    }

    // Get WebSocket URL from environment variable
    const wsUrl = process.env.NEXT_PUBLIC_WEBSOCKET_URL;
    if (!wsUrl) {
      console.warn('useAlexHoWebSocket - WebSocket URL not configured');
      return;
    }

    // Create WebSocket client
    const client = new alexHoWebSocketClient(wsUrl, customerId, userType);
    clientRef.current = client;

    // Set up callbacks
    client.alexHoSetCallbacks({
      onInvoiceUpdate: (message: alexHoInvoiceUpdateMessage) => {
        console.log('useAlexHoWebSocket - Invoice update received:', message);

        // Invalidate relevant queries to refetch data
        queryClient.invalidateQueries({ queryKey: ['invoices'] });
        if (message.invoice?.id) {
          queryClient.invalidateQueries({ queryKey: ['invoices', message.invoice.id] });
        }

        // Show notification if enabled
        if (showNotifications) {
          const eventMessages: Record<string, string> = {
            INVOICE_CREATED: 'New invoice created',
            INVOICE_UPDATED: 'Invoice updated',
            INVOICE_SENT: 'Invoice sent',
            INVOICE_PAID: 'Invoice paid',
            PAYMENT_RECORDED: 'Payment recorded',
          };

          toast({
            title: 'Invoice Update',
            description: eventMessages[message.eventType] || 'Invoice updated',
          });
        }
      },
      onStatusChange: (newStatus) => {
        setStatus(newStatus);
      },
      onError: (error) => {
        console.error('useAlexHoWebSocket - WebSocket error:', error);
        if (showNotifications) {
          toast({
            title: 'Connection Error',
            description: 'Failed to connect to real-time updates',
            variant: 'destructive',
          });
        }
      },
    });

    // Connect
    client.alexHoConnect();

    // Cleanup on unmount
    return () => {
      client.alexHoDisconnect();
      clientRef.current = null;
    };
  }, [enabled, customerId, userType, queryClient, toast, showNotifications]);

  return {
    status,
    isConnected: status === 'CONNECTED',
    reconnect: () => {
      if (clientRef.current) {
        clientRef.current.alexHoDisconnect();
        clientRef.current.alexHoConnect();
      }
    },
  };
}

