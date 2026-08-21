import React, { useMemo, useState } from "react";
import { useNavigate } from "react-router-dom";
import { useNotifications } from "../../context/NotificationContext";

type Filter = "all" | "unread" | "read";

export default function FreelanceNotificationsPage() {
  const navigate = useNavigate();
  const { loading, notifications, unread, refresh, markRead } = useNotifications();
  const [filter, setFilter] = useState<Filter>("all");
  const [q, setQ] = useState("");

  const filtered = useMemo(() => {
    let list = [...notifications];

    if (filter === "unread") list = list.filter((n) => !n.read);
    if (filter === "read") list = list.filter((n) => n.read);

    const query = q.trim().toLowerCase();
    if (query) {
      list = list.filter((n) =>
        `${n.message} ${n.missionTitle ?? ""}`.toLowerCase().includes(query)
      );
    }

    list.sort((a, b) => (a.timestamp < b.timestamp ? 1 : -1));
    return list;
  }, [notifications, filter, q]);

  return (
    <div className="space-y-4">
      <div className="flex flex-col sm:flex-row sm:items-center sm:justify-between gap-3">
        <div>
          <h1 className="text-2xl font-semibold">Notifications</h1>
          <p className="text-sm text-gray-600">
            Non lues: <span className="font-semibold">{unread}</span>
          </p>
        </div>

        <div className="flex items-center gap-2">
          <button
            onClick={() => navigate("/client/missions/create")}
            className="px-4 py-2 rounded-lg bg-green-600 text-white hover:bg-green-700"
          >
            Créer mission
          </button>

          <button
            onClick={refresh}
            className="px-4 py-2 rounded-lg bg-blue-600 text-white hover:bg-blue-700"
          >
            Rafraîchir
          </button>
        </div>
      </div>

      <div className="bg-white rounded-xl border p-4 flex flex-col md:flex-row md:items-center gap-3">
        <input
          value={q}
          onChange={(e) => setQ(e.target.value)}
          placeholder="Rechercher (message, mission...)"
          className="w-full md:w-1/2 px-3 py-2 rounded-lg border"
        />

        <div className="flex gap-2">
          <button
            onClick={() => setFilter("all")}
            className={`px-3 py-2 rounded-lg border text-sm ${
              filter === "all" ? "bg-gray-900 text-white border-gray-900" : ""
            }`}
          >
            Toutes
          </button>
          <button
            onClick={() => setFilter("unread")}
            className={`px-3 py-2 rounded-lg border text-sm ${
              filter === "unread" ? "bg-gray-900 text-white border-gray-900" : ""
            }`}
          >
            Non lues
          </button>
          <button
            onClick={() => setFilter("read")}
            className={`px-3 py-2 rounded-lg border text-sm ${
              filter === "read" ? "bg-gray-900 text-white border-gray-900" : ""
            }`}
          >
            Lues
          </button>
        </div>
      </div>

      {loading ? (
        <div className="bg-white rounded-xl border p-6">Chargement...</div>
      ) : filtered.length === 0 ? (
        <div className="bg-white rounded-xl border p-10 text-center">
          <div className="text-lg font-semibold">Rien à afficher</div>
          <div className="text-sm text-gray-600 mt-1">
            Change le filtre ou la recherche.
          </div>
        </div>
      ) : (
        <div className="bg-white rounded-xl border divide-y">
          {filtered.map((n) => (
            <div key={n.id} className="p-4 flex items-start justify-between gap-4">
              <div className="min-w-0">
                <div className="flex items-center gap-2">
                  <span
                    className={`inline-flex px-2 py-0.5 rounded-full text-xs font-semibold ${
                      n.read
                        ? "bg-green-50 text-green-700"
                        : "bg-orange-50 text-orange-700"
                    }`}
                  >
                    {n.read ? "Lue" : "Non lue"}
                  </span>
                  {n.missionTitle && (
                    <span className="text-xs text-gray-500 truncate">
                      {n.missionTitle}
                    </span>
                  )}
                </div>

                <div className="mt-2 font-medium break-words">{n.message}</div>
                <div className="mt-1 text-sm text-gray-500">
                  {new Date(n.timestamp).toLocaleString()}
                </div>
              </div>

              {!n.read && (
                <button
                  onClick={() => markRead(n.id)}
                  className="px-3 py-2 rounded-lg bg-gray-900 text-white hover:bg-black text-sm"
                >
                  Marquer lue
                </button>
              )}
            </div>
          ))}
        </div>
      )}
    </div>
  );
}