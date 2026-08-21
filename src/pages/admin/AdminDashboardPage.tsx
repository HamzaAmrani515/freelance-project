import React, { useCallback, useEffect, useMemo, useState } from "react";
import { useNavigate } from "react-router-dom";
import { FreelancerRankingDto, getRanking } from "../../api/rankingV2Api";
import { clearCurrentUser, getCurrentUser } from "../../auth/session";

const PAGE_SIZE = 50;

export default function AdminDashboardPage() {
  const navigate = useNavigate();
  const [user] = useState(() => getCurrentUser());

  const [ranking, setRanking] = useState<FreelancerRankingDto[]>([]);
  const [page, setPage] = useState(0);

  const [loading, setLoading] = useState(true);
  const [error, setError] = useState<string | null>(null);

  const [search, setSearch] = useState("");
  const [skillFilter, setSkillFilter] = useState("ALL");
  const [availabilityFilter, setAvailabilityFilter] = useState("ALL");
  const [seniorityFilter, setSeniorityFilter] = useState("ALL");
  const [trendFilter, setTrendFilter] = useState("ALL");

  const [showEvaluationAlgo, setShowEvaluationAlgo] = useState(false);

  const loadRanking = useCallback(async (targetPage: number) => {
    try {
      setLoading(true);
      setError(null);

      const data = await getRanking(targetPage, PAGE_SIZE);
      const safeData = Array.isArray(data) ? data : [];

      setRanking(safeData);
      setPage(targetPage);
    } catch (e: any) {
      setError(e.message || "Erreur pendant le chargement du ranking");
      setRanking([]);
    } finally {
      setLoading(false);
    }
  }, []);

  useEffect(() => {
    if (!user || user.role !== "ADMIN") {
      navigate("/login");
      return;
    }

    loadRanking(0);
  }, [navigate, user, loadRanking]);

  const logout = () => {
    clearCurrentUser();
    navigate("/login");
  };

  const openFreelancerDetails = (freelancer: FreelancerRankingDto) => {
    localStorage.setItem("selectedFreelancerDetail", JSON.stringify(freelancer));
    navigate(`/freelancers/${freelancer.freelancerId}/details`);
  };

  const filteredRanking = useMemo(() => {
    return ranking.filter((freelancer) => {
      const searchText = `${freelancer.fullName || ""} ${freelancer.skill || ""} ${
        freelancer.companyName || ""
      }`.toLowerCase();

      const matchesSearch =
        search.trim() === "" || searchText.includes(search.toLowerCase());

      const matchesSkill =
        skillFilter === "ALL" || freelancer.skill === skillFilter;

      const matchesAvailability =
        availabilityFilter === "ALL" ||
        freelancer.availabilityStatus === availabilityFilter;

      const matchesSeniority =
        seniorityFilter === "ALL" || freelancer.seniority === seniorityFilter;

      const matchesTrend =
        trendFilter === "ALL" || freelancer.trendLabel === trendFilter;

      return (
        matchesSearch &&
        matchesSkill &&
        matchesAvailability &&
        matchesSeniority &&
        matchesTrend
      );
    });
  }, [
    ranking,
    search,
    skillFilter,
    availabilityFilter,
    seniorityFilter,
    trendFilter,
  ]);

  const topProfiles = useMemo(() => ranking.slice(0, 4), [ranking]);

  const skills = useMemo(() => {
    return Array.from(new Set(ranking.map((f) => f.skill).filter(Boolean))).sort();
  }, [ranking]);

  const availabilities = useMemo(() => {
    return Array.from(
      new Set(ranking.map((f) => f.availabilityStatus).filter(Boolean))
    ).sort();
  }, [ranking]);

  const seniorities = useMemo(() => {
    return Array.from(new Set(ranking.map((f) => f.seniority).filter(Boolean))).sort();
  }, [ranking]);

  const trends = useMemo(() => {
    return Array.from(new Set(ranking.map((f) => f.trendLabel).filter(Boolean))).sort();
  }, [ranking]);

  const totalEvaluations = useMemo(() => {
    return ranking.reduce(
      (sum, freelancer) => sum + Number(freelancer.evaluationsCount || 0),
      0
    );
  }, [ranking]);

  const totalMissions = useMemo(() => {
    return ranking.reduce(
      (sum, freelancer) => sum + Number(freelancer.missionsCount || 0),
      0
    );
  }, [ranking]);

  const resetFilters = () => {
    setSearch("");
    setSkillFilter("ALL");
    setAvailabilityFilter("ALL");
    setSeniorityFilter("ALL");
    setTrendFilter("ALL");
  };

  if (!user || user.role !== "ADMIN") {
    return null;
  }

  return (
    <div className="min-h-screen bg-slate-950 text-white p-8">
      <div className="max-w-7xl mx-auto">
        <div className="flex justify-between items-start mb-8">
          <div>
            <p className="text-blue-400 font-semibold">Espace administrateur</p>

            <h1 className="text-4xl font-bold mt-1">
              Ranking des freelancers
            </h1>

            <p className="text-slate-400 mt-3 max-w-3xl leading-7">
              Suivi des profils selon le score final, la tendance, la disponibilité,
              les missions et les évaluations.
            </p>
          </div>

          <div className="flex gap-3">
            <button
              onClick={() => setShowEvaluationAlgo((previous) => !previous)}
              className="bg-blue-600 hover:bg-blue-700 px-5 py-3 rounded-xl font-semibold"
            >
              Algorithme score
            </button>

            <button
              onClick={logout}
              className="bg-red-500/20 hover:bg-red-500/30 text-red-300 px-5 py-3 rounded-xl font-semibold"
            >
              Déconnexion
            </button>
          </div>
        </div>

        {showEvaluationAlgo && (
          <AlgoBox
            title="Algorithme de calcul du score"
            onClose={() => setShowEvaluationAlgo(false)}
            steps={[
              ["1", "Technique", "30% du score de l’évaluation."],
              ["2", "Communication", "20% du score de l’évaluation."],
              ["3", "Autonomie", "15% du score de l’évaluation."],
              ["4", "Tests", "15% du score de l’évaluation."],
              ["5", "Livraison", "20% : à temps, retard ou annulée."],
            ]}
            summary="Score évaluation sur 5 = critères pondérés. Score final sur 100 = (70% nouvelle évaluation + 30% ancienne évaluation) × 20."
          />
        )}

        <div className="grid grid-cols-4 gap-4 mb-8">
          <Stat title="Profils chargés" value={ranking.length} />
          <Stat title="Missions affichées" value={totalMissions} />
          <Stat title="Évaluations affichées" value={totalEvaluations} />
          <Stat title="Pagination" value={`${PAGE_SIZE} / page`} />
        </div>

        <div className="mb-8">
          <div className="flex justify-between items-end mb-4">
            <div>
              <h2 className="text-2xl font-bold">Meilleurs profils de la page</h2>

              <p className="text-slate-400 mt-1">
                Ces profils viennent directement du ranking backend.
              </p>
            </div>

            <button
              onClick={() => loadRanking(0)}
              disabled={loading}
              className="bg-slate-800 hover:bg-slate-700 disabled:opacity-40 px-4 py-2 rounded-lg text-sm"
            >
              Actualiser
            </button>
          </div>

          {loading ? (
            <div className="bg-white/5 border border-white/10 rounded-2xl p-6 text-slate-300">
              Chargement des profils...
            </div>
          ) : topProfiles.length === 0 ? (
            <div className="bg-white/5 border border-white/10 rounded-2xl p-6 text-slate-300">
              Aucun profil disponible.
            </div>
          ) : (
            <div className="grid grid-cols-4 gap-4">
              {topProfiles.map((profile) => (
                <ShowcaseCard
                  key={profile.freelancerId}
                  profile={profile}
                  onOpen={() => openFreelancerDetails(profile)}
                />
              ))}
            </div>
          )}
        </div>

        <div className="bg-white/5 border border-white/10 rounded-2xl p-6 mb-8">
          <div className="flex justify-between items-start gap-6 mb-6">
            <div>
              <h2 className="text-2xl font-bold">Filtres</h2>

              <p className="text-slate-400 mt-1">
                Recherche simple sur les profils chargés dans la page courante.
              </p>
            </div>

            <button
              onClick={resetFilters}
              className="bg-slate-800 hover:bg-slate-700 px-4 py-2 rounded-lg text-sm"
            >
              Réinitialiser
            </button>
          </div>

          <div className="grid grid-cols-6 gap-4">
            <div className="col-span-2">
              <label className="text-sm text-slate-400">Recherche</label>

              <input
                value={search}
                onChange={(e) => setSearch(e.target.value)}
                placeholder="Nom, skill, entreprise..."
                className="input mt-2"
              />
            </div>

            <FilterSelect
              label="Skill"
              value={skillFilter}
              onChange={setSkillFilter}
              options={skills}
            />

            <FilterSelect
              label="Disponibilité"
              value={availabilityFilter}
              onChange={setAvailabilityFilter}
              options={availabilities}
            />

            <FilterSelect
              label="Seniorité"
              value={seniorityFilter}
              onChange={setSeniorityFilter}
              options={seniorities}
            />

            <FilterSelect
              label="Tendance"
              value={trendFilter}
              onChange={setTrendFilter}
              options={trends}
            />
          </div>
        </div>

        <div className="bg-white/5 border border-white/10 rounded-2xl overflow-hidden">
          <div className="p-6 border-b border-white/10 flex items-center justify-between">
            <div>
              <h2 className="text-2xl font-bold">Ranking global</h2>

              <p className="text-slate-400 mt-1">
                Page {page + 1} · {filteredRanking.length} profil(s) affiché(s)
              </p>
            </div>

            <div className="flex gap-3">
              <button
                onClick={() => loadRanking(Math.max(0, page - 1))}
                disabled={page === 0 || loading}
                className="bg-slate-800 hover:bg-slate-700 disabled:opacity-40 px-4 py-2 rounded-lg"
              >
                ← Précédent
              </button>

              <button
                onClick={() => loadRanking(page + 1)}
                disabled={loading || ranking.length < PAGE_SIZE}
                className="bg-blue-600 hover:bg-blue-700 disabled:opacity-40 px-4 py-2 rounded-lg"
              >
                Suivant →
              </button>
            </div>
          </div>

          {loading && (
            <div className="p-8 text-slate-300">Chargement du ranking...</div>
          )}

          {error && (
            <div className="p-8 text-red-300 bg-red-500/10 border-t border-red-500/20">
              {error}
            </div>
          )}

          {!loading && !error && (
            <div className="overflow-x-auto">
              <table className="w-full text-sm">
                <thead className="bg-white/10 text-slate-300">
                  <tr>
                    <th className="p-4 text-left">Rang</th>
                    <th className="p-4 text-left">Freelancer</th>
                    <th className="p-4 text-left">Skill</th>
                    <th className="p-4 text-left">Seniorité</th>
                    <th className="p-4 text-left">Disponibilité</th>
                    <th className="p-4 text-left">Score final</th>
                    <th className="p-4 text-left">Tendance</th>
                    <th className="p-4 text-left">Historique</th>
                    <th className="p-4 text-left">Action</th>
                  </tr>
                </thead>

                <tbody>
                  {filteredRanking.map((freelancer, index) => (
                    <tr
                      key={freelancer.freelancerId}
                      className="border-t border-white/10 hover:bg-white/5"
                    >
                      <td className="p-4 text-slate-400">
                        #{page * PAGE_SIZE + index + 1}
                      </td>

                      <td className="p-4">
                        <div className="font-bold">
                          {freelancer.fullName || "Nom non renseigné"}
                        </div>

                        <div className="text-slate-400 text-xs">
                          {freelancer.companyName || "-"} ·{" "}
                          {freelancer.yearsExperience || 0} ans
                        </div>
                      </td>

                      <td className="p-4">{freelancer.skill || "-"}</td>

                      <td className="p-4">
                        <Badge value={freelancer.seniority} />
                      </td>

                      <td className="p-4">
                        <Badge value={freelancer.availabilityStatus} />
                      </td>

                      <td className="p-4">
                        <ScoreValue freelancer={freelancer} />
                      </td>

                      <td className="p-4">
                        <Trend value={freelancer.trendLabel} />
                      </td>

                      <td className="p-4">
                        {freelancer.missionsCount || 0} missions ·{" "}
                        {freelancer.evaluationsCount || 0} évaluations
                      </td>

                      <td className="p-4">
                        <button
                          onClick={() => openFreelancerDetails(freelancer)}
                          className="bg-slate-700 hover:bg-slate-600 px-4 py-2 rounded-lg"
                        >
                          Voir détail
                        </button>
                      </td>
                    </tr>
                  ))}

                  {filteredRanking.length === 0 && (
                    <tr>
                      <td colSpan={9} className="p-8 text-center text-slate-400">
                        Aucun profil ne correspond aux filtres.
                      </td>
                    </tr>
                  )}
                </tbody>
              </table>
            </div>
          )}
        </div>

        <div className="mt-8 bg-white/5 border border-white/10 rounded-2xl p-6">
          <h2 className="text-xl font-bold">Règles métier</h2>

          <div className="grid grid-cols-3 gap-4 mt-4 text-sm text-slate-300">
            <Rule
              title="Évaluation"
              text="Technique, communication, autonomie, tests et livraison."
            />

            <Rule
              title="Score final"
              text="Le score est recalculé après chaque nouvelle évaluation."
            />

            <Rule
              title="Ranking"
              text="Les profils sont triés par score final décroissant."
            />
          </div>
        </div>
      </div>
    </div>
  );
}

