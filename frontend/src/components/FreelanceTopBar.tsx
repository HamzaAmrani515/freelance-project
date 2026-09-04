import React from "react";
import { Link, useParams } from "react-router-dom";
import { NotificationBadge } from "./NotificationBadge";

export default function FreelanceTopBar() {
  const { id } = useParams<{ id: string }>();
  const freelancerId = Number(id);

  return (
    <div className="sticky top-0 bg-white border-b px-4 py-3 flex items-center justify-between">
      <div className="font-semibold">Freelance Dashboard</div>
      <div className="flex gap-3">
        <Link className="px-3 py-2 rounded bg-gray-100" to={`/freelances/${freelancerId}/missions`}>
          Missions
        </Link>
        <NotificationBadge to={`/freelances/${freelancerId}/notifications`} />
      </div>
    </div>
  );
}
