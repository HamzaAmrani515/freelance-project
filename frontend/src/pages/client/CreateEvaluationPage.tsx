import React, { useMemo, useState } from "react";
import { useNavigate, useSearchParams } from "react-router-dom";
import {
  createEvaluation,
  DeliveryStatus,
  EvaluationResponse,
  FreelancerMissionHistoryDto,
} from "../../api/rankingV2Api";
import { toast } from "react-toastify";
import { getCurrentUser } from "../../auth/session";
import { getMissionById, getMissionsForClient } from "../../auth/demoUsers";

export default function CreateEvaluationPage() {
  const navigate = useNavigate();
  const [searchParams] = useSearchParams();

  const user = getCurrentUser();

  const clientMissions =
    user?.role === "CLIENT" ? getMissionsForClient(user.clientId) : [];

  const missionIdFromUrl = Number(
    searchParams.get("missionId") || clientMissions[0]?.missionId || 1
  );

  const missionAllowed = clientMissions.some(
    (mission) => mission.missionId === missionIdFromUrl
  );

  const initialMissionId = missionAllowed
    ? missionIdFromUrl
    : clientMissions[0]?.missionId || 1;

  const [submitting, setSubmitting] = useState(false);

  const [form, setForm] = useState({
    missionId: initialMissionId,
    technicalQuality: 5,
    communication: 5,
    autonomy: 4,
    testQuality: 5,
    deliveryStatus: "ON_TIME" as DeliveryStatus,
    feedback: "Très bonne livraison, client satisfait.",
  });

  const selectedMission = useMemo(() => {
    return getMissionById(form.missionId);
  }, [form.missionId]);

  const previewScore = calculateEvaluationScore(
    form.technicalQuality,
    form.communication,
    form.autonomy,
    form.testQuality,
    form.deliveryStatus
  );

  if (!user || user.role !== "CLIENT") {
    return (
      <div className="min-h-screen bg-slate-950 text-white p-8">
        <div className="max-w-3xl mx-auto bg-red-500/10 border border-red-500/20 rounded-3xl p-8">
          <h1 className="text-3xl font-bold text-red-300">Accès refusé</h1>

          <p className="text-slate-300 mt-3">
            Seul un client peut créer une évaluation sur une mission.
          </p>

          <button
            onClick={() => navigate("/login")}
            className="mt-6 bg-blue-600 hover:bg-blue-700 px-5 py-3 rounded-xl font-semibold"
          >
            Retour login
          </button>
        </div>
      </div>
    );
  }

  const submit = async () => {
    if (!selectedMission) {
      toast.error("Mission introuvable.");
      return;
    }

    try {
      setSubmitting(true);

      const res = await createEvaluation(form);

      saveEvaluationHistory(res, {
        missionId: form.missionId,
        missionTitle: selectedMission.missionTitle,
        deliveryStatus: form.deliveryStatus,
        note: Math.round(previewScore),
        technicalQuality: form.technicalQuality,
        communication: form.communication,
        autonomy: form.autonomy,
        testQuality: form.testQuality,
        missionScore: previewScore,
        feedback: form.feedback,
        evaluatedAt: new Date().toISOString(),
      });

      localStorage.removeItem("selectedFreelancerDetail");

      toast.success(
        `Évaluation envoyée. Score final : ${res.finalScore}. Tendance : ${formatLabel(
          res.trendLabel
        )}.`
      );

      navigate(`/freelancers/${res.freelancerId}/details`);
    } catch (e: any) {
      toast.error(e.message || "Erreur pendant la création de l'évaluation");
    } finally {
      setSubmitting(false);
    }
  };

  const setValue = (field: keyof typeof form, value: number | string) => {
    setForm((previous) => ({
      ...previous,
      [field]: value,
    }));
  };

  return (
    <div className="min-h-screen bg-slate-950 text-white p-8">
      <div className="max-w-6xl mx-auto">
        <button
          onClick={() => navigate("/client/dashboard")}
          className="mb-6 text-blue-400 hover:text-blue-300"
        >
          ← Retour espace client
        </button>

        <div className="grid grid-cols-[1.1fr_0.9fr] gap-8">
          <div className="bg-white/5 border border-white/10 rounded-3xl p-8">
            <p className="text-blue-400 font-semibold">Évaluation client</p>

            <h1 className="text-4xl font-bold mb-2">Créer une évaluation</h1>

            <p className="text-slate-400 mb-8 leading-7">
              Le client évalue le freelancer affecté. Le score final est recalculé
              côté backend.
            </p>

            <div className="grid gap-6">
              <Field label="Mission à évaluer">
                <select
                  value={form.missionId}
                  onChange={(e) => setValue("missionId", Number(e.target.value))}
                  className="input"
                >
                  {clientMissions.map((mission) => (
                    <option key={mission.missionId} value={mission.missionId}>
                      Mission #{mission.missionId} — {mission.missionTitle} —{" "}
                      {mission.freelancerName}
                    </option>
                  ))}
                </select>
              </Field>

              <Slider
                label="Qualité technique"
                description="Qualité du code, solution technique, robustesse."
                value={form.technicalQuality}
                onChange={(v) => setValue("technicalQuality", v)}
              />

              <Slider
                label="Communication"
                description="Clarté des échanges, reporting, réactivité."
                value={form.communication}
                onChange={(v) => setValue("communication", v)}
              />

              <Slider
                label="Autonomie"
                description="Capacité à avancer sans blocage constant."
                value={form.autonomy}
                onChange={(v) => setValue("autonomy", v)}
              />

              <Slider
                label="Qualité des tests"
                description="Tests, validation, couverture fonctionnelle."
                value={form.testQuality}
                onChange={(v) => setValue("testQuality", v)}
              />

              <Field label="Statut de livraison">
                <select
                  value={form.deliveryStatus}
                  onChange={(e) =>
                    setValue("deliveryStatus", e.target.value as DeliveryStatus)
                  }
                  className="input"
                >
                  <option value="ON_TIME">Livrée à temps</option>
                  <option value="LATE_DELIVERY">Livrée en retard</option>
                  <option value="CANCELLED">Mission annulée</option>
                </select>
              </Field>

              <Field label="Feedback client">
                <textarea
                  value={form.feedback}
                  onChange={(e) => setValue("feedback", e.target.value)}
                  className="input min-h-[130px]"
                  placeholder="Ajouter un commentaire métier..."
                />
              </Field>

              <button
                onClick={submit}
                disabled={submitting || clientMissions.length === 0}
                className="bg-blue-600 hover:bg-blue-700 disabled:opacity-60 px-6 py-4 rounded-xl font-bold"
              >
                {submitting ? "Envoi en cours..." : "Envoyer l’évaluation"}
              </button>
            </div>
          </div>

          <div className="space-y-6">
            <div className="bg-gradient-to-br from-blue-600 to-indigo-700 rounded-3xl p-8">
              <p className="text-blue-100 font-semibold">Prévisualisation</p>

              <div className="text-6xl font-bold mt-4">{previewScore}</div>

              <p className="text-blue-100 mt-2">Score évaluation sur 5</p>
            </div>

            <div className="bg-white/5 border border-white/10 rounded-3xl p-6">
              <h2 className="text-xl font-bold mb-4">Mission sélectionnée</h2>

              <Info label="Client" value={selectedMission?.clientName || "-"} />
              <Info label="Mission" value={selectedMission?.missionTitle || "-"} />
              <Info label="Freelancer" value={selectedMission?.freelancerName || "-"} />
              <Info label="Statut livraison" value={formatLabel(form.deliveryStatus)} />
            </div>

            <div className="bg-white/5 border border-white/10 rounded-3xl p-6">
              <h2 className="text-xl font-bold mb-4">Formule utilisée</h2>

              <div className="text-sm text-slate-300 leading-7">
                <p>30% qualité technique</p>
                <p>20% communication</p>
                <p>15% autonomie</p>
                <p>15% qualité des tests</p>
                <p>20% statut de livraison</p>
              </div>

              <div className="mt-5 p-4 bg-blue-500/10 border border-blue-500/20 rounded-xl text-sm text-slate-300 leading-6">
                Score final = calculé côté backend selon les dernières évaluations.
              </div>

              <div className="mt-5 p-4 bg-slate-900 rounded-xl text-sm text-slate-300 leading-6">
                Score actuel de cette évaluation :{" "}
                <span className="font-bold text-blue-300">{previewScore}/5</span>
              </div>
            </div>
          </div>
        </div>
      </div>
    </div>
  );
}

