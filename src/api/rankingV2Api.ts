import { http } from "./http";

export type DeliveryStatus = "ON_TIME" | "LATE_DELIVERY" | "CANCELLED";

export type CreateEvaluationPayload = {
  missionId: number;
  technicalQuality: number;
  communication: number;
  autonomy: number;
  testQuality: number;
  deliveryStatus: DeliveryStatus;
  feedback: string;
};

export type EvaluationResponse = {
  missionId: number;
  freelancerId: number;
  globalNote: number;
  evaluationScore: number;
  finalScore: number;
  deliveryStatus: DeliveryStatus;
  trendLabel: string;
  message: string;
};

export type FreelancerMissionHistoryDto = {
  missionId: number;
  missionTitle: string;
  deliveryStatus: DeliveryStatus;
  note: number;
  technicalQuality: number;
  communication: number;
  autonomy: number;
  testQuality: number;
  missionScore: number;
  feedback: string;
  evaluatedAt: string;
};

export type MissionHistoryDto = FreelancerMissionHistoryDto;

export type FreelancerRankingDto = {
  freelancerId: number;
  fullName: string;
  skill: string;
  seniority: string;
  availabilityStatus: string;
  companyName: string;
  yearsExperience: number;
  finalScore: number;
  recentEvaluationScore: number;
  reliabilityScore: number;
  trendScore: number;
  experienceScore: number;
  availabilityScore: number;
  trendLabel: string;
  missionsCount: number;
  evaluationsCount: number;
};

export type FreelancerDetailDto = FreelancerRankingDto & {
  missionHistory: FreelancerMissionHistoryDto[];
};

export async function getRanking(
  page: number = 0,
  size: number = 100
): Promise<FreelancerRankingDto[]> {
  return http<FreelancerRankingDto[]>(
    `/api/ranking?page=${page}&size=${size}`
  );
}

export async function getRankingFreelancerById(
  freelancerId: number
): Promise<FreelancerRankingDto> {
  return http<FreelancerRankingDto>(`/api/ranking/${freelancerId}`);
}

export async function getFreelancerEvaluationHistory(
  freelancerId: number
): Promise<FreelancerMissionHistoryDto[]> {
  return http<FreelancerMissionHistoryDto[]>(
    `/api/evaluations/freelancer/${freelancerId}`
  );
}

export async function getFreelancerDetails(
  freelancerId: number
): Promise<FreelancerDetailDto> {
  return http<FreelancerDetailDto>(`/api/freelancers/${freelancerId}/details`);
}

export async function createEvaluation(
  payload: CreateEvaluationPayload
): Promise<EvaluationResponse> {
  return http<EvaluationResponse>("/api/evaluations", {
    method: "POST",
    body: JSON.stringify(payload),
  });
}