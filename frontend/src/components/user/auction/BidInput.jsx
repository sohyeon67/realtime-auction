import { Box, Button, Fab, IconButton, Stack, TextField } from "@mui/material";
import { useEffect, useState } from "react";
import AddIcon from '@mui/icons-material/Add';
import RemoveIcon from '@mui/icons-material/Remove';
import { NumericFormat } from "react-number-format";

export default function BidInput({ currentPrice, onBidSubmit, step = 1000 }) {
  const [bidPrice, setBidPrice] = useState(currentPrice + step);
  const [errorText, setErrorText] = useState("");

  // currentPrice가 바뀔 때마다 bidPrice를 최신가 + step으로 갱신
  useEffect(() => {
    setErrorText("");
    setBidPrice(currentPrice + step);
  }, [currentPrice, step]);

  const handleIncrease = () => setBidPrice(prev => prev + step);
  const handleDecrease = () => setBidPrice(prev => Math.max(currentPrice + step, prev - step));

  const validateBid = (value) => {
    const minPrice = currentPrice + step;

    if (value < minPrice) {
      return `${minPrice.toLocaleString()}원 이상으로 입찰해야 합니다.`;
    } else if (value % step !== 0) {
      return `입찰 단위는 ${step.toLocaleString()}원 입니다.`;
    } else {
      return "";
    }
  };

  const handleSubmit = () => {
    const error = validateBid(bidPrice);
    if (error) {
      setErrorText(error);
      return;
    }
    onBidSubmit(bidPrice);
  };

  return (
    <Box display="flex" flexDirection="column" alignItems="center" gap={2}>
      <Stack direction="row" alignItems="center" gap={1}>
        <Fab onClick={handleDecrease} color="primary" size="small">
          <RemoveIcon />
        </Fab>

        <NumericFormat
          customInput={TextField}
          value={bidPrice}
          onChange={(e) => {
            const value = Number(e.target.value.replace(/,/g, '')) || 0;
            setBidPrice(value);
            setErrorText(validateBid(value));
          }}
          thousandSeparator
          size="small"
          error={!!errorText}
          helperText={errorText}
        />
        <Box sx={{ fontSize: 18, fontWeight: 500, ml: 1 }}>원</Box>

        <Fab onClick={handleIncrease} color="primary" size="small">
          <AddIcon />
        </Fab>
      </Stack>

      <Button variant="contained" color="black" onClick={handleSubmit} sx={{ width: "100%" }}>
        입찰
      </Button>
    </Box>
  );
}