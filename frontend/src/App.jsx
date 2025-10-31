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
import AuctionList from './pages/user/AuctionList';
import AuctionRegister from './pages/user/AuctionRegister';
import AuctionDetails from './pages/user/AuctionDetails';
import AuctionEdit from './pages/user/AuctionEdit';

// Admin 페이지
import Dashboard from './pages/admin/Dashboard';
import Users from './pages/admin/Users';
import Categories from './pages/admin/Categories';
import { AuthProvider } from './contexts/AuthContext';
import ProtectedRoute from './routes/ProtectedRoute';


function App() {

  return (
    <AuthProvider>
      <BrowserRouter>
        <Routes>
          <Route path="/auth/callback" element={<SocialLoginSuccess />} />

          <Route path="/auth/*" element={<AuthLayout />}>
            <Route path="login" element={<Login />} />
            <Route path="signup" element={<Signup />} />
          </Route>

          <Route path="/user/*" element={<UserLayout />}>
            <Route path="auctions" element={<AuctionList />} />
            <Route path="auctions/:auctionId" element={<AuctionDetails />} />

            {/* 인증된 사용자만 가능 */}
            <Route element={<ProtectedRoute />}>
              <Route path="auctions/register" element={<AuctionRegister />} />
              <Route path="auctions/:auctionId/edit" element={<AuctionEdit />} />
            </Route>
          </Route>

          <Route path="/admin/*" element={<AdminLayout />}>
            <Route path="dashboard" element={<Dashboard />} />
            <Route path="users" element={<Users />} />
            <Route path="categories" element={<Categories />} />
          </Route>

          <Route path="*" element={<Navigate to="/user/auctions" replace />} />
        </Routes>
      </BrowserRouter>
    </AuthProvider>
  )
}

export default App;
