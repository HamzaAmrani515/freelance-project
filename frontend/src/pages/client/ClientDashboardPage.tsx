import React, { useEffect } from "react";
import { useNavigate } from "react-router-dom";
import { ClientMission, getMissionsForClient } from "../../auth/demoUsers";
import { clearCurrentUser, getCurrentUser } from "../../auth/session";

export default function ClientDashboardPage() {
  const navigate = useNavigate();
  const user = getCurrentUser();

  useEffect(() => {
    if (!user || user.role !== "CLIENT") {
      navigate("/login");
    }
  }, [navigate, user]);

  if (!user || user.role !== "CLIENT") {
    return null;
  }

  const missions = getMissionsForClient(user.clientId);

  const logout = () => {
    clearCurrentUser();
    navigate("/login");
  };

  const openFreelancerProfile = (mission: ClientMission) => {
    navigate(`/freelancers/${mission.freelancerId}/details`);
  };

  return (
    <div className="min-h-screen bg-slate-950 text-white p-8">
      <div className="max-w-7xl mx-auto">
        <div className="flex justify-between items-start mb-8">
          <div>
            <p className="text-blue-400 font-semibold">Espace client</p>

            <h1 className="text-4xl font-bold">{user.label}</h1>

            <p className="text-slate-400 mt-2 max-w-3xl leading-7">
              Le client voit ses missions, consulte le profil du freelancer affecté
              et peut ajouter une évaluation.
            </p>
          </div>

          <div className="flex gap-3">
            <button
              onClick={() => navigate("/client/recommendations")}
              className="bg-blue-600 hover:bg-blue-700 px-5 py-3 rounded-xl font-semibold"
            >
              Chercher des freelancers
            </button>

            <button
              onClick={logout}
              className="bg-red-500/20 hover:bg-red-500/30 text-red-300 px-5 py-3 rounded-xl font-semibold"
            >
              Déconnexion
            </button>
          </div>
        </div>

        <div className="grid grid-cols-3 gap-4 mb-8">
          <Stat title="Client" value={user.companyName || user.label} />
          <Stat title="Missions visibles" value={missions.length} />
          <Stat
            title="Missions évaluables"
            value={
              missions.filter((mission) => canEvaluateMission(mission.status))
                .length
            }
          />
        </div>

        <div className="bg-white/5 border border-white/10 rounded-2xl overflow-hidden">
          <div className="p-6 border-b border-white/10">
            <h2 className="text-2xl font-bold">Mes missions</h2>

            <p className="text-slate-400 mt-1 leading-7">
              Le client peut consulter le profil du freelancer et ajouter une
              évaluation sur une mission.
            </p>
          </div>

          <div className="overflow-x-auto">
            <table className="w-full text-sm">
              <thead className="bg-white/10 text-slate-300">
                <tr>
                  <th className="p-4 text-left">Mission</th>
                  <th className="p-4 text-left">Freelancer</th>
                  <th className="p-4 text-left">Skill</th>
                  <th className="p-4 text-left">Statut</th>
                  <th className="p-4 text-left">Contexte métier</th>
                  <th className="p-4 text-left">Actions</th>
                </tr>
              </thead>

              <tbody>
                {missions.map((mission) => (
                  <tr
                    key={mission.missionId}
                    className="border-t border-white/10 hover:bg-white/5"
                  >
                    <td className="p-4 font-semibold">
                      {mission.missionTitle}
                    </td>

                    <td className="p-4">{mission.freelancerName}</td>

                    <td className="p-4">{mission.skill}</td>

                    <td className="p-4">
                      <Badge value={mission.status} />
                    </td>

                    <td className="p-4 text-slate-300 max-w-sm leading-6">
                      {mission.businessContext}
                    </td>

                    <td className="p-4">
                      <div className="flex gap-3">
                        <button
                          onClick={() => openFreelancerProfile(mission)}
                          className="bg-slate-700 hover:bg-slate-600 px-4 py-2 rounded-lg"
                        >
                          Voir profil
                        </button>

                        {canEvaluateMission(mission.status) && (
                          <button
                            onClick={() =>
                              navigate(
                                `/evaluations/create?missionId=${mission.missionId}`
                              )
                            }
                            className="bg-blue-600 hover:bg-blue-700 px-4 py-2 rounded-lg"
                          >
                            {evaluationLabel(mission.status)}
                          </button>
                        )}
                      </div>
                    </td>
                  </tr>
                ))}

                {missions.length === 0 && (
                  <tr>
                    <td colSpan={6} className="p-8 text-center text-slate-400">
                      Aucune mission visible pour ce client.
                    </td>
                  </tr>
                )}
              </tbody>
            </table>
          </div>
        </div>

        <div className="mt-8 bg-blue-500/10 border border-blue-500/20 rounded-2xl p-6">
          <h2 className="text-xl font-bold">Logique métier client</h2>

          <p className="text-slate-300 mt-2 leading-7">
            Le client consulte les missions qui lui sont associées, visualise le
            profil du freelancer affecté, puis ajoute une évaluation. Le score du
            freelancer est ensuite recalculé côté backend et visible dans le profil
            ainsi que dans le ranking administrateur.
          </p>
        </div>
      </div>
    </div>
  );
}

function canEvaluateMission(status?: string | null) {
  return (
    status === "IN_PROGRESS" || status === "COMPLETED" || status === "AT_RISK"
  );
}

function evaluationLabel(status?: string | null) {
  if (status === "COMPLETED") return "Évaluation finale";
  if (status === "AT_RISK") return "Évaluer le risque";
  if (status === "IN_PROGRESS") return "Évaluer un jalon";

  return "Évaluer";
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
    <span className="px-3 py-1 rounded-full bg-blue-500/20 text-blue-300 text-xs font-semibold whitespace-nowrap">
      {formatLabel(value)}
    </span>
  );
}

function formatLabel(value?: string | null) {
  if (!value) return "Non renseigné";

  return value
    .replace(/_/g, " ")
    .toLowerCase()
    .replace(/\b\w/g, (letter) => letter.toUpperCase());
}