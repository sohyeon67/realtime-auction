# 실시간 경매 플랫폼 (개인 프로젝트)

## 📝 설명
- 실시간 경매 플랫폼
- 기간 : 2025.09.09 ~ 진행중

## 📚 기술 스택
- **Backend:** Spring Boot, JPA, QueryDSL
- **Frontend:** React, MUI
- **Database:** MySQL
- **Real-time:** WebSocket/STOMP
- **인증/보안:** JWT, OAuth2 Client
- **Tools:** Docker, GitHub

## ⚙️ 구현 기능
- 실시간 입찰 기능(WebSocket/STOMP)
- OAuth2 소셜 로그인
- 경매 CRUD 및 카테고리 관리
- JWT Access Token 기반 인증

## 🛠 트러블슈팅
- 커서 기반 페이지네이션 도입
- 경매 조회 API 성능 최적화
- 비관적락을 이용한 입찰 동시성 문제 해결
- JWT Access Token 기반 인증 구현
- 추가 예정...

## 📺 화면 미리보기
### 로그인 / 회원가입
<img width="1920" height="944" alt="image" src="https://github.com/user-attachments/assets/034d7a09-d0d3-4b93-9128-fb3e16afe9b3" />
<img width="1920" height="944" alt="image" src="https://github.com/user-attachments/assets/218bf796-0ead-4797-8912-a2d360da68fb" />

### 경매 화면
<img width="1920" height="943" alt="image" src="https://github.com/user-attachments/assets/4ffe5397-b570-4ff5-b405-c2b5bf9b9c98" />
<img width="1920" height="943" alt="image" src="https://github.com/user-attachments/assets/acb654c3-01b7-4480-80f9-757e541c6b7e" />
<img width="1920" height="943" alt="image" src="https://github.com/user-attachments/assets/8c6e7532-2058-4fa8-8257-0f7bfd005195" />
<img width="1920" height="943" alt="image" src="https://github.com/user-attachments/assets/58965b10-c041-43e8-894e-ba20cbb1c2ee" />

### 관리자 카테고리 관리
<img width="1920" height="944" alt="image" src="https://github.com/user-attachments/assets/ebd83078-12fc-4efc-a52a-3142532a63ed" />

## 🌟 향후 개선 예정
- AWS 배포
- Redis 캐싱 이용해보기
- 마이페이지, 낙찰 이후 로직, 관리자 통계 등 세부 기능 추가 구현
