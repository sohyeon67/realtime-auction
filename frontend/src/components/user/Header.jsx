import { useState } from 'react';
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
import { Link, useLocation, useNavigate } from 'react-router-dom';
import { useAuth } from '../../contexts/AuthContext';

export default function Header() {

  const location = useLocation();
  const navigate = useNavigate();
  const { isLoggedIn, logout } = useAuth();

  const handleLogin = () => {
    navigate('/auth/login', { state: { from: location } });
  };

  const handleLogout = () => {
    logout();
    navigate("/", { replace: true });
  };

  const [open, setOpen] = useState(false);
  const toggleDrawer = (newOpen) => () => setOpen(newOpen);

  return (
    <AppBar
      position="fixed"
      sx={{
        boxShadow: 0,
        backgroundImage: 'none',
        py: '0.5rem'
      }}
    >
      <Container maxWidth="lg">
        <Toolbar variant="regular" disableGutters>
          <Box sx={{ flexGrow: 1, display: 'flex', alignItems: 'center', px: 0 }}>

            <Link
              to="/"
              style={{ textDecoration: 'none', color: 'inherit' }}
            >
              <Sitemark />
            </Link>

            <Box
              sx={{
                position: 'absolute',
                left: '50%',
                transform: 'translateX(-50%)',
                width: 400,
              }}
            >
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

          </Box>

          <Box
            sx={{
              display: { xs: 'none', md: 'flex' },
              gap: 1,
              alignItems: 'center',
            }}
          >

            <Button color="inherit" variant="text" size="medium" component={Link} to="/user/auctions/register" sx={{ whiteSpace: 'nowrap' }}>
              물품등록
            </Button>

            {!isLoggedIn && (
              <>
                <Divider orientation="vertical" variant="middle" flexItem />
                <Button color="inherit" variant="text" size="medium" onClick={handleLogin} sx={{ whiteSpace: 'nowrap' }}>
                  로그인
                </Button>
                <Divider orientation="vertical" variant="middle" flexItem />
                <Button color="inherit" variant="text" size="medium" component={Link} to="/auth/signup" sx={{ whiteSpace: 'nowrap' }}>
                  회원가입
                </Button>
              </>
            )}

            {isLoggedIn && (
              <>
                <Divider orientation="vertical" variant="middle" flexItem />
                <Button color="inherit" variant="text" size="medium" onClick={handleLogout} sx={{ whiteSpace: 'nowrap' }}>
                  로그아웃
                </Button>
                <IconButton color="inherit">
                  <NotificationsIcon />
                </IconButton>
                <IconButton color="inherit">
                  <AccountCircle />
                </IconButton>
              </>
            )}

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
                  <Button color="primary" variant="outlined" fullWidth onClick={handleLogin}>
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
