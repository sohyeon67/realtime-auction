import { Button, Dialog, DialogContent, DialogTitle, IconButton, Paper, styled, Table, TableBody, TableCell, tableCellClasses, TableContainer, TableHead, TableRow } from "@mui/material";
import CloseIcon from "@mui/icons-material/Close";
import { useEffect, useRef, useState } from "react";
import api from "../../../api/api";

const StyledTableCell = styled(TableCell)(({ theme }) => ({
  [`&.${tableCellClasses.head}`]: {
    backgroundColor: theme.palette.common.black,
    color: theme.palette.common.white,
  },
  [`&.${tableCellClasses.body}`]: {
    fontSize: 14,
  },
}));

const StyledTableRow = styled(TableRow)(({ theme }) => ({
  '&:nth-of-type(odd)': {
    backgroundColor: theme.palette.action.hover,
  },
  // hide last border
  '&:last-child td, &:last-child th': {
    border: 0,
  },
}));

export default function BidListModal({ open, onClose, auctionId, newBid }) {

  const [bids, setBids] = useState([]);
  const [hasNext, setHasNext] = useState(false);
  const lastBidIdRef = useRef(null);
  const size = 10;

  useEffect(() => {
    if (open) {
      setBids([]);
      setHasNext(false);
      lastBidIdRef.current = null;
      fetchBids(0); // 첫 페이지 로드
    }
  }, [open, auctionId]);

  // 실시간 입찰이 들어올 때 맨 앞에 추가
  useEffect(() => {
    if (newBid) {
      setBids(prev => [newBid, ...prev]);
    }
  }, [newBid]);

  const fetchBids = () => {
    let url = `/api/auctions/${auctionId}/bids?size=${size}`;
    if (lastBidIdRef.current) {
      url += `&lastBidId=${lastBidIdRef.current}`;
    }

    api.get(url)
      .then(res => {
        const { bids: newBids, hasNext: nextExists, lastBidId } = res.data;
        setBids(prev => [...prev, ...newBids]);
        setHasNext(nextExists);
        lastBidIdRef.current = lastBidId;
      });
  };


  return (
    <Dialog open={open} maxWidth="md" fullWidth>

      <DialogTitle>
        입찰 내역
        <IconButton
          aria-label="close"
          onClick={onClose}
          sx={() => ({
            position: 'absolute',
            right: 8,
            top: 8,
          })}
        >
          <CloseIcon />
        </IconButton>
      </DialogTitle>

      <DialogContent dividers>
        {bids.length === 0 ? (
          <p>입찰 내역이 없습니다.</p>
        ) : (
          <TableContainer component={Paper}>
            <Table>
              <TableHead>
                <TableRow>
                  <StyledTableCell align="left">입찰일시</StyledTableCell>
                  <StyledTableCell align="center">입찰자</StyledTableCell>
                  <StyledTableCell align="right">입찰금액</StyledTableCell>
                </TableRow>
              </TableHead>
              <TableBody>
                {bids.map(bid => (
                  <StyledTableRow key={bid.bidId}>
                    <StyledTableCell align="left">{new Date(bid.bidTime).toLocaleString()}</StyledTableCell>
                    <StyledTableCell align="center">{bid.bidderName}</StyledTableCell>
                    <StyledTableCell align="right">{bid.bidPrice.toLocaleString()}원</StyledTableCell>
                  </StyledTableRow>
                ))}
              </TableBody>
            </Table>
          </TableContainer>
        )}

        {hasNext && (
          <Button
            sx={{ mt: 1 }}
            variant="outlined"
            color="black"
            fullWidth
            onClick={() => fetchBids()}
          >
            더보기
          </Button>
        )}

      </DialogContent>
    </Dialog>
  );
}