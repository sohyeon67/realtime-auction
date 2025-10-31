import { Navigate, Outlet, useLocation } from "react-router-dom";
import { useAuth } from "../contexts/AuthContext";

export default function ProtectedRoute() {
  const { isLoggedIn } = useAuth();
  const location = useLocation();
  // replace : 히스토리에서 보호된 페이지가 로그인 페이지로 대체됨
  return isLoggedIn ? <Outlet /> : <Navigate to="/auth/login" replace state={{ from: location }} />
}