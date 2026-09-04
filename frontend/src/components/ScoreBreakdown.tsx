import React from "react";
import { FreelancerDetailDto } from "../api/rankingV2Api";

export default function ScoreBreakdown({ detail }: { detail: FreelancerDetailDto }) {
  const parts = [
    {
      label: "Qualité récente",
      weight: "45%",
      raw: detail.recentEvaluationScore,
      contribution: detail.recentEvaluationScore * 0.45,
      explanation: "Évaluations récentes pondérées plus fortement.",
    },
    {
      label: "Fiabilité",
      weight: "25%",
      raw: detail.reliabilityScore,
      contribution: detail.reliabilityScore * 0.25,
      explanation: "Impact des succès, retards et annulations.",
    },
    {
      label: "Tendance",
      weight: "15%",
      raw: detail.trendScore,
      contribution: detail.trendScore * 0.15,
      explanation: "Comparaison des 3 dernières évaluations avec les 3 précédentes.",
    },
    {
      label: "Expérience",
      weight: "10%",
      raw: detail.experienceScore,
      contribution: detail.experienceScore * 0.1,
      explanation: "Années d’expérience et volume de missions.",
    },
    {
      label: "Disponibilité",
      weight: "5%",
      raw: detail.availabilityScore,
      contribution: detail.availabilityScore * 0.05,
      explanation: "Disponible, partiellement disponible, occupé ou indisponible.",
    },
  ];

  const total = parts.reduce((sum, item) => sum + item.contribution, 0);

  return (
    <div className="bg-white/5 border border-white/10 rounded-2xl p-6 mb-8">
      <div className="flex justify-between items-start gap-4 mb-6">
        <div>
          <h2 className="text-2xl font-bold">Décomposition du score</h2>
          <p className="text-slate-400 mt-1">
            Cette section explique pourquoi le freelancer obtient ce score final.
          </p>
        </div>

        <div className="bg-blue-500/10 border border-blue-500/20 rounded-2xl p-4 text-right">
          <p className="text-slate-400 text-sm">Score recalculé</p>
          <p className="text-3xl font-bold text-blue-300">
            {Math.round(total * 100) / 100}
          </p>
        </div>
      </div>

      <div className="grid grid-cols-1 md:grid-cols-5 gap-4">
        {parts.map((part) => (
          <div key={part.label} className="bg-slate-900 border border-white/10 rounded-2xl p-4">
            <div className="flex justify-between items-center">
              <p className="font-bold">{part.label}</p>
              <span className="text-xs bg-blue-500/20 text-blue-300 px-2 py-1 rounded-full">
                {part.weight}
              </span>
            </div>

            <p className="text-slate-400 text-sm mt-3">Score brut : {part.raw}</p>

            <p className="text-blue-300 text-2xl font-bold mt-2">
              +{Math.round(part.contribution * 100) / 100}
            </p>

            <p className="text-slate-500 text-xs mt-3 leading-5">
              {part.explanation}
            </p>
          </div>
        ))}
      </div>
    </div>
  );
}