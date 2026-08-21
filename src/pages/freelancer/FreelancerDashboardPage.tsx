import React, { useEffect } from "react";
import { useNavigate } from "react-router-dom";
import { getCurrentUser } from "../../auth/session";

export default function FreelancerDashboardPage() {
  const navigate = useNavigate();

  useEffect(() => {
    const user = getCurrentUser();

    if (!user || user.role !== "FREELANCER") {
      navigate("/login", { replace: true });
      return;
    }

    const freelancerId = getDemoFreelancerId(user.email || "");

    navigate(`/freelancers/${freelancerId}/details`, { replace: true });
  }, [navigate]);

  return (
    <div className="min-h-screen bg-slate-950 text-white p-8">
      Chargement du dashboard freelancer...
    </div>
  );
}

function getDemoFreelancerId(email: string) {
  const normalizedEmail = email.toLowerCase();

  if (normalizedEmail.includes("hamza")) {
    return 1;
  }

  if (normalizedEmail.includes("nadia")) {
    return 2;
  }

  if (normalizedEmail.includes("yassine")) {
    return 3;
  }

  if (normalizedEmail.includes("salma")) {
    return 4;
  }

  return 1;
}