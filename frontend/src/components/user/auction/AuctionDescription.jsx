import { Box, Divider, Typography } from "@mui/material";

export default function AuctionDescription({ description }) {
  return (
    <Box sx={{ mt: 4 }}>
      <Typography variant="h5" fontWeight="bold">상품 설명</Typography>
      <Divider sx={{ my: 2 }} />
      <Typography variant="body1" sx={{ whiteSpace: 'pre-line' }}>
        {description}
      </Typography>
    </Box>
  );
}