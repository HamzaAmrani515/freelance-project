import React from "react";
import {
  BrowserRouter as Router,
  Route,
  Routes,
  Navigate,
} from "react-router-dom";
import { ToastContainer } from "react-toastify";
import "react-toastify/dist/ReactToastify.css";

import LoginPage from "./pages/LoginPage";
import AdminDashboardPage from "./pages/admin/AdminDashboardPage";
import ClientDashboardPage from "./pages/client/ClientDashboardPage";
import ClientFreelancerSearchPage from "./pages/client/ClientFreelancerSearchPage";
import FreelancerDashboardPage from "./pages/freelancer/FreelancerDashboardPage";
import FreelanceDetailsPage from "./pages/freelancer/FreelanceDetailsPage";
import CreateEvaluationPage from "./pages/client/CreateEvaluationPage";

import { getCurrentUser } from "./auth/session";
import type { UserRole } from "./auth/demoUsers";

function RequireRole({
  allowedRoles,
  children,
}: {
  allowedRoles: UserRole[];
  children: React.ReactNode;
}) {
  const user = getCurrentUser();

  if (!user) {
    return <Navigate to="/login" replace />;
  }

  if (!allowedRoles.includes(user.role)) {
    return <Navigate to="/login" replace />;
  }

  return <>{children}</>;
}

const App: React.FC = () => {
  return (
    <Router>
      <ToastContainer position="top-right" autoClose={3000} />

      <Routes>
        <Route path="/" element={<Navigate to="/login" replace />} />

        <Route path="/login" element={<LoginPage />} />

        <Route
          path="/admin/dashboard"
          element={
            <RequireRole allowedRoles={["ADMIN"]}>
              <AdminDashboardPage />
            </RequireRole>
          }
        />

        <Route
          path="/ranking"
          element={
            <RequireRole allowedRoles={["ADMIN"]}>
              <AdminDashboardPage />
            </RequireRole>
          }
        />

        <Route
          path="/client/dashboard"
          element={
            <RequireRole allowedRoles={["CLIENT"]}>
              <ClientDashboardPage />
            </RequireRole>
          }
        />

        <Route
          path="/client/recommendations"
          element={
            <RequireRole allowedRoles={["CLIENT"]}>
              <ClientFreelancerSearchPage />
            </RequireRole>
          }
        />

        <Route
          path="/evaluations/create"
          element={
            <RequireRole allowedRoles={["CLIENT"]}>
              <CreateEvaluationPage />
            </RequireRole>
          }
        />

        <Route
          path="/freelancer/dashboard"
          element={
            <RequireRole allowedRoles={["FREELANCER"]}>
              <FreelancerDashboardPage />
            </RequireRole>
          }
        />

        <Route
          path="/freelancers/:id/details"
          element={
            <RequireRole allowedRoles={["ADMIN", "CLIENT", "FREELANCER"]}>
              <FreelanceDetailsPage />
            </RequireRole>
          }
        />

        <Route
          path="/freelances/:id/details"
          element={
            <RequireRole allowedRoles={["ADMIN", "CLIENT", "FREELANCER"]}>
              <FreelanceDetailsPage />
            </RequireRole>
          }
        />

        <Route
          path="*"
          element={
            <div className="min-h-screen bg-slate-950 text-white p-8">
              <h1 className="text-3xl font-bold">404</h1>
              <p className="text-slate-400 mt-2">Page introuvable.</p>
              <a href="/login" className="text-blue-400 mt-4 inline-block">
                Retour login
              </a>
            </div>
          }
        />
      </Routes>
    </Router>
  );
};

export default App;