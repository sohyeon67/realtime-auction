import { AppBar, Box, IconButton, InputBase, Paper, Toolbar, Typography } from '@mui/material';
import NotificationsIcon from "@mui/icons-material/Notifications";
import AccountCircle from "@mui/icons-material/AccountCircle";
import SearchIcon from '@mui/icons-material/Search';

function Header() {
  return (
    <AppBar position="static" color='transparent' elevation={0}>
      <Toolbar
        sx={{
          display : 'flex',
          justifyContent: 'space-between',
        }}
      >
        <Typography variant="h6" component="div">
          경매
        </Typography>

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

        <Box>
          <IconButton>
            <NotificationsIcon />
          </IconButton>
          <IconButton>
            <AccountCircle />
          </IconButton>
        </Box>

      </Toolbar>
    </AppBar>
  )
}

export default Header