import { Navigate, Outlet, useLocation } from "react-router-dom";
import { useAuth } from "../contexts/AuthContext";

export default function ProtectedRoute() {
  const { isLoggedIn } = useAuth();
  const location = useLocation();
  return isLoggedIn ? <Outlet /> : <Navigate to="/auth/login" replace state={{ from: location }} />
}