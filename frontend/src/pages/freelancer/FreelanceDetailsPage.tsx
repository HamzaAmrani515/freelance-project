import React, { useEffect, useMemo, useState } from "react";
import { useNavigate, useParams } from "react-router-dom";
import {
  DeliveryStatus,
  FreelancerDetailDto,
  FreelancerMissionHistoryDto,
  FreelancerRankingDto,
  getFreelancerEvaluationHistory,
  getRankingFreelancerById,
} from "../../api/rankingV2Api";
import MissionScoreChart from "../../components/MissionScoreChart";
import { getCurrentUser } from "../../auth/session";

export default function FreelanceDetailsPage() {
  const { id } = useParams();
  const navigate = useNavigate();

  const [detail, setDetail] = useState<FreelancerDetailDto | null>(null);
  const [loading, setLoading] = useState(true);

  const user = useMemo(() => getCurrentUser(), []);

  useEffect(() => {
    if (!id) {
      setDetail(buildGenericFreelancerDetail(0));
      setLoading(false);
      return;
    }

    const freelancerId = Number(id);

    setLoading(true);

    getRankingFreelancerById(freelancerId)
      .then(async (backendFreelancer: FreelancerRankingDto) => {
        let backendHistory: FreelancerMissionHistoryDto[] = [];

        try {
          backendHistory = await getFreelancerEvaluationHistory(freelancerId);
        } catch {
          backendHistory = [];
        }

        setDetail({
          ...backendFreelancer,
          missionHistory: Array.isArray(backendHistory) ? backendHistory : [],
        });
      })
      .catch(() => {
        setDetail(buildGenericFreelancerDetail(freelancerId));
      })
      .finally(() => {
        setLoading(false);
      });
  }, [id]);

  const isAdmin = user?.role === "ADMIN";
  const isClient = user?.role === "CLIENT";

  const pageContext = useMemo(() => {
    if (isAdmin) return "Vue administrateur : audit du profil et du score";
    if (isClient) return "Vue client : profil recommandé ou affecté";
    return "Vue freelancer : profil personnel";
  }, [isAdmin, isClient]);

  if (loading || !detail) {
    return (
      <div className="min-h-screen bg-slate-950 text-white p-8">
        Chargement du profil...
      </div>
    );
  }

  const missionHistory = Array.isArray(detail.missionHistory)
    ? detail.missionHistory
    : [];

  const sortedHistory = [...missionHistory].sort((a, b) => {
    return getTime(b.evaluatedAt) - getTime(a.evaluatedAt);
  });

  const latestEvaluation = sortedHistory[0];

  const latestEvaluationScore = latestEvaluation
    ? calculateEvaluationScore(latestEvaluation)
    : detail.finalScore
    ? Number(detail.finalScore) / 20
    : 0;

  const calculatedFinalScore =
    sortedHistory.length > 0
      ? formatNumber(calculateFinalScoreFromLastFour(sortedHistory) * 20)
      : formatNumber(detail.finalScore);

  return (
    <div className="min-h-screen bg-slate-950 text-white p-8">
      <div className="max-w-7xl mx-auto">
        <button
          onClick={() => navigate(-1)}
          className="mb-6 text-blue-400 hover:text-blue-300"
        >
          ← Retour
        </button>

        <div className="bg-gradient-to-r from-blue-600 to-indigo-700 rounded-3xl p-8 mb-8">
          <p className="text-blue-100 font-semibold">{pageContext}</p>

          <h1 className="text-4xl font-bold mt-2">
            {detail.fullName || "Freelancer"}
          </h1>

          <p className="text-blue-100 mt-2">
            {detail.skill || "Skill non renseigné"} ·{" "}
            {detail.seniority || "Seniorité non renseignée"} ·{" "}
            {detail.companyName || "Entreprise non renseignée"}
          </p>

          <div className="mt-6 text-6xl font-bold">
            {formatNumber(detail.finalScore)}
          </div>

          <p className="text-blue-100">Score final sur 100</p>
        </div>

        <div className="hidden">
          <Info label="Score final" value={formatNumber(detail.finalScore)} />
          <Info label="Tendance" value={formatLabel(detail.trendLabel)} />
          <Info
            label="Disponibilité"
            value={formatLabel(detail.availabilityStatus)}
          />
          <Info label="Missions" value={detail.missionsCount || 0} />
          <Info label="Évaluations" value={detail.evaluationsCount || 0} />
        </div>

        <div className="bg-white/5 border border-white/10 rounded-2xl p-6 mb-8">
          <h2 className="text-2xl font-bold">Décomposition de l’algorithme</h2>

          <p className="text-slate-400 mt-1">
            Score officiel récupéré depuis le backend et historique chargé depuis la base de données.
          </p>

          <div className="grid grid-cols-1 md:grid-cols-3 gap-4 mt-6">
            <FormulaCard
              title="Dernière évaluation"
              value={`${formatNumber(latestEvaluationScore)} / 5`}
              text="Score calculé avec les critères pondérés."
            />

            <FormulaCard
              title="Score final backend"
              value={`${formatNumber(detail.finalScore)} / 100`}
              text="Valeur officielle utilisée dans le ranking."
            />

            <FormulaCard
              title="Recalcul affichage"
              value={`${calculatedFinalScore} / 100`}
              text="40% dernière + 30% deuxième + 20% troisième + 10% quatrième."
            />
          </div>
        </div>

        <MissionScoreChart history={missionHistory} />

        <div className="bg-white/5 border border-white/10 rounded-2xl overflow-hidden">
          <div className="p-6 border-b border-white/10">
            <h2 className="text-2xl font-bold">Historique des évaluations</h2>

            <p className="text-slate-400 mt-1">
              Historique récupéré depuis la base de données.
            </p>
          </div>

          <div className="overflow-x-auto">
            <table className="w-full text-sm">
              <thead className="bg-white/10 text-slate-300">
                <tr>
                  <th className="p-4 text-left">Mission</th>
                  <th className="p-4 text-left">Livraison</th>
                  <th className="p-4 text-left">Score mission</th>
                  <th className="p-4 text-left">Technique</th>
                  <th className="p-4 text-left">Communication</th>
                  <th className="p-4 text-left">Autonomie</th>
                  <th className="p-4 text-left">Tests</th>
                  <th className="p-4 text-left">Feedback</th>
                  <th className="p-4 text-left">Date</th>
                </tr>
              </thead>

              <tbody>
                {sortedHistory.map((history, index) => (
                  <tr
                    key={`${history.missionId}-${index}`}
                    className="border-t border-white/10 hover:bg-white/5"
                  >
                    <td className="p-4 font-semibold">
                      {history.missionTitle || `Mission #${history.missionId}`}
                    </td>

                    <td className="p-4">
                      <Badge value={history.deliveryStatus} />
                    </td>

                    <td className="p-4 text-blue-300 font-bold">
                      {formatNumber(history.missionScore)}/5
                    </td>

                    <td className="p-4">
                      {formatNumber(history.technicalQuality)}/5
                    </td>

                    <td className="p-4">
                      {formatNumber(history.communication)}/5
                    </td>

                    <td className="p-4">{formatNumber(history.autonomy)}/5</td>

                    <td className="p-4">
                      {formatNumber(history.testQuality)}/5
                    </td>

                    <td className="p-4 text-slate-300 max-w-md">
                      {history.feedback || "-"}
                    </td>

                    <td className="p-4 text-slate-400">
                      {formatDate(history.evaluatedAt)}
                    </td>
                  </tr>
                ))}

                {sortedHistory.length === 0 && (
                  <tr>
                    <td colSpan={9} className="p-8 text-center text-slate-400">
                      Aucun historique disponible depuis la base de données.
                    </td>
                  </tr>
                )}
              </tbody>
            </table>
          </div>
        </div>

        <div className="mt-8 bg-blue-500/10 border border-blue-500/20 rounded-2xl p-6">
          <h2 className="text-xl font-bold">Règle de calcul</h2>

          <p className="text-slate-300 mt-2 leading-7">
            Chaque évaluation produit un score sur 5 : 30% qualité technique,
            20% communication, 15% autonomie, 15% qualité des tests et 20%
            statut de livraison. Le score final du freelancer est calculé avec
            les dernières évaluations : 40% dernière, 30% deuxième, 20%
            troisième et 10% quatrième. Si le freelancer a moins de 4
            évaluations, les poids sont normalisés.
          </p>
        </div>
      </div>
    </div>
  );
}

