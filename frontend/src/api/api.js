import axios from "axios";

const api = axios.create({
  baseURL: `${import.meta.env.VITE_BACKEND_API_BASE_URL}`,
});

api.interceptors.request.use(config => {

  // 로그인 요청이면 토큰을 보내지 않음
  if (!config.url.endsWith("/login")) {
    const token = localStorage.getItem("accessToken");
    if (token) {
      config.headers.Authorization = `Bearer ${token}`;
    }
  }

  return config;
});

api.interceptors.response.use(
  response => response,
  error => {
    if (error.response && error.response.status == 401) {
      window.location.href = "/auth/login"; // 토큰 만료시 로그인 페이지 이동
    }
    return Promise.reject(error);
  }
)

export default api;