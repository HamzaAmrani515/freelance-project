import React, { useEffect, useMemo, useState } from "react";
import { useNavigate } from "react-router-dom";
import {
  DemoUser,
  UserRole,
  demoAdmin,
  demoClients,
  demoFreelancers,
} from "../auth/demoUsers";
import { setCurrentUser } from "../auth/session";

export default function LoginPage() {
  const navigate = useNavigate();

  const [activeRole, setActiveRole] = useState<UserRole>("FREELANCER");
  const [selectedUser, setSelectedUser] = useState<DemoUser | null>(null);

  const users = useMemo(() => {
    if (activeRole === "ADMIN") return [demoAdmin];
    if (activeRole === "CLIENT") return demoClients;
    return demoFreelancers;
  }, [activeRole]);

  useEffect(() => {
    setSelectedUser(users[0]);
  }, [users]);

  const login = () => {
    if (!selectedUser) return;

    setCurrentUser(selectedUser);

    if (selectedUser.role === "ADMIN") {
      navigate("/admin/dashboard");
      return;
    }

    if (selectedUser.role === "CLIENT") {
      navigate("/client/dashboard");
      return;
    }

    if (selectedUser.role === "FREELANCER" && selectedUser.freelancerId) {
      navigate(`/freelancers/${selectedUser.freelancerId}/details`);
      return;
    }

    navigate("/login");
  };

  return (
    <div className="min-h-screen bg-slate-950 text-white px-8 py-10">
      <div className="max-w-6xl mx-auto">
        <div className="text-center mb-10">
          <p className="text-blue-400 font-semibold">Freelance Platform</p>

          <h1 className="text-5xl font-bold mt-3">
            Se connecter à l’espace métier
          </h1>

          <p className="text-slate-400 mt-4 max-w-3xl mx-auto leading-7">
            Choisissez un rôle, sélectionnez un compte de démonstration, puis
            accédez uniquement aux fonctionnalités correspondant à ce périmètre
            métier.
          </p>
        </div>

        <div className="flex justify-center mb-8">
          <div className="bg-white/5 border border-white/10 rounded-2xl p-2 flex gap-2">
            <RoleButton
              label="Freelancer"
              active={activeRole === "FREELANCER"}
              onClick={() => setActiveRole("FREELANCER")}
            />

            <RoleButton
              label="Client"
              active={activeRole === "CLIENT"}
              onClick={() => setActiveRole("CLIENT")}
            />

            <RoleButton
              label="Administrateur"
              active={activeRole === "ADMIN"}
              onClick={() => setActiveRole("ADMIN")}
            />
          </div>
        </div>

        <div className="grid grid-cols-[1fr_0.85fr] gap-8 items-start">
          <div className="bg-white/5 border border-white/10 rounded-3xl p-7">
            <div className="flex justify-between items-start mb-6">
              <div>
                <h2 className="text-2xl font-bold">
                  {activeRole === "ADMIN" && "Compte administrateur"}
                  {activeRole === "CLIENT" && "Choisir un client"}
                  {activeRole === "FREELANCER" && "Choisir un freelancer"}
                </h2>

                <p className="text-slate-400 mt-2">
                  Un seul rôle est actif à la fois.
                </p>
              </div>

              <span className="px-3 py-1 rounded-full bg-blue-500/20 text-blue-300 text-xs font-semibold">
                {formatLabel(activeRole)}
              </span>
            </div>

            <div className="grid gap-4">
              {users.map((user) => {
                const selected = selectedUser?.id === user.id;

                return (
                  <button
                    key={user.id}
                    onClick={() => setSelectedUser(user)}
                    className={
                      selected
                        ? "text-left rounded-2xl p-5 border border-blue-500 bg-blue-500/10 transition"
                        : "text-left rounded-2xl p-5 border border-white/10 bg-slate-900/70 hover:bg-white/10 transition"
                    }
                  >
                    <div className="flex justify-between gap-4">
                      <div>
                        <h3 className="text-xl font-bold">{user.label}</h3>

                        <p className="text-blue-300 text-sm mt-2">
                          {user.email}
                        </p>

                        <p className="text-slate-400 mt-4 leading-7">
                          {user.subtitle}
                        </p>
                      </div>

                      <span className="text-slate-400">→</span>
                    </div>
                  </button>
                );
              })}
            </div>
          </div>

          <div className="bg-white/5 border border-white/10 rounded-3xl p-7 sticky top-8">
            <p className="text-blue-400 font-semibold">
              Connexion de démonstration
            </p>

            <h2 className="text-3xl font-bold mt-2">
              Accès sécurisé par rôle
            </h2>

            <div className="mt-7 grid gap-5">
              <Field label="Rôle sélectionné">
                <input
                  value={formatLabel(activeRole)}
                  readOnly
                  className="input"
                />
              </Field>

              <Field label="Email professionnel">
                <input
                  value={selectedUser?.email || ""}
                  readOnly
                  className="input"
                />
              </Field>

              <Field label="Mot de passe">
                <input
                  value="••••••••••"
                  readOnly
                  type="password"
                  className="input"
                />
              </Field>

              <button
                onClick={login}
                className="bg-blue-600 hover:bg-blue-700 px-6 py-4 rounded-xl font-bold mt-2"
              >
                Se connecter
              </button>
            </div>

            <div className="mt-7 bg-slate-900 border border-white/10 rounded-2xl p-5">
              <h3 className="font-bold">Périmètre après connexion</h3>

              <p className="text-slate-400 mt-3 leading-7 text-sm">
                {activeRole === "ADMIN" &&
                  "L’administrateur visualise le ranking global, les profils, les scores et les évaluations."}

                {activeRole === "CLIENT" &&
                  "Le client visualise ses missions, consulte les profils et peut évaluer les freelancers affectés."}

                {activeRole === "FREELANCER" &&
                  "Le freelancer visualise son profil, son score actuel et l’historique des évaluations reçues."}
              </p>
            </div>
          </div>
        </div>
      </div>
    </div>
  );
}

function RoleButton({
  label,
  active,
  onClick,
}: {
  label: string;
  active: boolean;
  onClick: () => void;
}) {
  return (
    <button
      onClick={onClick}
      className={
        active
          ? "px-6 py-3 rounded-xl bg-blue-600 text-white font-bold"
          : "px-6 py-3 rounded-xl text-slate-300 hover:bg-white/10 font-semibold"
      }
    >
      {label}
    </button>
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
      <p className="text-sm text-slate-400 mb-2">{label}</p>
      {children}
    </label>
  );
}

function formatLabel(value: string) {
  return value
    .replaceAll("_", " ")
    .toLowerCase()
    .replace(/\b\w/g, (letter) => letter.toUpperCase());
}