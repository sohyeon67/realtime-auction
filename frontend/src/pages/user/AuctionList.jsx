import * as React from 'react';
import { useEffect, useState } from 'react';
import { Container, Box, Grid, Typography, Divider, Card, CardHeader, CardContent } from '@mui/material';
import { SimpleTreeView, TreeItem } from '@mui/x-tree-view';
import api from '../../api/api';

export default function AuctionList() {
  const [categories, setCategories] = useState([]);

  useEffect(() => {
    api.get("/api/categories")
      .then(res => setCategories(res.data))
      .catch(() => alert("카테고리 불러오기 실패"));
  }, []);

  // 재귀적으로 TreeItem 생성
  const renderTree = (nodes) => (
    nodes.map(node => (
      <TreeItem key={node.id} itemId={String(node.id)} label={node.name}>
        {node.children && node.children.length > 0 && renderTree(node.children)}
      </TreeItem>
    ))
  );

  return (
    <Container
      id="auctions"
      sx={{
        pt: { xs: 14, sm: 20 },
        pb: { xs: 8, sm: 12 },
        display: 'flex',
        flexDirection: 'column',
        alignItems: 'center',
        gap: { xs: 3, sm: 6 },
      }}
    >
      <Typography
        component="h2"
        variant="h4"
        sx={{ color: 'text.primary', width: { sm: '100%', md: '60%' }, textAlign: { sm: 'left', md: 'center' } }}
      >
        물품 목록
      </Typography>

      <Box sx={{ width: '100%', flexGrow: 1 }}>
        <Grid container spacing={2}>

          {/* 왼쪽 */}
          <Grid item xs={12} sm={3}>

            <Card sx={{
              minHeight: 352,
              minWidth: 250,
            }}
            >
              <CardHeader
                title="카테고리"
                titleTypographyProps={{ variant: "h6" }}
              />
              <Divider />
              <CardContent>
                <SimpleTreeView disableSelection>
                  {renderTree(categories)}
                </SimpleTreeView>
              </CardContent>

            </Card>
          </Grid>


          <Grid item xs={12} sm={9}>
            <Box>
              {/* 오른쪽 내용 영역 */}
              내용 영역
            </Box>
          </Grid>
        </Grid>
      </Box>
    </Container>
  );
}
