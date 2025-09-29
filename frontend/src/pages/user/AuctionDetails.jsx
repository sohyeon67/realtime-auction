import { Box, Container, Grid, Typography, Skeleton, Button } from '@mui/material';
import React, { useEffect, useState } from 'react';
import { Navigate, useNavigate, useParams } from 'react-router-dom';
import api from '../../api/api';
import AuctionTimer from '../../components/user/auction/AuctionTimer';
import AuctionStatus from '../../components/user/auction/AuctionStatus';

function AuctionDetails() {
  const { auctionId } = useParams();
  const navigate = useNavigate();

  const [auction, setAuction] = useState(null);
  const [selectedImage, setSelectedImage] = useState(null);

  useEffect(() => {
    api.get(`/api/auctions/${auctionId}`)
      .then(res => {
        setAuction(res.data);

        // 대표 이미지가 기본적으로 선택됨
        const mainImg = res.data.images?.find(img => img.isMain) || res.data.images?.[0];
        setSelectedImage(mainImg);
      })
      .catch(err => console.error(err));
  }, [auctionId]);

  if (!auction) return <Skeleton variant="rectangular" width="100%" height={400} />

  return (
    <Container sx={{ pt: { xs: 14, sm: 20 }, pb: { xs: 8, sm: 12 } }}>
      <Typography variant="h4" sx={{mb: {xs: 1, sm:2}}}>{auction.title}</Typography>

      <Grid container spacing={4}>
        {/* 왼쪽: 이미지 */}
        <Grid size={{ xs: 12, md: 6 }}>
          {/* 선택 이미지 */}
          {selectedImage && (
            <Box
              component="img"
              src={selectedImage.url}
              alt={selectedImage.originalName}
              sx={{ width: '100%', height: 400, objectFit: 'contain', mb: 2 }}
            />
          )}

          {/* 사진 목록 */}
          <Grid container spacing={1}>
            {auction.images?.sort((a, b) => a.sortOrder - b.sortOrder).map((img, index) => (
              <Grid key={index}>
                <Box
                  component="img"
                  src={img.url}
                  alt={img.originalName}
                  sx={{
                    width: 80,
                    height: 80,
                    objectFit: 'cover',
                    border: img === selectedImage ? '2px solid #007FFF' : '1px solid #ccc',
                    cursor: 'pointer',
                  }}
                  onClick={() => setSelectedImage(img)}
                />
              </Grid>
            ))}
          </Grid>
        </Grid>

        {/* 오른쪽: 정보 */}
        <Grid size={{ xs: 12, md: 6 }} sx={{ display: 'flex', flexDirection: 'column', gap: 2 }}>
          <Typography variant="h6">시작가: {auction.startPrice}원</Typography>
          <Typography variant="h6">현재가: {auction.currentPrice}원</Typography>

          <AuctionTimer endTime={auction.endTime} />

          <Typography variant="h6">시작시간: {new Date(auction.startTime).toLocaleString()}</Typography>
          <Typography variant="h6">종료시간: {new Date(auction.endTime).toLocaleString()}</Typography>

          <Typography variant="body1">카테고리: {auction.categoryName}</Typography>
          <Typography variant="body1">판매자: {auction.sellerId} {auction.sellerNickname}</Typography>

          <AuctionStatus status={auction.status} />

          {auction.canEdit && (
            <Button
              variant="contained"
              color="primary"
              onClick={() => navigate(`/auctions/${auction.auctionId}/edit`)}
            >
              수정
            </Button>
          )}
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