function ShowcaseCard({
  profile,
  onOpen,
}: {
  profile: FreelancerRankingDto;
  onOpen: () => void;
}) {
  return (
    <div className="bg-white/5 border border-white/10 hover:border-blue-500/50 rounded-3xl p-5 transition">
      <div className="flex items-start justify-between">
        <div>
          <h3 className="text-xl font-bold">
            {profile.fullName || "Nom non renseigné"}
          </h3>

          <p className="text-blue-300 text-sm mt-1">{profile.skill || "-"}</p>

          <p className="text-slate-400 text-sm mt-1">
            {profile.companyName || "-"}
          </p>
        </div>

        <Trend value={profile.trendLabel} />
      </div>

      <div className="mt-5">
        <p className="text-slate-400 text-sm">Score final</p>
        <ScoreValue freelancer={profile} />
      </div>

      <div className="mt-4">
        <Badge value={profile.availabilityStatus} />
      </div>

      <div className="grid grid-cols-2 gap-3 mt-5 text-sm">
        <MiniStat label="Missions" value={profile.missionsCount || 0} />
        <MiniStat label="Évaluations" value={profile.evaluationsCount || 0} />
      </div>

      <button
        onClick={onOpen}
        className="mt-5 w-full bg-blue-600 hover:bg-blue-700 px-4 py-3 rounded-xl font-semibold"
      >
        Voir le profil
      </button>
    </div>
  );
}

