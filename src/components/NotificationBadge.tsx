import React from "react";
import { Link } from "react-router-dom";
import { useNotifications } from "../context/NotificationContext";

export function NotificationBadge({ to }: { to: string }) {
  try {
    const { unread } = useNotifications();
    return (
      <Link to={to} className="px-3 py-2 rounded-lg border text-sm hover:bg-gray-50">
        Notifications {unread > 0 ? `(${unread})` : ""}
      </Link>
    );
  } catch {
    return (
      <Link to={to} className="px-3 py-2 rounded-lg border text-sm hover:bg-gray-50">
        Notifications
      </Link>
    );
  }
}
