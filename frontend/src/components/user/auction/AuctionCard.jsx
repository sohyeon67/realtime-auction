import { useNavigate } from "react-router-dom";
import { Box, Typography, Divider, Card, CardActionArea, CardMedia, CardContent } from '@mui/material';
import AccessAlarmIcon from '@mui/icons-material/AccessAlarm';
import { formatRemainingTime } from "../../../utils/time";
import AuctionStatus from "./AuctionStatus";

export default function AuctionCard({ auction, searchParams }) {
  const navigate = useNavigate();

  return (
    <Card>
      <CardActionArea
        onClick={() => navigate(`/user/auctions/${auction.auctionId}?${searchParams.toString()}`)} // 상세페이지에 검색 조건 전달
      >

        <CardMedia
          component="img"
          height="140"
          image={auction.mainImageUrl}
          alt={auction.title}
        />

        <CardContent>

          <Typography
            variant="h6"
            sx={{
              overflow: 'hidden',
              textOverflow: 'ellipsis',
              whiteSpace: 'nowrap'
            }}
          >
            {auction.title}
          </Typography>

          <Divider sx={{ my: 1 }} />

          <AuctionStatus status={auction.status} size="small" />

          <Typography
            variant="h6"
            sx={{ fontWeight: 'bold' }}
          >
            {auction.currentPrice?.toLocaleString()} 원
          </Typography>


          <Box display="flex" alignItems="center" gap={0.5}>
            <Typography variant="body2" color="text.secondary">
              입찰 {auction.bidCount} 회
            </Typography>

            <Divider orientation="vertical" flexItem sx={{ mx: 1 }} />

            <AccessAlarmIcon fontSize="small" />
            <Typography variant="body2" color="text.secondary">
              {formatRemainingTime(auction.endTime)}
            </Typography>
          </Box>

          <Typography variant="body2" color="text.secondary">
            판매자 : {auction.sellerNickname}
          </Typography>

        </CardContent>

      </CardActionArea>
    </Card>
  );
}