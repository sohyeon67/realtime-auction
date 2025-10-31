import { Outlet } from 'react-router-dom'
import Header from '../components/user/Header';
import Footer from '../components/user/Footer';
import { Box } from '@mui/material';

export default function UserLayout() {
  return (
    <>
      <Header />
      <Box sx={{ pt: { xs: 14, sm: 20 }, pb: { xs: 8, sm: 12 } }}>
        <Outlet />
      </Box>
      <Footer />
    </>
  );
}