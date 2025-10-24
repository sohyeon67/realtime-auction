import { Box, Button, Divider, Stack, TextField, Typography, Link } from "@mui/material";
import { useState } from "react";
import api from "../../api/api";
import { Link as RouterLink, useLocation, useNavigate } from 'react-router-dom';
import { useAuth } from "../../contexts/AuthContext";


const BACKEND_API_BASE_URL = import.meta.env.VITE_BACKEND_API_BASE_URL;

export default function Login() {

  const navigate = useNavigate();
  const location = useLocation();
  const { login } = useAuth();

  // 로그인 직전 경로
  const from = location.state?.from?.pathname || '/';

  // 자체 로그인
  const [email, setEmail] = useState("");
  const [password, setPassword] = useState("");
  const [error, setError] = useState("");

  const handleLogin = async (e) => {
    e.preventDefault();
    setError("");

    if (!email || !password) {
      setError("이메일과 비밀번호를 입력하세요.");
      return;
    }

    try {
      const res = await api.post("/login", {
        username: email,
        password,
      });

      login(res.data.accessToken);
      // localStorage.setItem("accessToken", res.data.accessToken);
      localStorage.setItem("refreshToken", res.data.refreshToken);

      // 로그인 성공 후 페이지 이동
      // window.location.href = "/";
      navigate(from, { replace: true });

    } catch (err) {
      setError("이메일 또는 비밀번호가 잘못되었습니다.");
    }
  }


  // 소셜 로그인
  const handleSocialLogin = (provider) => {
    // 하이퍼링크 형태로 요청을 보냄
    window.location.href = `${BACKEND_API_BASE_URL}/oauth2/authorization/${provider}`
  };

  return (
    <form onSubmit={handleLogin}>
      <Stack spacing={2}>

        <Typography variant="h4" textAlign="center">로그인</Typography>

        <TextField
          label="Email Address"
          fullWidth name="email"
          autoFocus value={email}
          onChange={(e) => setEmail(e.target.value)}
        />
        <TextField
          label="Password"
          type="password"
          fullWidth
          name="password"
          value={password}
          onChange={(e) => setPassword(e.target.value)} />

        {error && (
          <Typography variant="body2" color="error" textAlign="center">
            {error}
          </Typography>
        )}

        <Button type="submit" variant="contained" color="primary">로그인</Button>

        <Divider>or</Divider>

        <Box sx={{ display: 'flex', flexDirection: 'column', gap: 2 }}>
          <Button
            fullWidth
            variant="outlined"
            onClick={() => handleSocialLogin("google")}
          >
            Sign in with Google
          </Button>
          <Button
            fullWidth
            variant="outlined"
            onClick={() => handleSocialLogin("naver")}
          >
            Sign in with Naver
          </Button>
        </Box>
        <Typography variant="body2" textAlign="center">
          계정이 없으신가요?{' '}
          <Link to="/auth/signup" component={RouterLink}>
            회원가입
          </Link>
        </Typography>

      </Stack>
    </form >
  );
}