function ScoreValue({ freelancer }: { freelancer: FreelancerRankingDto }) {
  const score = Number(freelancer.finalScore || 0);

  return (
    <p className="text-3xl font-bold text-blue-300">
      {formatNumber(score)}
    </p>
  );
}

function AlgoBox({
  title,
  steps,
  summary,
  onClose,
}: {
  title: string;
  steps: string[][];
  summary: string;
  onClose: () => void;
}) {
  return (
    <div className="mb-8 bg-blue-500/10 border border-blue-500/20 rounded-2xl p-6">
      <div className="flex justify-between items-start gap-6">
        <div>
          <h2 className="text-2xl font-bold">{title}</h2>
        </div>

        <button
          onClick={onClose}
          className="bg-slate-800 hover:bg-slate-700 px-4 py-2 rounded-lg text-sm"
        >
          Fermer
        </button>
      </div>

      <div className="grid grid-cols-5 gap-4 mt-6">
        {steps.map(([number, stepTitle, text]) => (
          <AlgoStep
            key={number}
            number={number}
            title={stepTitle}
            text={text}
          />
        ))}
      </div>

      <div className="mt-6 bg-slate-900 border border-white/10 rounded-xl p-5 text-sm text-slate-300 leading-7">
        {summary}
      </div>
    </div>
  );
}

