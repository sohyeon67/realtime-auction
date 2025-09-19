const BACKEND_API_BASE_URL = import.meta.env.VITE_BACKEND_API_BASE_URL;

import React, { useEffect } from 'react'
import { useNavigate } from 'react-router-dom';
import api from '../../api/api';

export default function SocialLoginSuccess() {

  const navigate = useNavigate();

  // 특정 조건이 만족했을 때 입력 함수를 수행하라라는 훅
  // 페이지에 접근시(백엔드에서 리다이렉션으로 여기로 보내면 실행)
  useEffect(() => {

    const cookieToBody = async () => {

      try {
        const res = await api.post(
          "/jwt/exchange",
          {}, // body가 필요 없으면 빈 객체
          { withCredentials: true } // 쿠키 포함
        );

        const data = res.data;

        localStorage.setItem("accessToken", data.accessToken);
        localStorage.setItem("refreshToken", data.refreshToken);

        navigate("/");
      } catch (err) {
        navigate("/auth/login");
      }
    };

    cookieToBody();

  }, [navigate]);

  return (
    <>로그인 처리중...</>
  );
}