import { createContext, useContext, useState } from "react";
import api from "../api/api";

export const AuthContext = createContext();

export function AuthProvider({ children }) {

  // 새로고침시 로그인 유지할 수 있도록 초기값 넣어주기
  const [isLoggedIn, setIsLoggedIn] = useState(() => !!localStorage.getItem("accessToken"));

  const login = (token) => {
    localStorage.setItem("accessToken", token);
    setIsLoggedIn(true);
  };

  const logout = async () => {
    try {
      // 서버가 쿠키에서 refresh token을 읽어 삭제하고, 쿠키를 제거하도록
      await api.post("/logout", {}, { withCredentials: true });
    } finally {
      localStorage.removeItem("accessToken");
      setIsLoggedIn(false);
    }
  };

  return (
    <AuthContext.Provider value={{ isLoggedIn, login, logout }}>
      {children}
    </AuthContext.Provider>
  );
}

// 커스텀 훅으로 만들기
export function useAuth() {
  return useContext(AuthContext);
}