import { Box, Container, Grid, Typography, Skeleton, Button, Stack, Divider } from '@mui/material';
import React, { useEffect, useState } from 'react';
import { Navigate, useLocation, useNavigate, useParams } from 'react-router-dom';
import api from '../../api/api';
import AuctionTimer from '../../components/user/auction/AuctionTimer';
import AuctionStatus from '../../components/user/auction/AuctionStatus';
import ImageGallery from '../../components/common/ImageGallery';

function AuctionDetails() {
  const { auctionId } = useParams();
  const navigate = useNavigate();
  const location = useLocation();

  const [auction, setAuction] = useState(null);

  useEffect(() => {
    api.get(`/api/auctions/${auctionId}`)
      .then(res => {
        setAuction(res.data);
      })
      .catch(err => console.error(err));
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

          <Button variant="contained" color="black">
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
