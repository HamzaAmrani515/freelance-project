import React, {
  createContext,
  useCallback,
  useContext,
  useEffect,
  useMemo,
  useState,
} from "react";
import { toast } from "react-toastify";
import {
  getFreelancerNotifications,
  getUnreadCount,
  markNotificationAsRead,
  type NotificationDto,
} from "../api/notificationsApi";
import { useNotificationSocket } from "../hooks/useNotificationSocket";

type NotificationState = {
  loading: boolean;
  notifications: NotificationDto[];
  unread: number;
  refresh: () => Promise<void>;
  markRead: (id: number) => Promise<void>;
};

const Ctx = createContext<NotificationState | null>(null);

export function useNotifications() {
  const v = useContext(Ctx);
  if (!v) throw new Error("useNotifications must be used inside NotificationProvider");
  return v;
}

export function NotificationProvider({
  freelancerId,
  children,
}: {
  freelancerId: number;
  children: React.ReactNode;
}) {
  const [loading, setLoading] = useState(true);
  const [notifications, setNotifications] = useState<NotificationDto[]>([]);
  const [unread, setUnread] = useState(0);

  // ✅ Load list + unread count from REST
  const refresh = useCallback(async () => {
    if (!freelancerId || Number.isNaN(freelancerId)) return;

    setLoading(true);
    try {
      const [list, cnt] = await Promise.all([
        getFreelancerNotifications(freelancerId),
        getUnreadCount(freelancerId),
      ]);
      setNotifications(list);
      setUnread(cnt);
    } catch (e: any) {
      toast.error(e?.message ?? "Erreur chargement notifications");
    } finally {
      setLoading(false);
    }
  }, [freelancerId]);

  // ✅ Mark as read (REST) + update local state safely
  const markRead = useCallback(async (id: number) => {
    try {
      await markNotificationAsRead(id);

      // décrémenter unread seulement si la notif était unread avant
      setNotifications((prev) => {
        const existing = prev.find((n) => n.id === id);
        const wasUnread = existing ? !existing.read : false;

        if (wasUnread) {
          setUnread((u) => Math.max(0, u - 1));
        }

        return prev.map((n) => (n.id === id ? { ...n, read: true } : n));
      });
    } catch (e: any) {
      toast.error(e?.message ?? "Impossible de marquer comme lue");
    }
  }, []);

  // ✅ Handle incoming WS notifications (new + updates)
  const onWsMessage = useCallback((incoming: NotificationDto) => {
    setNotifications((prev) => {
      const idx = prev.findIndex((x) => x.id === incoming.id);

      // New notification
      if (idx < 0) {
        if (!incoming.read) setUnread((u) => u + 1);
        toast.info(incoming.message);
        return [incoming, ...prev];
      }

      // Update existing notification (read/unread change)
      const existing = prev[idx];
      const wasUnread = !existing.read;
      const nowUnread = !incoming.read;

      if (wasUnread && !nowUnread) setUnread((u) => Math.max(0, u - 1));
      if (!wasUnread && nowUnread) setUnread((u) => u + 1);

      const copy = [...prev];
      copy[idx] = incoming;

      // Toast seulement si c’est une notif réellement "nouvelle" (optionnel)
      // Ici on ne toast pas pour éviter spam quand c'est juste une update "read".
      return copy;
    });
  }, []);

  // ✅ WS subscription
  useNotificationSocket(freelancerId, onWsMessage);

  // ✅ Initial fetch + reset when freelancerId changes
  useEffect(() => {
    if (!freelancerId || Number.isNaN(freelancerId)) {
      setLoading(false);
      setNotifications([]);
      setUnread(0);
      return;
    }
    refresh();
  }, [freelancerId, refresh]);

  const value = useMemo(
    () => ({ loading, notifications, unread, refresh, markRead }),
    [loading, notifications, unread, refresh, markRead]
  );

  return <Ctx.Provider value={value}>{children}</Ctx.Provider>;
}