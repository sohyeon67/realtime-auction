import { BrowserRouter, Navigate, Route, Routes } from 'react-router-dom'
// 레이아웃
import AuthLayout from './layouts/AuthLayout';
import UserLayout from './layouts/UserLayout';
import AdminLayout from './layouts/AdminLayout';

// Auth 페이지
import Login from './pages/auth/Login';
import Signup from './pages/auth/Signup';

// User 페이지
import Home from './pages/user/Home';
import AuctionList from './pages/user/AuctionList';

// Admin 페이지
import Dashboard from './pages/admin/Dashboard';
import Users from './pages/admin/Users';
import SocialLoginSuccess from './pages/auth/SocialLoginSuccess';

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
          </Route>

          <Route path="/admin/*" element={<AdminLayout />}>
            <Route path="dashboard" element={<Dashboard />} />
            <Route path="users" element={<Users />} />
          </Route>

          <Route path="*" element={<Navigate to="/user/home" replace />} />
        </Routes>
      </BrowserRouter>
    </>
  )
}

export default App;
