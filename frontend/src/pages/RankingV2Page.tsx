import React, { useEffect, useState } from "react";
import { useNavigate } from "react-router-dom";
import { getRanking, FreelancerRankingDto } from "../api/rankingV2Api";

export default function RankingV2Page() {
  const [ranking, setRanking] = useState<FreelancerRankingDto[]>([]);
  const [loading, setLoading] = useState(true);
  const navigate = useNavigate();

  const loadRanking = async () => {
    setLoading(true);

    try {
      const data = await getRanking(0, 100);
      setRanking(Array.isArray(data) ? data : []);
    } finally {
      setLoading(false);
    }
  };

  useEffect(() => {
    loadRanking();
  }, []);

  const openFreelancerDetails = (freelancer: FreelancerRankingDto) => {
    localStorage.setItem("selectedFreelancerDetail", JSON.stringify(freelancer));
    navigate(`/freelancers/${freelancer.freelancerId}/details`);
  };

  return (
    <div className="min-h-screen bg-slate-950 text-white p-8">
      <div className="max-w-7xl mx-auto">
        <div className="flex justify-between items-center mb-8">
          <div>
            <p className="text-blue-400 font-semibold">Admin Dashboard</p>
            <h1 className="text-4xl font-bold">Freelancer Ranking</h1>
            <p className="text-slate-400 mt-2">
              Classement des freelancers selon le score final calculé par le backend.
            </p>
          </div>

          <button
            onClick={loadRanking}
            className="bg-slate-800 hover:bg-slate-700 px-5 py-3 rounded-xl font-semibold"
          >
            Rafraîchir
          </button>
        </div>

        <div className="grid grid-cols-4 gap-4 mb-8">
          <Stat title="Freelancers affichés" value={ranking.length} />
          <Stat title="Leader" value={ranking[0]?.fullName || "-"} />
          <Stat title="Meilleur score" value={ranking[0]?.finalScore ?? "-"} />
          <Stat title="Source" value="Backend" />
        </div>

        <div className="bg-white/5 border border-white/10 rounded-2xl overflow-hidden">
          {loading ? (
            <div className="p-6">Chargement...</div>
          ) : (
            <table className="w-full text-sm">
              <thead className="bg-white/10 text-slate-300">
                <tr>
                  <th className="p-4 text-left">Rang</th>
                  <th className="p-4 text-left">Freelancer</th>
                  <th className="p-4 text-left">Skill</th>
                  <th className="p-4 text-left">Disponibilité</th>
                  <th className="p-4 text-left">Score final</th>
                  <th className="p-4 text-left">Tendance</th>
                  <th className="p-4 text-left">Missions</th>
                  <th className="p-4 text-left">Évaluations</th>
                  <th className="p-4 text-left">Action</th>
                </tr>
              </thead>

              <tbody>
                {ranking.map((freelancer, index) => (
                  <tr
                    key={freelancer.freelancerId}
                    className="border-t border-white/10 hover:bg-white/5"
                  >
                    <td className="p-4 font-bold">#{index + 1}</td>

                    <td className="p-4">
                      <div className="font-semibold">
                        {freelancer.fullName || "Nom non renseigné"}
                      </div>
                      <div className="text-slate-400">
                        {freelancer.companyName || "-"} ·{" "}
                        {freelancer.yearsExperience || 0} ans
                      </div>
                    </td>

                    <td className="p-4">{freelancer.skill || "-"}</td>

                    <td className="p-4">
                      <Badge value={freelancer.availabilityStatus} />
                    </td>

                    <td className="p-4 font-bold text-blue-300">
                      {formatNumber(freelancer.finalScore)}
                    </td>

                    <td className="p-4">
                      <Badge value={freelancer.trendLabel} />
                    </td>

                    <td className="p-4">{freelancer.missionsCount || 0}</td>

                    <td className="p-4">{freelancer.evaluationsCount || 0}</td>

                    <td className="p-4">
                      <button
                        onClick={() => openFreelancerDetails(freelancer)}
                        className="bg-indigo-600 hover:bg-indigo-700 px-4 py-2 rounded-lg"
                      >
                        Voir profil
                      </button>
                    </td>
                  </tr>
                ))}

                {ranking.length === 0 && (
                  <tr>
                    <td colSpan={9} className="p-8 text-center text-slate-400">
                      Aucun freelancer trouvé.
                    </td>
                  </tr>
                )}
              </tbody>
            </table>
          )}
        </div>
      </div>
    </div>
  );
}

function Stat({ title, value }: { title: string; value: any }) {
  return (
    <div className="bg-white/5 border border-white/10 rounded-2xl p-5">
      <p className="text-slate-400 text-sm">{title}</p>
      <p className="text-2xl font-bold mt-2">{value}</p>
    </div>
  );
}

function Badge({ value }: { value?: string | null }) {
  return (
    <span className="px-3 py-1 rounded-full bg-blue-500/20 text-blue-300 text-xs font-semibold">
      {formatLabel(value)}
    </span>
  );
}

function formatLabel(value?: string | null) {
  if (!value) return "Non renseigné";

  return String(value)
    .replace(/_/g, " ")
    .toLowerCase()
    .replace(/\b\w/g, (letter) => letter.toUpperCase());
}

function formatNumber(value?: number | null) {
  return Math.round(Number(value || 0) * 100) / 100;
}