import React, { createContext, useContext, useState } from "react";

type NotificationContextValue = {
  freelancerId?: number;
  setFreelancerId: (id?: number) => void;
  notifications: any[];
  unread: number;
  unreadCount: number;
  loading: boolean;
  refresh: () => Promise<void>;
  markRead: (id: number) => Promise<void>;
  markAsRead: (id: number) => Promise<void>;
  markAllAsRead: () => Promise<void>;
};

const noopAsync = async () => {};

const NotificationContext = createContext<NotificationContextValue>({
  freelancerId: undefined,
  setFreelancerId: () => {},
  notifications: [],
  unread: 0,
  unreadCount: 0,
  loading: false,
  refresh: noopAsync,
  markRead: noopAsync,
  markAsRead: noopAsync,
  markAllAsRead: noopAsync,
});

export function NotificationProvider({
  freelancerId: initialFreelancerId,
  children,
}: {
  freelancerId?: number;
  children: React.ReactNode;
}) {
  const [freelancerId, setFreelancerId] = useState<number | undefined>(
    initialFreelancerId
  );

  return (
    <NotificationContext.Provider
      value={{
        freelancerId,
        setFreelancerId,
        notifications: [],
        unread: 0,
        unreadCount: 0,
        loading: false,
        refresh: noopAsync,
        markRead: noopAsync,
        markAsRead: noopAsync,
        markAllAsRead: noopAsync,
      }}
    >
      {children}
    </NotificationContext.Provider>
  );
}

export function useNotifications() {
  return useContext(NotificationContext);
}