function AlgoStep({
  number,
  title,
  text,
}: {
  number: string;
  title: string;
  text: string;
}) {
  return (
    <div className="bg-slate-900 border border-white/10 rounded-xl p-4">
      <div className="w-8 h-8 rounded-full bg-blue-600 flex items-center justify-center font-bold">
        {number}
      </div>

      <p className="font-bold mt-3">{title}</p>
      <p className="text-slate-400 text-sm mt-2 leading-6">{text}</p>
    </div>
  );
}

function MiniStat({ label, value }: { label: string; value: any }) {
  return (
    <div className="bg-slate-900 rounded-xl p-3">
      <p className="text-slate-400 text-xs">{label}</p>
      <p className="font-bold mt-1">{value}</p>
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

function FilterSelect({
  label,
  value,
  onChange,
  options,
}: {
  label: string;
  value: string;
  onChange: (value: string) => void;
  options: string[];
}) {
  return (
    <div>
      <label className="text-sm text-slate-400">{label}</label>

      <select
        value={value}
        onChange={(e) => onChange(e.target.value)}
        className="input mt-2"
      >
        <option value="ALL">Tous</option>

        {options.map((option) => (
          <option key={option} value={option}>
            {formatLabel(option)}
          </option>
        ))}
      </select>
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

function Trend({ value }: { value?: string | null }) {
  const isImproving = value === "IMPROVING";
  const isDeclining = value === "DECLINING";

  const className = isImproving
    ? "bg-green-500/20 text-green-300"
    : isDeclining
    ? "bg-red-500/20 text-red-300"
    : "bg-slate-500/20 text-slate-300";

  return (
    <span className={`px-3 py-1 rounded-full text-xs font-semibold whitespace-nowrap ${className}`}>
      {formatLabel(value)}
    </span>
  );
}

function Rule({ title, text }: { title: string; text: string }) {
  return (
    <div className="bg-slate-900 rounded-xl p-4">
      <p className="font-bold text-white">{title}</p>
      <p className="text-slate-400 mt-2 leading-6">{text}</p>
    </div>
  );
}

function formatLabel(value?: string | null) {
  if (!value) {
    return "Non renseigné";
  }

  return String(value)
    .replaceAll("_", " ")
    .toLowerCase()
    .replace(/\b\w/g, (letter) => letter.toUpperCase());
}

function formatNumber(value?: number | null) {
  const numberValue = Number(value || 0);
  return Math.round(numberValue * 100) / 100;
}