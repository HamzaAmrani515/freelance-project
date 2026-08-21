export type UserRole = "ADMIN" | "CLIENT" | "FREELANCER";

export type DemoUser = {
  id: string;
  role: UserRole;
  label: string;
  email: string;
  subtitle: string;
  companyName?: string;
  clientId?: string;
  freelancerId?: number;
};

export type ClientMission = {
  missionId: number;
  missionTitle: string;
  clientId: string;
  clientName: string;
  freelancerId: number;
  freelancerName: string;
  skill: string;
  status: "IN_PROGRESS" | "COMPLETED" | "AT_RISK" | "ASSIGNED" | "CANCELLED";
  businessContext: string;
};

export const demoAdmin: DemoUser = {
  id: "admin-platform",
  role: "ADMIN",
  label: "Administrateur plateforme",
  email: "admin@freelance-platform.com",
  subtitle:
    "Pilotage global du ranking, des profils, des évaluations et de la volumétrie.",
};

export const demoClients: DemoUser[] = [
  {
    id: "client-amex",
    role: "CLIENT",
    clientId: "amex",
    label: "AMEX Digital",
    email: "client.amex@freelance-platform.com",
    subtitle: "Mission data engineering, pipelines et qualité des données.",
    companyName: "AMEX Digital",
  },
  {
    id: "client-dassault",
    role: "CLIENT",
    clientId: "dassault",
    label: "Dassault Systemes",
    email: "client.dassault@freelance-platform.com",
    subtitle: "Mission DevOps, CI/CD, monitoring et automatisation.",
    companyName: "Dassault Systemes",
  },
  {
    id: "client-capgemini",
    role: "CLIENT",
    clientId: "capgemini",
    label: "Capgemini",
    email: "client.capgemini@freelance-platform.com",
    subtitle: "Mission frontend React, composants et expérience utilisateur.",
    companyName: "Capgemini",
  },
  {
    id: "client-carrefour",
    role: "CLIENT",
    clientId: "carrefour",
    label: "Carrefour Tech",
    email: "client.carrefour@freelance-platform.com",
    subtitle: "Mission PostgreSQL, sauvegarde, performance et optimisation.",
    companyName: "Carrefour Tech",
  },
];

export const demoFreelancers: DemoUser[] = [
  {
    id: "freelancer-amex",
    role: "FREELANCER",
    freelancerId: 1,
    label: "Amex Demo",
    email: "amex.demo@freelance-platform.com",
    subtitle: "Data Engineer · AMEX Digital · mission active #1.",
  },
  {
    id: "freelancer-nadia",
    role: "FREELANCER",
    freelancerId: 2,
    label: "Nadia Tazi",
    email: "nadia.tazi@freelance-platform.com",
    subtitle: "DevOps Engineer · Dassault Systemes · mission active #2.",
  },
  {
    id: "freelancer-yassine",
    role: "FREELANCER",
    freelancerId: 3,
    label: "Yassine Alaoui",
    email: "yassine.alaoui@freelance-platform.com",
    subtitle: "React Developer · Capgemini · mission active #3.",
  },
  {
    id: "freelancer-sara",
    role: "FREELANCER",
    freelancerId: 4,
    label: "Sara Moreau",
    email: "sara.moreau@freelance-platform.com",
    subtitle: "PostgreSQL DBA · Carrefour Tech · mission active #4.",
  },
];

export const clientMissions: ClientMission[] = [
  {
    missionId: 1,
    missionTitle: "AMEX - Pipeline Data Engineering #1",
    clientId: "amex",
    clientName: "AMEX Digital",
    freelancerId: 1,
    freelancerName: "Amex Demo",
    skill: "Data Engineer",
    status: "IN_PROGRESS",
    businessContext:
      "Mission active sur pipelines data, qualité des données et reporting. Le client peut évaluer un jalon.",
  },
  {
    missionId: 2,
    missionTitle: "Dassault - Mission DevOps #2",
    clientId: "dassault",
    clientName: "Dassault Systemes",
    freelancerId: 2,
    freelancerName: "Nadia Tazi",
    skill: "DevOps Engineer",
    status: "IN_PROGRESS",
    businessContext:
      "Mission active sur CI/CD, monitoring et automatisation DevOps. Le client peut évaluer un jalon.",
  },
  {
    missionId: 3,
    missionTitle: "Capgemini - Mission React #3",
    clientId: "capgemini",
    clientName: "Capgemini",
    freelancerId: 3,
    freelancerName: "Yassine Alaoui",
    skill: "React Developer",
    status: "IN_PROGRESS",
    businessContext:
      "Mission active sur interface React, composants front et expérience utilisateur. Le client peut évaluer un jalon.",
  },
  {
    missionId: 4,
    missionTitle: "Carrefour - Mission PostgreSQL #4",
    clientId: "carrefour",
    clientName: "Carrefour Tech",
    freelancerId: 4,
    freelancerName: "Sara Moreau",
    skill: "PostgreSQL DBA",
    status: "IN_PROGRESS",
    businessContext:
      "Mission active sur base de données, sauvegarde et performance PostgreSQL. Le client peut évaluer un jalon.",
  },
];

export function getMissionsForClient(clientId?: string) {
  if (!clientId) return [];
  return clientMissions.filter((mission) => mission.clientId === clientId);
}

export function getMissionById(missionId: number) {
  return clientMissions.find((mission) => mission.missionId === missionId);
}

export function getMissionsForFreelancer(freelancerId?: number) {
  if (!freelancerId) return [];
  return clientMissions.filter(
    (mission) => mission.freelancerId === freelancerId
  );
}

export function getActiveMissionsForFreelancer(freelancerId?: number) {
  if (!freelancerId) return [];

  return clientMissions.filter(
    (mission) =>
      mission.freelancerId === freelancerId &&
      ["IN_PROGRESS", "AT_RISK", "ASSIGNED"].includes(mission.status)
  );
}

export function getAllDemoUsers(): DemoUser[] {
  return [demoAdmin, ...demoClients, ...demoFreelancers];
}