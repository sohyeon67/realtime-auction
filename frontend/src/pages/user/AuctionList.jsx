import { useEffect, useState } from 'react';
import { Container, Box, Grid, Typography, Divider, Card, CardContent, CardMedia, CardActionArea, Stack, Pagination, TextField, Button, IconButton, Select, MenuItem, FormControl, InputLabel, CardHeader } from '@mui/material';
import { RichTreeView } from '@mui/x-tree-view';
import api from '../../api/api';
import { useNavigate, useSearchParams } from 'react-router-dom';
import RefreshIcon from '@mui/icons-material/Refresh';
import { NumericFormat } from 'react-number-format';
import AuctionStatus from '../../components/user/auction/AuctionStatus';
import AccessAlarmIcon from '@mui/icons-material/AccessAlarm';
import { getRemainingTime } from '../../utils/time';
import { useCategories } from '../../hooks/useCategories';
import AuctionCard from '../../components/user/auction/AuctionCard';

export default function AuctionList() {
  const navigate = useNavigate();

  const { categories } = useCategories();

  // 브라우저 URL 쿼리스트링을 관리하기 위함
  const [searchParams, setSearchParams] = useSearchParams();

  const [auctions, setAuctions] = useState([]);
  const [totalElements, setTotalElements] = useState(0);
  const [totalPages, setTotalPages] = useState(1);

  // 검색 조건
  const page = parseInt(searchParams.get("page")) || 1;
  const size = parseInt(searchParams.get("size")) || 12;
  const sort = searchParams.get("sort") || "POPULARITY";

  // 최초 렌더링 시 URL에 있는 값으로 초기화하기 위해서
  const [keyword, setKeyword] = useState(searchParams.get("keyword") || "");
  const [categoryIds, setCategoryIds] = useState(searchParams.getAll("categoryIds") || []);
  const [sellerName, setSellerName] = useState(searchParams.get("sellerName") || "");
  const [minPrice, setMinPrice] = useState(searchParams.get("minPrice") || "");
  const [maxPrice, setMaxPrice] = useState(searchParams.get("maxPrice") || "");

  const [errors, setErrors] = useState({ minPrice: "", maxPrice: "" });

  // 경매 목록 불러오기
  const getAuctions = () => {
    // 서버에 보낼 쿼리스트링
    const params = new URLSearchParams();

    // 실제 페이지는 -1 해줘야 함
    params.append("page", page - 1);
    params.append("size", size);
    params.append("sort", sort);

    // 검색 필터 값이 있는 것들만 세팅
    if (categoryIds?.length > 0) categoryIds.forEach(id => params.append("categoryIds", id));
    if (keyword?.trim()) params.append("keyword", keyword.trim());
    if (sellerName?.trim()) params.append("sellerName", sellerName.trim());
    if (minPrice) params.append("minPrice", parseInt(minPrice.replace(/,/g, ''), 10));
    if (maxPrice) params.append("maxPrice", parseInt(maxPrice.replace(/,/g, ''), 10));

    api.get(`/api/auctions?${params.toString()}`)
      .then(res => {
        setAuctions(res.data.content);
        setTotalPages(res.data.totalPages);
        setTotalElements(res.data.totalElements);
      })
      .catch(() => alert("경매 데이터 불러오기 실패"));
  };

  // searchParams가 바뀔 때마다 경매 데이터 가져오기
  useEffect(() => {
    getAuctions();
  }, [searchParams]);


  // 검색 버튼 클릭시
  const handleSearch = () => {
    // 1. 가격 검사
    const min = minPrice ? parseInt(minPrice.replace(/,/g, ''), 10) : null;
    const max = maxPrice ? parseInt(maxPrice.replace(/,/g, ''), 10) : null;

    const newErrors = { minPrice: "", maxPrice: "" };

    if (min !== null && min < 1000) newErrors.minPrice = "최소가격은 1,000원 이상이어야 합니다.";
    if (max !== null && max < 1000) newErrors.maxPrice = "최대가격은 1,000원 이상이어야 합니다.";
    if (min !== null && max !== null && max < min) newErrors.maxPrice = "최대가격은 최소가격 이상이어야 합니다.";

    setErrors(newErrors);

    if (newErrors.minPrice || newErrors.maxPrice) return;

    // 2. url 업데이트
    // 유지되는 값들
    const params = {
      size: searchParams.get("size") || 12,
      sort: searchParams.get("sort") || "POPULARITY",
      page: 1,
    };

    // 값이 있을 때만 url에 붙인다.
    if (categoryIds?.length > 0) params.categoryIds = categoryIds;
    if (keyword?.trim()) params.keyword = keyword.trim();
    if (sellerName?.trim()) params.sellerName = sellerName.trim();
    if (minPrice) params.minPrice = min;
    if (maxPrice) params.maxPrice = max;

    // 브라우저 URL 쿼리파라미터 세팅
    setSearchParams(params);
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
        fontWeight="bold"
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
          총 <strong style={{ color: 'orangered' }}>{totalElements?.toLocaleString() || 0}</strong>개
        </Typography>

        <Stack direction="row" spacing={2}>

          <FormControl size="small" sx={{ minWidth: 120 }}>
            <InputLabel id="size-select-label">페이지당 항목</InputLabel>
            <Select
              labelId="size-select-label"
              id="size-select"
              label="페이지당 항목"
              value={size}
              onChange={(e) => setSearchParams({ ...Object.fromEntries(searchParams), size: e.target.value, page: 1 })}
            >
              <MenuItem value={12}>12</MenuItem>
              <MenuItem value={24}>24</MenuItem>
              <MenuItem value={36}>36</MenuItem>
              <MenuItem value={48}>48</MenuItem>
            </Select>
          </FormControl>

          <FormControl size="small">
            <InputLabel id="sort-select-label">정렬</InputLabel>
            <Select
              labelId="sort-select-label"
              id="sort-select"
              label="정렬"
              value={sort}
              onChange={(e) => setSearchParams({ ...Object.fromEntries(searchParams), page: 1, sort: e.target.value })}
            >
              <MenuItem value="POPULARITY">인기경매순</MenuItem>
              <MenuItem value="ENDING_SOON">마감임박순</MenuItem>
              <MenuItem value="RECENT">신규경매순</MenuItem>
              <MenuItem value="PRICE_DESC">높은가격순</MenuItem>
              <MenuItem value="PRICE_ASC">낮은가격순</MenuItem>
            </Select>

          </FormControl>

        </Stack>
      </Box>

      <Divider />

      <Box sx={{ width: '100%', flexGrow: 1 }}>

        <Grid container spacing={2}>

          {/* 왼쪽 내용 영역 */}
          <Grid size={{ xs: 12, sm: 3 }}>

            <Card sx={{
              minHeight: 352,
              minWidth: 250,
            }}
            >
              <CardContent>

                <Stack direction="row" alignItems="center" justifyContent="space-between" spacing={1}>
                  <Typography variant="h6" gutterBottom>검색 필터</Typography>

                  {/* 초기화 버튼 */}
                  <IconButton
                    size="small"
                    aria-label="refresh"
                    onClick={() => {
                      setKeyword("");
                      setSellerName("");
                      setMinPrice("");
                      setMaxPrice("");
                      setCategoryIds([]);
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

                <NumericFormat
                  customInput={TextField}
                  label="최소가격"
                  variant="outlined"
                  size="small"
                  fullWidth
                  margin="dense"
                  value={minPrice}
                  onChange={(e) => setMinPrice(e.target.value)}
                  placeholder="1,000"
                  thousandSeparator
                  error={!!errors.minPrice}
                  helperText={errors.minPrice}
                />
                <NumericFormat
                  customInput={TextField}
                  label="최대가격"
                  variant="outlined"
                  size="small"
                  fullWidth
                  margin="dense"
                  value={maxPrice}
                  onChange={(e) => setMaxPrice(e.target.value)}
                  placeholder="10,000,000"
                  thousandSeparator
                  error={!!errors.maxPrice}
                  helperText={errors.maxPrice}
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


          {/* 오른쪽 내용 영역 */}
          <Grid size={{ xs: 12, sm: 9 }}>

            <Grid container spacing={2}>
              {auctions.map(auction =>
                <Grid size={{ xs: 12, sm: 6, md: 4 }} key={auction.auctionId}>
                  <AuctionCard auction={auction} searchParams={searchParams} />
                </Grid>
              )}

            </Grid>

            <Stack spacing={2} sx={{ mt: 4, alignItems: 'center' }}>
              <Pagination
                count={totalPages}
                page={page}
                onChange={(e, value) => {
                  setSearchParams({ ...Object.fromEntries(searchParams), page: value });
                  window.scrollTo({ top: 0, behavior: 'smooth' });
                }}
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
