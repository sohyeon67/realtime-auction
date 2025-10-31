import { createContext, useContext, useState } from "react";

export const AuthContext = createContext();

export function AuthProvider({ children }) {
  // 새로고침시 로그인 유지할 수 있도록 초기값 넣어주기
  const [isLoggedIn, setIsLoggedIn] = useState(() => !!localStorage.getItem("accessToken"));

  const login = (token) => {
    localStorage.setItem("accessToken", token);
    setIsLoggedIn(true);
  };

  const logout = () => {
    localStorage.removeItem("accessToken");
    setIsLoggedIn(false);
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