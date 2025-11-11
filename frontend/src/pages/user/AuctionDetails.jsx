import { Box, Container, Grid, Typography, Skeleton, Button, Stack, Divider } from '@mui/material';
import { useEffect, useRef, useState } from 'react';
import { useLocation, useNavigate, useParams } from 'react-router-dom';
import api from '../../api/api';
import ImageGallery from '../../components/common/ImageGallery';
import { Client } from '@stomp/stompjs';
import SockJS from 'sockjs-client';
import BidInput from '../../components/user/auction/BidInput';
import BidListModal from '../../components/user/auction/BidListModal';
import AuctionInfo from '../../components/user/auction/AuctionInfo';
import AuctionDescription from '../../components/user/auction/AuctionDescription';

function AuctionDetails() {
  const { auctionId } = useParams();
  const navigate = useNavigate();
  const location = useLocation();

  const [auction, setAuction] = useState(null);
  const [currentPrice, setCurrentPrice] = useState(auction?.currentPrice || 0);
  const [open, setOpen] = useState(false);  // 모달

  const [latestBid, setLatestBid] = useState(null);

  const stompClient = useRef(null); // 리렌더링되도 값이 유지됨

  const accessToken = localStorage.getItem("accessToken");

  useEffect(() => {
    // 초기 데이터 조회
    api.get(`/api/auctions/${auctionId}`)
      .then(res => {
        setAuction(res.data);
        setCurrentPrice(res.data.currentPrice);
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
        const newBid = JSON.parse(message.body);
        setLatestBid(newBid);
        setCurrentPrice(newBid.bidPrice);
      });

      // 개인 오류 메시지 구독
      client.subscribe('/user/queue/errors', (error) => {
        try {
          const errorData = JSON.parse(error.body);
          alert(errorData.error || "알 수 없는 오류가 발생했습니다.");
        } catch (e) {
          console.error("에러 파싱 실패:", e);
          alert("오류가 발생했습니다.");
        }
      })
    };

    // 서버에서 예외를 던져 연결 요청을 거부했을 때, 클라이언트 에러 콜백
    client.onStompError = (frame) => {
      client.deactivate();  // 재연결 중단
      alert("서버에서 연결 거부");
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

  // 실시간 입찰
  const handleBidSubmit = (price) => {
    if (!stompClient.current || !stompClient.current.connected) return;

    stompClient.current.publish({
      destination: `/app/auctions/${auctionId}/bids`,
      body: JSON.stringify(
        {
          bidPrice: price,
        }
      ),
    });
  };


  if (!auction) return <Skeleton variant="rectangular" width="100%" height={400} />

  return (
    <Container>
      <Typography variant="h4" fontWeight="bold" sx={{ mb: { xs: 1, sm: 2 } }}>{auction.title}</Typography>
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

          <AuctionInfo auction={auction} currentPrice={currentPrice} />

          <BidInput
            currentPrice={currentPrice}
            onBidSubmit={handleBidSubmit}
          />

          <Button
            variant="contained"
            color="lightGray"
            onClick={() => setOpen(true)}
          >
            입찰 내역
          </Button>

          <BidListModal open={open} onClose={() => setOpen(false)} auctionId={auctionId} newBid={latestBid} />

        </Grid>
      </Grid>

      {/* 아래: 설명 */}
      <AuctionDescription description={auction.description} />

    </Container>
  );
}

export default AuctionDetails;
