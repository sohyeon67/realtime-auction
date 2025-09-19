import { Container } from '@mui/material';
import { Outlet } from 'react-router-dom'



export default function UserLayout() {
  return (
    <Container fixed>
      <header style={{ background: "#eee", padding: "1rem" }}>사용자 헤더</header>
      <main style={{ padding: "1rem" }}>
        <Outlet />
      </main>
      <footer style={{ background: "#eee", padding: "1rem" }}>푸터</footer>
    </Container>
  );
}