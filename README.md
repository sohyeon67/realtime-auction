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
- 실시간 입찰 기능: WebSocket/STOMP 기반으로 여러 사용자가 동시에 입찰 가능
- 커서 기반 페이지네이션: 실시간 입찰 화면에서 다수 사용자의 입찰로 인해 기존 페이지네이션이 꼬이는 문제를 해결
- 경매 조회 최적화 : 
- 비관적 락(Pessimistic Lock): 다중 동시 입찰 시 입찰수 및 최고가 정확하게 반영
- JWT + OAuth2 Client 인증: 소셜로그인 및 토큰 기반 인증 구현
- 카테고리 관리 기능: 경매 카테고리 CRUD 및 연관관계 관리

## 📺 화면 미리보기


## 🛠 향후 개선 예정
- AWS 배포
- Redis 캐싱 이용해보기
- 마이페이지, 낙찰 이후 로직, 관리자 통계 등 세부 기능 추가 구현
