import { BrowserRouter, Navigate, Route, Routes } from 'react-router-dom'
// 레이아웃
import AuthLayout from './layouts/AuthLayout';
import UserLayout from './layouts/UserLayout';
import AdminLayout from './layouts/AdminLayout';

// Auth 페이지
import Login from './pages/auth/Login';
import Signup from './pages/auth/Signup';
import SocialLoginSuccess from './pages/auth/SocialLoginSuccess';

// User 페이지
import Home from './pages/user/Home';
import AuctionList from './pages/user/AuctionList';

// Admin 페이지
import Dashboard from './pages/admin/Dashboard';
import Users from './pages/admin/Users';
import Categories from './pages/admin/Categories';
import AuctionRegister from './pages/user/AuctionRegister';
import AuctionDetails from './pages/user/AuctionDetails';


function App() {

  return (
    <>
      <BrowserRouter>
        <Routes>
          <Route path="/auth/callback" element={<SocialLoginSuccess />} />

          <Route path="/auth/*" element={<AuthLayout />}>
            <Route path="login" element={<Login />} />
            <Route path="signup" element={<Signup />} />
          </Route>

          <Route path="/user/*" element={<UserLayout />}>
            <Route path="home" element={<Home />} />
            <Route path="auctions" element={<AuctionList />} />
            <Route path="auctions/register" element={<AuctionRegister />} />
            <Route path="auctions/:auctionId" element={<AuctionDetails />} />
            <Route path="auctions/:auctionId/edit" element={<AuctionRegister />} />
          </Route>

          <Route path="/admin/*" element={<AdminLayout />}>
            <Route path="dashboard" element={<Dashboard />} />
            <Route path="users" element={<Users />} />
            <Route path="categories" element={<Categories />} />
          </Route>

          <Route path="*" element={<Navigate to="/user/home" replace />} />
        </Routes>
      </BrowserRouter>
    </>
  )
}

export default App;