function saveEvaluationHistory(
  res: EvaluationResponse,
  evaluation: FreelancerMissionHistoryDto
) {
  const key = `evaluationHistory_${res.freelancerId}`;
  const saved = localStorage.getItem(key);

  let history: FreelancerMissionHistoryDto[] = [];

  if (saved) {
    try {
      history = JSON.parse(saved);
    } catch {
      history = [];
    }
  }

  const updatedHistory = [evaluation, ...history];
  localStorage.setItem(key, JSON.stringify(updatedHistory));
}

function calculateEvaluationScore(
  technicalQuality: number,
  communication: number,
  autonomy: number,
  testQuality: number,
  deliveryStatus: DeliveryStatus
) {
  const deliveryScore =
    deliveryStatus === "LATE_DELIVERY"
      ? 2.5
      : deliveryStatus === "CANCELLED"
      ? 0
      : 5;

  const score =
    0.3 * technicalQuality +
    0.2 * communication +
    0.15 * autonomy +
    0.15 * testQuality +
    0.2 * deliveryScore;

  return Math.round(score * 100) / 100;
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

function Slider({
  label,
  description,
  value,
  onChange,
}: {
  label: string;
  description: string;
  value: number;
  onChange: (v: number) => void;
}) {
  return (
    <div className="bg-slate-900/70 border border-white/10 rounded-2xl p-4">
      <div className="flex justify-between mb-1">
        <span className="text-white font-semibold">{label}</span>
        <span className="font-bold text-blue-300">{value}/5</span>
      </div>

      <p className="text-slate-500 text-sm mb-3">{description}</p>

      <input
        type="range"
        min={1}
        max={5}
        value={value}
        onChange={(e) => onChange(Number(e.target.value))}
        className="w-full"
      />
    </div>
  );
}

function Info({ label, value }: { label: string; value: any }) {
  return (
    <div className="bg-slate-900 rounded-xl p-4 mb-3">
      <p className="text-slate-400 text-sm">{label}</p>
      <p className="font-bold mt-1">{value}</p>
    </div>
  );
}

function formatLabel(value?: string | null) {
  if (!value) return "Non renseigné";

  return value
    .replace(/_/g, " ")
    .toLowerCase()
    .replace(/\b\w/g, (letter) => letter.toUpperCase());
}