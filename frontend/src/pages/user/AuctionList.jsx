import * as React from 'react';
import { useEffect, useState } from 'react';
import { Container, Box, Grid, Typography, Divider, Card, CardHeader, CardContent, CardMedia, CardActionArea, Stack, Pagination, TextField, Button, IconButton, Select, MenuItem, FormControl, InputLabel } from '@mui/material';
import { RichTreeView, SimpleTreeView, TreeItem } from '@mui/x-tree-view';
import api from '../../api/api';
import { useNavigate, useSearchParams } from 'react-router-dom';
import RefreshIcon from '@mui/icons-material/Refresh';

export default function AuctionList() {
  const navigate = useNavigate();

  const [searchParams, setSearchParams] = useSearchParams();

  const [categories, setCategories] = useState([]);
  const [auctions, setAuctions] = useState([]);
  const [totalElements, setTotalElements] = useState(0);
  const [totalPages, setTotalPages] = useState(1);

  // 검색 조건
  const [page, setPage] = useState(0);
  const [size, setSize] = useState(12);
  const [sort, setSort] = useState("POPULARITY");

  const [keyword, setKeyword] = useState("");
  const [categoryIds, setCategoryIds] = useState([]);
  const [sellerName, setSellerName] = useState("");
  const [minPrice, setMinPrice] = useState("");
  const [maxPrice, setMaxPrice] = useState("");

  // 카테고리 불러오기
  useEffect(() => {
    api.get("/api/categories")
      .then(res => {
        setCategories(res.data);
      })
      .catch(() => alert("카테고리 불러오기 실패"));
  }, []);

  // 경매 목록 불러오기
  const getAuctions = () => {
    const params = new URLSearchParams();

    params.append("page", page);
    params.append("size", size);
    params.append("sort", sort);

    if (keyword) params.append("keyword", keyword);
    if (categoryIds.length > 0) categoryIds.forEach(id => params.append("categoryIds", id));
    if (sellerName) params.append("sellerName", sellerName);
    if (minPrice) params.append("minPrice", minPrice);
    if (maxPrice) params.append("maxPrice", maxPrice);

    api.get(`/api/auctions?${params.toString()}`)
      .then(res => {
        setAuctions(res.data.content);
        setTotalPages(res.data.totalPages);
        setTotalElements(res.data.totalElements);
      })
      .catch(() => alert("경매 데이터 불러오기 실패"));
  };

  useEffect(() => {
    getAuctions();
  }, [page]);

  // 정렬 변경시 첫 페이지부터
  useEffect(() => {
    setPage(0);
    getAuctions();
  }, [sort]);

  // 검색 시 페이지 0으로 초기화
  const handleSearch = () => {
    setPage(0);
    getAuctions();
  };

  // RichTreeView에 맞는 데이터로 변환
  const renderTree = (nodes) => (
    nodes.map(node => ({
      id: node.id.toString(),
      label: node.name,
      children: renderTree(node.children)
    }))
  );

  const treeItems = renderTree(categories);


  return (
    <Container
      sx={{
        pt: { xs: 14, sm: 20 },
        pb: { xs: 8, sm: 12 },
        display: 'flex',
        flexDirection: 'column',
        alignItems: 'center',
        gap: { xs: 2, sm: 2 },
      }}
    >
      
      <Typography
        component="h2"
        variant="h4"
        sx={{ color: 'text.primary', width: { sm: '100%', md: '60%' }, textAlign: { sm: 'left', md: 'center' } }}
      >
        물품 목록
      </Typography>


      <Box
        sx={{
          width: '100%',
          display: 'flex',
          justifyContent: 'space-between',
          alignItems: 'center',
        }}
      >
        <Typography variant="subtitle1" >
          총 <strong style={{ color: 'orangered'}}>{totalElements?.toLocaleString() || 0}</strong>개
        </Typography>

        <FormControl size="small">
          <InputLabel id="sort-select-label">정렬</InputLabel>
          <Select
            labelId="sort-select-label"
            id="sort-select"
            label="Sort"
            value={sort}
            onChange={(e) => setSort(e.target.value)}
          >
            <MenuItem value="POPULARITY">인기경매순</MenuItem>
            <MenuItem value="ENDING_SOON">마감임박순</MenuItem>
            <MenuItem value="RECENT">신규경매순</MenuItem>
            <MenuItem value="PRICE_DESC">높은가격순</MenuItem>
            <MenuItem value="PRICE_ASC">낮은가격순</MenuItem>
          </Select>
        </FormControl>
      </Box>

      <Divider/>

      <Box sx={{ width: '100%', flexGrow: 1 }}>

        <Grid container spacing={2}>

          {/* 왼쪽 */}
          <Grid size={{ xs: 12, sm: 3 }}>

            <Card sx={{
              minHeight: 352,
              minWidth: 250,
            }}
            >
              <CardContent>

                <Stack direction="row" alignItems="center" justifyContent="space-between" spacing={1}>
                  <Typography variant="h6" gutterBottom>검색 필터</Typography>

                  <IconButton
                    size="small"
                    aria-label="refresh"
                    onClick={() => {
                      setKeyword("");
                      setSellerName("");
                      setMinPrice("");
                      setMaxPrice("");
                      setCategoryIds([]);
                      setPage(0);
                    }}
                  >
                    <RefreshIcon />
                  </IconButton>
                </Stack>


                <Divider sx={{ my: 2 }} />

                <Typography variant="subtitle1" gutterBottom>
                  카테고리
                </Typography>
                <RichTreeView
                  items={treeItems}
                  checkboxSelection
                  multiSelect
                  selectedItems={categoryIds}
                  selectionPropagation={{ parents: true, descendants: true }}
                  onSelectedItemsChange={(event, newSelectedItems) => setCategoryIds(newSelectedItems)}
                />

                <Divider sx={{ my: 2 }} />


                <Typography variant="subtitle1" gutterBottom>검색 조건</Typography>

                <TextField
                  label="검색어"
                  variant="outlined"
                  fullWidth
                  size="small"
                  margin="dense"
                  value={keyword}
                  onChange={(e) => setKeyword(e.target.value)}
                />
                <TextField
                  label="판매자명"
                  variant="outlined"
                  fullWidth
                  size="small"
                  margin="dense"
                  value={sellerName}
                  onChange={(e) => setSellerName(e.target.value)}
                />

                <Divider sx={{ my: 2 }} />


                <Typography variant="subtitle1" gutterBottom>가격</Typography>

                <TextField
                  label="최소가격"
                  variant="outlined"
                  size="small"
                  fullWidth
                  margin="dense"
                  value={minPrice}
                  onChange={(e) => setMinPrice(e.target.value)}
                />
                <TextField
                  label="최대가격"
                  variant="outlined"
                  size="small"
                  fullWidth
                  margin="dense"
                  value={maxPrice}
                  onChange={(e) => setMaxPrice(e.target.value)}
                />

                <Button
                  variant="contained"
                  fullWidth
                  sx={{ mt: 2 }}
                  onClick={handleSearch}
                >
                  검색
                </Button>

              </CardContent>



            </Card>
          </Grid>


          <Grid size={{ xs: 12, sm: 9 }}>

            <Grid container spacing={2}>
              {/* 오른쪽 내용 영역 */}
              {auctions.map(auction =>
                <Grid size={{ xs: 12, sm: 6, md: 4 }} key={auction.auctionId}>
                  <Card>
                    <CardActionArea
                      onClick={() => navigate(`/user/auctions/${auction.auctionId}`)}
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

                        <Typography variant="body2" color="text.secondary">
                          판매자: {auction.sellerNickname}
                        </Typography>

                        <Typography variant="body2" color="text.secondary">
                          현재가: {auction.currentPrice?.toLocaleString()}원
                        </Typography>

                        <Typography variant="body2" color="text.secondary">
                          입찰 수: {auction.bidCount}
                        </Typography>

                      </CardContent>

                    </CardActionArea>
                  </Card>
                </Grid>
              )}

            </Grid>

            <Stack spacing={2} sx={{ mt: 4, alignItems: 'center' }}>
              <Pagination
                count={totalPages}
                page={page + 1}
                onChange={(e, value) => setPage(value - 1)}
                showFirstButton
                showLastButton
              />
            </Stack>

          </Grid>


        </Grid>


      </Box>


    </Container>
  );
}
