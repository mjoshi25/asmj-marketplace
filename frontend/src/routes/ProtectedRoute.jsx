import { Navigate, Outlet, useLocation } from "react-router-dom";
import { useAuth } from "../context/AuthContext";

export default function ProtectedRoute({ roles = [] }) {
  const { user } = useAuth();
  const location = useLocation();

  if (!user) return <Navigate to="/" replace state={{ from: location.pathname }} />;

  const userRoles = Array.isArray(user.roles)
    ? user.roles
    : user.role ? [user.role] : [];

  if (roles.length && !roles.some(r => userRoles.includes(r))) {
    if (userRoles.includes("ADMIN")) return <Navigate to="/admin" replace />;
    if (userRoles.includes("VENDOR")) return <Navigate to="/vendor" replace />;
    if (userRoles.includes("DRIVER")) return <Navigate to="/driver" replace />;
    return <Navigate to="/dashboard" replace />;
  }

  return <Outlet />;
}