import { Outlet } from 'react-router-dom'

export default function AdminLayout() {
  return (
    <div>
      <header style={{ background: "black", color: "white", padding: "1rem" }}>관리자 헤더</header>
      <main style={{ padding: "1rem" }}>
        <Outlet />
      </main>
    </div>
  );
}