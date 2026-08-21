import { http } from "./http";

export type MissionCreatePayload = {
  titre: string;
  description?: string;
  competences: { nom: string }[];
};

export async function createMission(payload: MissionCreatePayload): Promise<any> {
  return http<any>(`/api/missions/post`, {
    method: "POST",
    body: JSON.stringify(payload),
  });
}