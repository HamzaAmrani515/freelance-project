import React from "react";
import { NavLink, Outlet, useParams } from "react-router-dom";
import { NotificationProvider } from "../context/NotificationContext";
import { NotificationBadge } from "../components/NotificationBadge";

function TopBar({ freelancerId }: { freelancerId: number }) {
  const linkClass = ({ isActive }: { isActive: boolean }) =>
    `px-3 py-2 rounded-lg text-sm font-medium ${
      isActive ? "bg-gray-900 text-white" : "text-gray-700 hover:bg-gray-100"
    }`;

  return (
    <div className="sticky top-0 z-10 bg-white border-b">
      <div className="max-w-6xl mx-auto px-4 py-3 flex items-center justify-between">
        <div className="font-semibold text-lg">Freelance Dashboard</div>
        <div className="flex items-center gap-2">
          <NavLink className={linkClass} to={`/freelances/${freelancerId}/details`}>Profil</NavLink>
          <NavLink className={linkClass} to={`/freelances/${freelancerId}/missions`}>Missions</NavLink>
          <NotificationBadge to={`/freelances/${freelancerId}/notifications`} />
        </div>
      </div>
    </div>
  );
}

export default function FreelanceLayout() {
  const { id } = useParams();
  const freelancerId = Number(id);

  if (!id || Number.isNaN(freelancerId)) {
    return <div style={{ padding: 20 }}>FreelancerId invalide</div>;
  }

  return (
    <NotificationProvider freelancerId={freelancerId}>
      <TopBar freelancerId={freelancerId} />
      <main className="max-w-6xl mx-auto p-4">
        <Outlet />
      </main>
    </NotificationProvider>
  );
}
