import { Box, Container, Grid, Typography, Skeleton, Button, Stack, Divider } from '@mui/material';
import React, { useEffect, useRef, useState } from 'react';
import { useLocation, useNavigate, useParams } from 'react-router-dom';
import api from '../../api/api';
import AuctionTimer from '../../components/user/auction/AuctionTimer';
import ImageGallery from '../../components/common/ImageGallery';
import { Client } from '@stomp/stompjs';
import SockJS from 'sockjs-client';

function AuctionDetails() {
  const { auctionId } = useParams();
  const navigate = useNavigate();
  const location = useLocation();

  const [auction, setAuction] = useState(null);
  const [currentPrice, setCurrentPrice] = useState(auction?.currentPrice || 0);
  const [bids, setBids] = useState([]);

  const stompClient = useRef(null); // 리렌더링되도 값이 유지됨

  const accessToken = localStorage.getItem("accessToken");

  useEffect(() => {
    // 초기 데이터 조회
    api.get(`/api/auctions/${auctionId}`)
      .then(res => {
        setAuction(res.data);
      })
      .catch(err => console.error(err));

    // 웹소켓 연결
    const client = new Client({
      webSocketFactory: () => new SockJS(`${import.meta.env.VITE_BACKEND_API_BASE_URL}/ws`),
      connectHeaders: {
        // 로그인 상태일 때 토큰 전달
        'Authorization': accessToken ? `Bearer ${accessToken}` : '',
      },
      reconnectDelay: 5000,
      heartbeatIncoming: 4000,
      heartbeatOutgoing: 4000,
    });

    // 연결 성공 로직
    client.onConnect = () => {
      // 실시간 가격 업데이트 구독
      client.subscribe(`/topic/auctions/${auctionId}`, (message) => {
        console.log(JSON.parse(message.body));
      });

      // 개인 오류 메시지 구독
      client.subscribe('/user/queue/errors', (error) => {
          console.error(JSON.parse(error.body));
      })
    };

    client.activate();
    stompClient.current = client;

    // 언마운트 시 연결 종료(cleanup)
    return () => stompClient.current.deactivate(); // 연결 해제

  }, [auctionId]);

  // 경매 삭제
  const handleDelete = (auctionId) => {
    const ok = window.confirm("경매를 삭제하시겠습니까?");
    if (!ok) return;

    api.delete(`/api/auctions/${auctionId}`)
      .then(res => navigate('/user/auctions'))
      .catch(err => console.error(err));
  };

  // 경매 취소
  const handleCancel = (auctionId) => {
    const ok = window.confirm("경매를 취소하시겠습니까?");
    if (!ok) return;

    api.patch(`/api/auctions/${auctionId}/cancel`)
      .then(res => {
        setAuction(prev => ({ ...prev, status: 'CANCELLED' }));
      })
      .catch(err => console.error(err));
  };

  // 입찰 테스트
  const handleBid = () => {
    if (!stompClient.current || !stompClient.current.connected) return;

    stompClient.current.publish({
      destination: `/app/auctions/${auctionId}/bids`,
      body: JSON.stringify(
        {
          message: "hello"
        }
      ),
    });
  };


  if (!auction) return <Skeleton variant="rectangular" width="100%" height={400} />

  return (
    <Container sx={{ pt: { xs: 14, sm: 20 }, pb: { xs: 8, sm: 12 } }}>
      <Typography variant="h4" sx={{ mb: { xs: 1, sm: 2 } }}>{auction.title}</Typography>
      <Divider />

      <Stack direction="row" justifyContent="flex-end" spacing={1} sx={{ my: 2 }}>
        {auction.canEdit && (
          <Button
            variant="contained"
            color="success"
            onClick={() => navigate(`/user/auctions/${auction.auctionId}/edit`)}
          >
            수정
          </Button>
        )}
        {auction.canEdit && auction.status === 'READY' && (
          <Button
            variant="contained"
            color="error"
            onClick={() => handleDelete(auction.auctionId)}
          >
            삭제
          </Button>
        )}
        {auction.canEdit && auction.status === 'ONGOING' && (
          <Button
            variant="contained"
            color="primary"
            onClick={() => handleCancel(auction.auctionId)}
          >
            경매취소
          </Button>
        )}

        <Button
          variant="contained"
          color="primary"
          onClick={() => navigate(`/user/auctions${location.search}`)}
        >
          목록
        </Button>
      </Stack>

      <Grid container spacing={4}>
        {/* 왼쪽: 이미지 */}
        <Grid size={{ xs: 12, md: 6 }}>
          <ImageGallery images={auction.images} />
        </Grid>

        {/* 오른쪽: 정보 */}
        <Grid size={{ xs: 12, md: 6 }} sx={{ display: 'flex', flexDirection: 'column', gap: 2 }}>
          <Typography variant="h6">시작가: {auction.startPrice}원</Typography>
          <Typography variant="h6">현재가: {auction.currentPrice}원</Typography>

          <AuctionTimer endTime={auction.endTime} />

          <Typography variant="h6">시작시간: {new Date(auction.startTime).toLocaleString()}</Typography>
          <Typography variant="h6">종료시간: {new Date(auction.endTime).toLocaleString()}</Typography>

          <Typography variant="body1">카테고리: {auction.categoryName}</Typography>

          <Button variant="contained" color="black" onClick={() => handleBid()}>
            입찰하기
          </Button>

        </Grid>
      </Grid>

      {/* 아래: 설명 */}
      <Box sx={{ mt: 4 }}>
        <Typography variant="h6">상품 설명</Typography>
        <Typography variant="body1" sx={{ whiteSpace: 'pre-line' }}>
          {auction.description}
        </Typography>
      </Box>

    </Container>
  );
}

export default AuctionDetails;