function FormulaCard({
  title,
  value,
  text,
}: {
  title: string;
  value: string;
  text: string;
}) {
  return (
    <div className="bg-slate-900 border border-white/10 rounded-2xl p-5">
      <p className="text-slate-400 text-sm">{title}</p>
      <p className="text-3xl font-bold text-blue-300 mt-2">{value}</p>
      <p className="text-slate-400 text-sm mt-3 leading-6">{text}</p>
    </div>
  );
}

function Info({ label, value }: { label: string; value: any }) {
  return (
    <div className="bg-slate-900 rounded-xl p-4">
      <p className="text-slate-400 text-sm">{label}</p>
      <p className="font-bold mt-1">{value}</p>
    </div>
  );
}

function Badge({ value }: { value?: string | null }) {
  const currentValue = String(value || "");

  const isSuccess = currentValue === "ON_TIME" || currentValue === "COMPLETED";
  const isWarning =
    currentValue === "LATE_DELIVERY" || currentValue === "IN_PROGRESS";
  const isDanger =
    currentValue === "CANCELLED" || currentValue === "DECLINING";

  const className = isSuccess
    ? "bg-green-500/20 text-green-300"
    : isWarning
    ? "bg-yellow-500/20 text-yellow-300"
    : isDanger
    ? "bg-red-500/20 text-red-300"
    : "bg-blue-500/20 text-blue-300";

  return (
    <span className={`px-3 py-1 rounded-full text-xs font-semibold ${className}`}>
      {formatLabel(currentValue)}
    </span>
  );
}

