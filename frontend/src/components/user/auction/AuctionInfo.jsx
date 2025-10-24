import { Box, Stack, Typography } from "@mui/material";
import AuctionTimer from "./AuctionTimer";

export default function AuctionInfo({ auction, currentPrice }) {
  return (
    <Stack spacing={2}>
      <Box display="flex" gap={4}>
        <Typography variant="h5" sx={{ width: 100 }}>현재가</Typography>
        <Typography variant="h5" fontWeight="bold">{currentPrice.toLocaleString()}원</Typography>
      </Box>

      <Box display="flex" gap={4}>
        <Typography variant="h6" color="text.secondary" sx={{ width: 100 }}>시작가</Typography>
        <Typography variant="h6">{auction.startPrice.toLocaleString()}원</Typography>
      </Box>
      <Box display="flex" gap={4}>
        <Typography variant="h6" color="text.secondary" sx={{ width: 100 }}>남은시간</Typography>
        <AuctionTimer endTime={auction.endTime} />
      </Box>
      <Box display="flex" gap={4}>
        <Typography variant="h6" color="text.secondary" sx={{ width: 100 }}>시작시간</Typography>
        <Typography variant="h6">{new Date(auction.startTime).toLocaleString()}</Typography>
      </Box>
      <Box display="flex" gap={4}>
        <Typography variant="h6" color="text.secondary" sx={{ width: 100 }}>종료시간</Typography>
        <Typography variant="h6">{new Date(auction.endTime).toLocaleString()}</Typography>
      </Box>
      <Box display="flex" gap={4}>
        <Typography variant="h6" color="text.secondary" sx={{ width: 100 }}>카테고리</Typography>
        <Typography variant="h6">{auction.categoryName}</Typography>
      </Box>
    </Stack>

  );
}