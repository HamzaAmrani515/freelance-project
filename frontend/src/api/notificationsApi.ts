import { http } from "./http";

export type NotificationDto = {
  id: number;
  message: string;
  timestamp: string; // ISO
  read: boolean;
  freelancerId: number | null;
  missionId: number | null;
  missionTitle: string | null;
};

export function mapBackendNotif(n: any): NotificationDto {
  return {
    id: Number(n.id),
    message: String(n.message ?? ""),
    timestamp: String(n.timestamp ?? new Date().toISOString()),
    read: Boolean(n.read ?? n.isRead),
    freelancerId: n.freelancerId != null ? Number(n.freelancerId) : null,
    missionId: n.missionId != null ? Number(n.missionId) : null,
    missionTitle: (n.missionTitle ?? n.missionTitre ?? n.titre ?? null) as string | null,
  };
}

export async function getFreelancerNotifications(freelancerId: number): Promise<NotificationDto[]> {
  const raw = await http<any[]>(`/api/notifications/v2/freelancer/${freelancerId}`, { method: "GET" });
  return raw.map(mapBackendNotif);
}

export async function getUnreadCount(freelancerId: number): Promise<number> {
  return http<number>(`/api/notifications/v2/freelancer/${freelancerId}/unread-count`, { method: "GET" });
}

export async function markNotificationAsRead(notificationId: number): Promise<void> {
  return http<void>(`/api/notifications/v2/${notificationId}/read`, { method: "PATCH" });
}