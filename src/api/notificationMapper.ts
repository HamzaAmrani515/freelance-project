import type { NotificationDto } from "./notificationsApi";

export function mapBackendNotif(n: any): NotificationDto {
  return {
    id: Number(n.id),
    message: String(n.message ?? ""),
    timestamp: String(n.timestamp ?? new Date().toISOString()),
    read: Boolean(n.read ?? n.isRead), // ✅ support read / isRead
    freelancerId: n.freelancerId != null ? Number(n.freelancerId) : null,
    missionId: n.missionId != null ? Number(n.missionId) : null,
    missionTitle: (n.missionTitle ?? n.missionTitre ?? n.titre ?? null) as string | null,
  };
}
