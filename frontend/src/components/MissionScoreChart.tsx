import React from "react";
import { FreelancerMissionHistoryDto } from "../api/rankingV2Api";

export default function MissionScoreChart({
  history,
}: {
  history: FreelancerMissionHistoryDto[];
}) {
  const orderedHistory = [...history].reverse();

  if (orderedHistory.length === 0) {
    return null;
  }

  return (
    <div className="bg-white/5 border border-white/10 rounded-2xl p-6 mb-8">
      <h2 className="text-2xl font-bold">Évolution des scores mission</h2>

      <p className="text-slate-400 mt-1 mb-6">
        Visualisation simple de l’évolution des performances dans le temps.
      </p>

      <div className="space-y-4">
        {orderedHistory.map((item, index) => {
          const score = Number(item.missionScore || 0);
          const percentage = Math.max(5, Math.min(100, (score / 5) * 100));

          return (
            <div key={`${item.missionId}-${index}`}>
              <div className="flex justify-between text-sm mb-2">
                <span className="text-slate-300">
                  {index + 1}. {item.missionTitle || "Mission non renseignée"}
                </span>

                <span className="font-bold text-blue-300">
                  {score}/5
                </span>
              </div>

              <div className="h-3 bg-slate-800 rounded-full overflow-hidden">
                <div
                  className="h-full bg-blue-500 rounded-full"
                  style={{ width: `${percentage}%` }}
                />
              </div>

              <p className="text-xs text-slate-500 mt-1">
                {formatDate(item.evaluatedAt)} · {formatLabel(item.deliveryStatus)}
              </p>
            </div>
          );
        })}
      </div>
    </div>
  );
}

function formatDate(value?: string | null) {
  if (!value) {
    return "Date non renseignée";
  }

  return new Date(value).toLocaleDateString();
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