function buildGenericFreelancerDetail(
  freelancerId: number
): FreelancerDetailDto {
  return {
    freelancerId,
    fullName: `Freelancer ${freelancerId}`,
    skill: "IT",
    seniority: "Non renseigné",
    availabilityStatus: "AVAILABLE",
    companyName: "Entreprise non renseignée",
    yearsExperience: 0,
    finalScore: 0,
    recentEvaluationScore: 0,
    reliabilityScore: 0,
    trendScore: 0,
    experienceScore: 0,
    availabilityScore: 0,
    trendLabel: "STABLE",
    missionsCount: 0,
    evaluationsCount: 0,
    missionHistory: [],
  };
}

function deliveryScore(value?: DeliveryStatus | string | null) {
  if (value === "LATE_DELIVERY") return 2.5;
  if (value === "CANCELLED") return 0;
  return 5;
}

function calculateEvaluationScore(history: FreelancerMissionHistoryDto) {
  if (history.missionScore !== undefined && history.missionScore !== null) {
    return Number(history.missionScore);
  }

  return (
    0.3 * Number(history.technicalQuality || 0) +
    0.2 * Number(history.communication || 0) +
    0.15 * Number(history.autonomy || 0) +
    0.15 * Number(history.testQuality || 0) +
    0.2 * deliveryScore(history.deliveryStatus)
  );
}

function calculateFinalScoreFromLastFour(
  history: FreelancerMissionHistoryDto[]
) {
  const weights = [0.4, 0.3, 0.2, 0.1];

  let sum = 0;
  let weightSum = 0;

  history.slice(0, 4).forEach((evaluation, index) => {
    sum += calculateEvaluationScore(evaluation) * weights[index];
    weightSum += weights[index];
  });

  return weightSum === 0 ? 0 : sum / weightSum;
}

function formatLabel(value?: string | null) {
  if (!value) return "Non renseigné";

  return String(value)
    .replace(/_/g, " ")
    .toLowerCase()
    .replace(/\b\w/g, (letter) => letter.toUpperCase());
}

function formatDate(value?: string | null) {
  if (!value) return "Date non renseignée";

  const date = new Date(value);

  if (Number.isNaN(date.getTime())) {
    return "Date non renseignée";
  }

  return date.toLocaleDateString();
}

function formatNumber(value?: number | null) {
  return Math.round(Number(value || 0) * 100) / 100;
}

function getTime(value?: string | null) {
  if (!value) return 0;

  const date = new Date(value);

  if (Number.isNaN(date.getTime())) {
    return 0;
  }

  return date.getTime();
}