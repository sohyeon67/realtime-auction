import { Chip, Stack } from '@mui/material'

function AuctionStatus({ status }) {

  const textMap = {
    READY: "시작전",
    ONGOING: "진행중",
    SOLD: "낙찰완료",
    CANCELLED: "취소",
    EXPIRED: "기간만료"
  }

  const colorMap = {
    READY: "orange",
    ONGOING: "green",
    SOLD: "blue",
    CANCELLED: "gray",
    ENDED: "red",
  }

  return (
    <Stack direction="row" spacing={1}>
      <Chip
        label={textMap[status]}
        sx={{
          backgroundColor: colorMap[status],
          color: 'white',
          fontWeight: 'bold'
        }} />
    </Stack>
  )
}

export default AuctionStatus