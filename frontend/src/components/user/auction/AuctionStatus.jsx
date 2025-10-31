import { Chip, Stack } from '@mui/material'
import { AUCTION_STATUS_META } from '../../../constants/auctionStatus'

function AuctionStatus({ status, size = "medium" }) {

  const statusInfo = AUCTION_STATUS_META[status];

  return (
    <Stack direction="row" spacing={1}>
      <Chip
        label={statusInfo.text}
        size={size}
        sx={{
          backgroundColor: statusInfo.color,
          color: 'white',
          fontWeight: 'bold'
        }} />
    </Stack>
  )
}

export default AuctionStatus