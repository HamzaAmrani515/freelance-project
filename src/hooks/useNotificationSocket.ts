import { useEffect, useRef } from "react";
import SockJS from "sockjs-client";
import { Client, type StompSubscription } from "@stomp/stompjs";
import { OpenAPI } from "../api";
import type { NotificationDto } from "../api/notificationsApi";
import { mapBackendNotif } from "../api/notificationMapper";

type OnMessage = (notif: NotificationDto) => void;

function baseUrl(): string {
  const b = (OpenAPI.BASE ?? "").trim();
  return b.length > 0 ? b : "http://localhost:8080";
}

export function useNotificationSocket(freelancerId: number, onMessage: OnMessage) {
  const clientRef = useRef<Client | null>(null);
  const subRef = useRef<StompSubscription | null>(null);
  const onMessageRef = useRef<OnMessage>(onMessage);

  // ✅ garder toujours la dernière version de onMessage sans casser les deps
  useEffect(() => {
    onMessageRef.current = onMessage;
  }, [onMessage]);

  useEffect(() => {
    if (!freelancerId || Number.isNaN(freelancerId)) return;

    // ✅ cleanup ancien client si existant
    if (clientRef.current) {
      try {
        subRef.current?.unsubscribe();
      } catch {}
      subRef.current = null;

      try {
        clientRef.current.deactivate();
      } catch {}
      clientRef.current = null;
    }

    const sock = new SockJS(`${baseUrl()}/ws`);

    const client = new Client({
      webSocketFactory: () => sock as any,
      reconnectDelay: 2000,
      debug: () => {},
      onConnect: () => {
        // ✅ BACKEND envoie sur /topic/notifications/{freelancerId}
        subRef.current = client.subscribe(`/topic/notifications/${freelancerId}`, (msg) => {
          try {
            const raw = JSON.parse(msg.body);
            const mapped = mapBackendNotif(raw);
            onMessageRef.current(mapped);
          } catch {
            // ignore
          }
        });
      },
      // optionnel mais utile pour debug
      onStompError: () => {},
      onWebSocketError: () => {},
    });

    client.activate();
    clientRef.current = client;

    return () => {
      try {
        subRef.current?.unsubscribe();
      } catch {}
      subRef.current = null;

      try {
        client.deactivate();
      } catch {}
      clientRef.current = null;
    };
  }, [freelancerId]);
}