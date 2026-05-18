import { useState, useEffect, useCallback, useRef } from 'react';
import { Client, IMessage } from '@stomp/stompjs';

export interface PriceUpdateDTO {
  coinId: string;
  symbol: string;
  priceUsd: number;
  change24h: number;
  updatedAt: string;
}

export const useLivePrices = () => {
  const [prices, setPrices] = useState<Record<string, PriceUpdateDTO>>({});
  const [isConnected, setIsConnected] = useState(false);
  const clientRef = useRef<Client | null>(null);

  const connect = useCallback(() => {
    const client = new Client({
      brokerURL: 'ws://localhost:8082/ws', // Fallback URL if using SockJS: webSocketFactory: () => new SockJS('http://localhost:8082/ws')
      reconnectDelay: 3000,
      heartbeatIncoming: 4000,
      heartbeatOutgoing: 4000,
      onConnect: () => {
        console.log('Connected to WebSocket');
        setIsConnected(true);
        client.subscribe('/topic/prices', (message: IMessage) => {
          if (message.body) {
            const updates: PriceUpdateDTO[] = JSON.parse(message.body);
            setPrices((prev) => {
              const newPrices = { ...prev };
              updates.forEach((update) => {
                newPrices[update.symbol] = update;
              });
              return newPrices;
            });
          }
        });
      },
      onDisconnect: () => {
        console.log('Disconnected from WebSocket');
        setIsConnected(false);
      },
      onStompError: (frame) => {
        console.error('Broker reported error: ' + frame.headers['message']);
        console.error('Additional details: ' + frame.body);
      },
    });

    client.activate();
    clientRef.current = client;
  }, []);

  useEffect(() => {
    connect();
    return () => {
      if (clientRef.current) {
        clientRef.current.deactivate();
      }
    };
  }, [connect]);

  // Fallback to polling could be implemented here using a setInterval 
  // if isConnected remains false for a prolonged period, calling REST API.

  return { prices, isConnected };
};
