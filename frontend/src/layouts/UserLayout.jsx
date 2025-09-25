import { Container } from '@mui/material';
import { Outlet } from 'react-router-dom'
import Header from '../components/user/Header';
import Footer from '../components/user/Footer';
import AppAppBar from '../components/user/AppAppBar';

export default function UserLayout() {
  return (
    <>
      {/* <Header /> */}
      <AppAppBar/>
        <Outlet />
      <Footer/>
      </>
  );
}