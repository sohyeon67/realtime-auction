import { Container } from '@mui/material';
import { Outlet } from 'react-router-dom'
import Header from '../components/user/Header';



export default function UserLayout() {
  return (
    <Container>
      <Header />
      <main style={{ padding: "1rem" }}>
        <Outlet />
      </main>
      <footer style={{ background: "#eee", padding: "1rem" }}>푸터</footer>
    </Container>
  );
}