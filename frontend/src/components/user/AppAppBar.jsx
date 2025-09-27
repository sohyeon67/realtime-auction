import * as React from 'react';
import { styled, alpha } from '@mui/material/styles';
import Box from '@mui/material/Box';
import AppBar from '@mui/material/AppBar';
import Toolbar from '@mui/material/Toolbar';
import Button from '@mui/material/Button';
import IconButton from '@mui/material/IconButton';
import Container from '@mui/material/Container';
import Divider from '@mui/material/Divider';
import MenuItem from '@mui/material/MenuItem';
import Drawer from '@mui/material/Drawer';
import MenuIcon from '@mui/icons-material/Menu';
import CloseRoundedIcon from '@mui/icons-material/CloseRounded';
import Sitemark from './SitemarkIcon';
import NotificationsIcon from "@mui/icons-material/Notifications";
import AccountCircle from "@mui/icons-material/AccountCircle";
import SearchIcon from '@mui/icons-material/Search';
import { InputBase, Paper } from '@mui/material';
import { Link } from 'react-router-dom';

export default function AppAppBar() {
  const [open, setOpen] = React.useState(false);

  const toggleDrawer = (newOpen) => () => {
    setOpen(newOpen);
  };

  return (
    <AppBar
      position="fixed"
      sx={{
        boxShadow: 0,
        // bgcolor: 'transparent',
        backgroundImage: 'none',
        // mt: 'calc(var(--template-frame-height, 0px) + 28px)',
      }}
    >
      <Container maxWidth="lg">
        <Toolbar variant="regular" disableGutters>
          <Box sx={{ flexGrow: 1, display: 'flex', alignItems: 'center', px: 0 }}>

            <Sitemark />
            <Box sx={{ display: { xs: 'none', md: 'flex' } }}>
              <Button variant="text" color="info" size="small">
                경매
              </Button>
              <Button variant="text" color="info" size="small">
                FAQ
              </Button>
              <Button variant="text" color="info" size="small" sx={{ minWidth: 0 }}>
                FAQ
              </Button>
            </Box>

            <Paper
              variant='outlined'
              component="form"
              sx={{
                p: '2px 4px',
                display: 'flex',
                width: 400,
              }}
            >
              <InputBase
                sx={{ ml: 1, flex: 1 }}
                placeholder="물품명 검색"
                inputProps={{ 'aria-label': 'search' }}
              />
              <IconButton type="button" sx={{ p: '10px' }} aria-label="search">
                <SearchIcon />
              </IconButton>
            </Paper>

          </Box>

          <Box
            sx={{
              display: { xs: 'none', md: 'flex' },
              gap: 1,
              alignItems: 'center',
            }}
          >
            <Box>
              <IconButton color="inherit">
                <NotificationsIcon />
              </IconButton>
              <IconButton color="inherit">
                <AccountCircle />
              </IconButton>
            </Box>
            <Button color="primary" variant="contained" size="small" component={Link} to="/user/auctions/register">
              물품등록
            </Button>
            <Button color="primary" variant="contained" size="small">
              로그인
            </Button>
            <Button color="primary" variant="contained" size="small">
              회원가입
            </Button>
          </Box>

          {/* 반응형 */}
          <Box sx={{ display: { xs: 'flex', md: 'none' }, gap: 1 }}>
            <IconButton aria-label="Menu button" onClick={toggleDrawer(true)}>
              <MenuIcon />
            </IconButton>
            <Drawer
              anchor="top"
              open={open}
              onClose={toggleDrawer(false)}
              PaperProps={{
                sx: {
                  top: 'var(--template-frame-height, 0px)',
                },
              }}
            >
              <Box sx={{ p: 2, backgroundColor: 'background.default' }}>
                <Box
                  sx={{
                    display: 'flex',
                    justifyContent: 'flex-end',
                  }}
                >
                  <IconButton onClick={toggleDrawer(false)}>
                    <CloseRoundedIcon />
                  </IconButton>
                </Box>

                <MenuItem>Features</MenuItem>
                <MenuItem>Testimonials</MenuItem>
                <MenuItem>Highlights</MenuItem>
                <MenuItem>Pricing</MenuItem>
                <MenuItem>FAQ</MenuItem>
                <MenuItem>Blog</MenuItem>
                <Divider sx={{ my: 3 }} />
                <MenuItem>
                  <Button color="primary" variant="contained" fullWidth>
                    Sign up
                  </Button>
                </MenuItem>
                <MenuItem>
                  <Button color="primary" variant="outlined" fullWidth>
                    로그인
                  </Button>
                </MenuItem>
              </Box>
            </Drawer>
          </Box>


        </Toolbar>
      </Container>
    </AppBar>
  );
}
