import React, { useEffect, useMemo, useState } from "react";
import { useNavigate } from "react-router-dom";
import { FreelancerRankingDto, getRanking } from "../../api/rankingV2Api";
import { getCurrentUser } from "../../auth/session";

export default function ClientFreelancerSearchPage() {
  const navigate = useNavigate();
  const user = getCurrentUser();

  const [ranking, setRanking] = useState<FreelancerRankingDto[]>([]);
  const [loading, setLoading] = useState(true);
  const [searched, setSearched] = useState(false);

  const [skill, setSkill] = useState("ALL");
  const [seniority, setSeniority] = useState("ALL");
  const [minExperience, setMinExperience] = useState(0);

  useEffect(() => {
    if (!user || user.role !== "CLIENT") {
      navigate("/login");
      return;
    }

    getRanking(0, 200)
      .then((data) => setRanking(Array.isArray(data) ? data : []))
      .finally(() => setLoading(false));
  }, [navigate, user]);

  const skills = useMemo(() => {
    return Array.from(new Set(ranking.map((f) => f.skill).filter(Boolean))).sort();
  }, [ranking]);

  const seniorities = useMemo(() => {
    return Array.from(new Set(ranking.map((f) => f.seniority).filter(Boolean))).sort();
  }, [ranking]);

  const recommendedFreelancers = useMemo(() => {
    return ranking
      .filter((freelancer) => freelancer.availabilityStatus === "AVAILABLE")
      .filter((freelancer) => skill === "ALL" || freelancer.skill === skill)
      .filter((freelancer) => seniority === "ALL" || freelancer.seniority === seniority)
      .filter((freelancer) => Number(freelancer.yearsExperience || 0) >= minExperience)
      .sort((a, b) => Number(b.finalScore || 0) - Number(a.finalScore || 0))
      .slice(0, 5);
  }, [ranking, skill, seniority, minExperience]);

  const openDetails = (freelancer: FreelancerRankingDto) => {
    localStorage.setItem("selectedFreelancerDetail", JSON.stringify(freelancer));
    navigate(`/freelancers/${freelancer.freelancerId}/details`);
  };

  if (!user || user.role !== "CLIENT") {
    return null;
  }

  return (
    <div className="min-h-screen bg-slate-950 text-white p-8">
      <div className="max-w-7xl mx-auto">
        <button
          onClick={() => navigate("/client/dashboard")}
          className="mb-6 text-blue-400 hover:text-blue-300"
        >
          ← Retour espace client
        </button>

        <div className="bg-gradient-to-r from-blue-600 to-indigo-700 rounded-3xl p-8 mb-8">
          <p className="text-blue-100 font-semibold">Espace client</p>

          <h1 className="text-4xl font-bold mt-2">
            Chercher des freelancers
          </h1>

          <p className="text-blue-100 mt-3 max-w-3xl leading-7">
            Le client choisit les critères. Le système affiche les 5 meilleurs
            freelancers disponibles selon le score final.
          </p>
        </div>

        <div className="bg-white/5 border border-white/10 rounded-2xl p-6 mb-8">
          <h2 className="text-2xl font-bold mb-6">Critères de recherche</h2>

          <div className="grid grid-cols-4 gap-4">
            <Field label="Skill demandé">
              <select
                value={skill}
                onChange={(e) => setSkill(e.target.value)}
                className="input"
              >
                <option value="ALL">Tous les skills</option>
                {skills.map((item) => (
                  <option key={item} value={item}>
                    {item}
                  </option>
                ))}
              </select>
            </Field>

            <Field label="Seniorité">
              <select
                value={seniority}
                onChange={(e) => setSeniority(e.target.value)}
                className="input"
              >
                <option value="ALL">Toutes les seniorités</option>
                {seniorities.map((item) => (
                  <option key={item} value={item}>
                    {formatLabel(item)}
                  </option>
                ))}
              </select>
            </Field>

            <Field label="Expérience minimale">
              <input
                type="number"
                min={0}
                value={minExperience}
                onChange={(e) => setMinExperience(Number(e.target.value))}
                className="input"
              />
            </Field>

            <div className="flex items-end">
              <button
                onClick={() => setSearched(true)}
                className="w-full bg-blue-600 hover:bg-blue-700 px-5 py-3 rounded-xl font-semibold"
              >
                Voir les freelancers recommandés
              </button>
            </div>
          </div>
        </div>

        {loading && (
          <div className="bg-white/5 border border-white/10 rounded-2xl p-6">
            Chargement des freelancers...
          </div>
        )}

        {!loading && searched && (
          <div className="bg-white/5 border border-white/10 rounded-2xl overflow-hidden">
            <div className="p-6 border-b border-white/10">
              <h2 className="text-2xl font-bold">
                Top 5 freelancers disponibles
              </h2>

              <p className="text-slate-400 mt-1">
                Résultat filtré par besoin client, disponibilité et score final.
              </p>
            </div>

            <div className="grid grid-cols-5 gap-4 p-6">
              {recommendedFreelancers.map((freelancer, index) => (
                <div
                  key={freelancer.freelancerId}
                  className="bg-slate-900 border border-white/10 rounded-2xl p-5"
                >
                  <p className="text-blue-400 font-bold">#{index + 1}</p>

                  <h3 className="text-xl font-bold mt-2">
                    {freelancer.fullName || "Nom non renseigné"}
                  </h3>

                  <p className="text-slate-400 text-sm mt-1">
                    {freelancer.companyName || "-"}
                  </p>

                  <div className="mt-4 space-y-2 text-sm">
                    <Info label="Skill" value={freelancer.skill || "-"} />
                    <Info label="Seniorité" value={formatLabel(freelancer.seniority)} />
                    <Info label="Expérience" value={`${freelancer.yearsExperience || 0} ans`} />
                    <Info label="Disponibilité" value={formatLabel(freelancer.availabilityStatus)} />
                  </div>

                  <div className="mt-5">
                    <p className="text-slate-400 text-sm">Score final</p>
                    <p className="text-4xl font-bold text-blue-300">
                      {formatNumber(freelancer.finalScore)}
                    </p>
                  </div>

                  <button
                    onClick={() => openDetails(freelancer)}
                    className="mt-5 w-full bg-slate-700 hover:bg-slate-600 px-4 py-3 rounded-xl font-semibold"
                  >
                    Voir profil
                  </button>
                </div>
              ))}

              {recommendedFreelancers.length === 0 && (
                <div className="col-span-5 text-center text-slate-400 p-8">
                  Aucun freelancer disponible ne correspond aux critères.
                </div>
              )}
            </div>
          </div>
        )}

        {!loading && !searched && (
          <div className="bg-blue-500/10 border border-blue-500/20 rounded-2xl p-6">
            <h2 className="text-xl font-bold">Algorithme de recommandation</h2>

            <p className="text-slate-300 mt-3 leading-7">
              Recommandation = besoin client + disponibilité + score final.
              Le système affiche seulement les freelancers disponibles et garde
              les 5 meilleurs profils.
            </p>
          </div>
        )}
      </div>
    </div>
  );
}

function Field({
  label,
  children,
}: {
  label: string;
  children: React.ReactNode;
}) {
  return (
    <label className="block">
      <p className="text-sm text-slate-300 mb-2">{label}</p>
      {children}
    </label>
  );
}

function Info({ label, value }: { label: string; value: any }) {
  return (
    <div>
      <p className="text-slate-500">{label}</p>
      <p className="font-semibold">{value}</p>
    </div>
  );
}

function formatLabel(value?: string | null) {
  if (!value) return "-";

  return String(value)
    .replaceAll("_", " ")
    .toLowerCase()
    .replace(/\b\w/g, (letter) => letter.toUpperCase());
}

function formatNumber(value?: number | null) {
  const numberValue = Number(value || 0);
  return Math.round(numberValue * 100) / 100;
}