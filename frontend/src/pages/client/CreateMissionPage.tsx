import React, { useMemo, useState } from "react";
import { toast } from "react-toastify";
import { http } from "../../api/http";

function parseSkills(input: string): { nom: string }[] {
  return input
    .split(",")
    .map((s) => s.trim())
    .filter(Boolean)
    .map((nom) => ({ nom }));
}

export default function CreateMissionPage() {
  const [titre, setTitre] = useState("DEMO - Mission pour dve une app web");
  const [description, setDescription] = useState("Test notif : matching competences");
  const [budget, setBudget] = useState<number>(5000);
  const [duree, setDuree] = useState("1 mois");
  const [clientId, setClientId] = useState<number>(1);
  const [skills, setSkills] = useState("Java, Spring Boot");
  const [loading, setLoading] = useState(false);

  const payload = useMemo(
    () => ({
      titre,
      description,
      budget,
      duree,
      statut: "EN_ATTENTE",
      client: { id: clientId },
      competences: parseSkills(skills),
    }),
    [titre, description, budget, duree, clientId, skills]
  );

  const onSubmit = async (e: React.FormEvent) => {
    e.preventDefault();

    if (!titre.trim()) return toast.error("Titre obligatoire.");
    if (!description.trim()) return toast.error("Description obligatoire.");
    if (!budget || Number.isNaN(budget)) return toast.error("Budget invalide.");
    if (!duree.trim()) return toast.error("Durée obligatoire.");
    if (!clientId || Number.isNaN(clientId)) return toast.error("Client ID invalide.");
    if (parseSkills(skills).length === 0) return toast.error("Ajoute au moins 1 compétence.");

    setLoading(true);
    try {
      const created = await http<any>("/api/missions/post", {
        method: "POST",
        body: JSON.stringify(payload),
      });

      toast.success("✅ Mission créée avec succès");

      const missionId = Number(created?.id);
      if (missionId && !Number.isNaN(missionId)) {
        try {
          await http<string>(`/api/notifications/v2/mission/${missionId}/dispatch`, {
            method: "POST",
          });
        } catch {}
      }

      setDescription("");
    } catch (err: any) {
      toast.error(err?.message ?? "Erreur création mission.");
    } finally {
      setLoading(false);
    }
  };

  return (
    <div className="min-h-screen bg-gray-50 py-10 px-4">
      <div className="max-w-2xl mx-auto">
        <div className="bg-white border rounded-2xl shadow-sm p-6">
          <h1 className="text-2xl font-semibold mb-6">Créer une mission</h1>

          <form onSubmit={onSubmit} className="space-y-4">
            <div>
              <label className="text-sm font-medium">Titre</label>
              <input
                value={titre}
                onChange={(e) => setTitre(e.target.value)}
                className="mt-1 w-full border rounded-xl px-3 py-2 focus:outline-none focus:ring-2 focus:ring-black"
              />
            </div>

            <div>
              <label className="text-sm font-medium">Description</label>
              <textarea
                value={description}
                onChange={(e) => setDescription(e.target.value)}
                className="mt-1 w-full border rounded-xl px-3 py-2 min-h-[140px] focus:outline-none focus:ring-2 focus:ring-black"
              />
            </div>

            <div className="grid grid-cols-1 sm:grid-cols-2 gap-4">
              <div>
                <label className="text-sm font-medium">Budget</label>
                <input
                  type="number"
                  value={budget}
                  onChange={(e) => setBudget(Number(e.target.value))}
                  className="mt-1 w-full border rounded-xl px-3 py-2 focus:outline-none focus:ring-2 focus:ring-black"
                />
              </div>

              <div>
                <label className="text-sm font-medium">Durée</label>
                <input
                  value={duree}
                  onChange={(e) => setDuree(e.target.value)}
                  className="mt-1 w-full border rounded-xl px-3 py-2 focus:outline-none focus:ring-2 focus:ring-black"
                />
              </div>
            </div>

            <div>
              <label className="text-sm font-medium">Client ID</label>
              <input
                type="number"
                value={clientId}
                onChange={(e) => setClientId(Number(e.target.value))}
                className="mt-1 w-full border rounded-xl px-3 py-2 focus:outline-none focus:ring-2 focus:ring-black"
              />
            </div>

            <div>
              <label className="text-sm font-medium">Compétences (séparées par virgule)</label>
              <input
                value={skills}
                onChange={(e) => setSkills(e.target.value)}
                className="mt-1 w-full border rounded-xl px-3 py-2 focus:outline-none focus:ring-2 focus:ring-black"
              />
            </div>

            <button
              type="submit"
              disabled={loading}
              className="w-full px-5 py-3 rounded-xl bg-green-600 text-white hover:bg-green-700 disabled:opacity-60"
            >
              {loading ? "Création..." : "Créer la mission"}
            </button>
          </form>
        </div>
      </div>
    </div>
  );
}