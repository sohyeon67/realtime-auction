import React, { useState } from 'react';
import {
  TextField,
  Button,
  Typography,
  Stack,
  Link,
  Box
} from '@mui/material';
import { Link as RouterLink } from 'react-router-dom';
import api from '../../api/api';

export default function Signup() {

  const [formData, setFormData] = useState({
    email: '',
    username: '',
    password: '',
    confirmPassword: '',
    nickname: '',
    phone: '',
  });

  const [isEmailChecked, setIsEmailChecked] = useState(false); // 중복 확인용
  const emailRegex = /^[^\s@]+@[^\s@]+\.[^\s@]+$/;

  const handleInputChange = (e) => {
    const { name, value } = e.target; // e.target.name과 e.target.value 구조분해 할당
    setFormData(prev => ({ ...prev, [name]: value })); // 현재 입력한 필드만 업데이트
    if (name === 'email') {
      setIsEmailChecked(false); // 입력 바뀌면 중복 확인 초기화
    }
  };

  const checkEmail = () => {
    if (!formData.email || !emailRegex.test(formData.email)) {
      setErrors(prev => ({ ...prev, email: '올바른 이메일 형식이 아닙니다' }));
      return;
    }

    api.post('/api/members/exist', { username: formData.email })
      .then(res => {
        if (res.data) { // 존재
          setIsEmailChecked(false);
          setErrors(prev => ({ ...prev, email: '다른 이메일을 사용해주세요' }));
        } else {
          setIsEmailChecked(true);
          setErrors(prev => ({ ...prev, email: '' }));
        }
      });
  }

  const [errors, setErrors] = useState({});

  const validate = () => {
    const newErrors = {};

    if (!formData.email || formData.email.trim() === "") {
      newErrors.email = '이메일을 입력하세요';
    } else if (!emailRegex.test(formData.email)) {
      newErrors.email = '올바른 이메일 형식이 아닙니다';
    }

    if (!formData.username || formData.username.trim() === "") {
      newErrors.username = '이름을 입력하세요';
    }

    if (!formData.password) {
      newErrors.password = '비밀번호를 입력하세요';
    } else if (formData.password.length < 8) {
      newErrors.password = '비밀번호는 최소 8자리 이상이어야 합니다';
    }

    if (formData.password !== formData.confirmPassword)
      newErrors.confirmPassword = '비밀번호가 일치하지 않습니다';

    if (!formData.nickname || formData.nickname.trim() === "") {
      newErrors.nickname = '닉네임을 입력하세요';
    }

    if (!formData.phone || formData.phone.trim() === "") {
      newErrors.phone = '연락처를 입력하세요';
    } else if (!/^010\d{8}$/.test(formData.phone)) {
      newErrors.phone = '유효한 연락처 형식이 아닙니다 (예: 01012345678)';
    }

    setErrors(newErrors);
    return Object.keys(newErrors).length === 0;
  };

  const handleSubmit = async (e) => {
    e.preventDefault();
    if (!validate()) return;

    if (!isEmailChecked) return;

    // 서버에 맞게 키 변환
    const payload = {
      username: formData.email,
      name: formData.username,
      password: formData.password,
      nickname: formData.nickname,
      phone: formData.phone
    }

    // API 요청
    try {
      const res = await api.post('/api/members', payload);
      window.location.href = "/auth/login";
    } catch (err) {
      const newErrors = {};

      // exception 및 bean validation 이용
      if (err.response.data) {
        if (err.response.data.error) {
          newErrors.email = err.response.data.error;
        } else if (err.response.data.errors) {
          const serverErrors = err.response.data.errors;
          newErrors.email = serverErrors.username;
          newErrors.username = serverErrors.name;
          newErrors.password = serverErrors.password;
          newErrors.nickname = serverErrors.nickname;
          newErrors.phone = serverErrors.phone;

        }

        setErrors(newErrors);
      }

    }
  };

  return (
    <form onSubmit={handleSubmit}>
      <Stack spacing={3}>
        <Typography variant="h4" textAlign="center">회원가입</Typography>
        <Box sx={{ display: 'flex', gap: 1 }}>
          <TextField
            fullWidth
            label="이메일"
            name="email"
            value={formData.email}
            onChange={handleInputChange}
            error={!!errors.email}
            helperText={errors.email}
            autoFocus
          />
          <Button
            variant="outlined"
            onClick={checkEmail}
            color={isEmailChecked ? "success" : "primary"}
            sx={{
              whiteSpace: 'nowrap', // 줄바꿈 방지
            }}>
            {isEmailChecked ? "확인완료" : "중복확인"}
          </Button>
        </Box>

        <TextField
          fullWidth
          type="password"
          label="비밀번호"
          name="password"
          value={formData.password}
          onChange={handleInputChange}
          error={!!errors.password}
          helperText={errors.password}
        />
        <TextField
          fullWidth
          type="password"
          label="비밀번호 확인"
          name="confirmPassword"
          value={formData.confirmPassword}
          onChange={handleInputChange}
          error={!!errors.confirmPassword}
          helperText={errors.confirmPassword}
        />
        <TextField
          fullWidth
          label="사용자 이름"
          name="username"
          value={formData.username}
          onChange={handleInputChange}
          error={!!errors.username}
          helperText={errors.username}
        />
        <TextField
          fullWidth
          label="닉네임"
          name="nickname"
          value={formData.nickname}
          onChange={handleInputChange}
          error={!!errors.nickname}
          helperText={errors.nickname}
        />
        <TextField
          fullWidth
          label="연락처"
          name="phone"
          value={formData.phone}
          onChange={handleInputChange}
          error={!!errors.phone}
          helperText={errors.phone}
        />
        <Button fullWidth variant="contained" color="primary" type="submit">
          가입하기
        </Button>
        <Typography variant="body2" textAlign="center">
          이미 계정이 있으신가요?{' '}
          <Link
            to="/auth/login"
            component={RouterLink}
          >
            로그인
          </Link>
        </Typography>
      </Stack>
    </form>
  );
};