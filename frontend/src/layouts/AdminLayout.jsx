import { Outlet, Link } from "react-router-dom";
import { Box, AppBar, Toolbar, Typography, Drawer, List, ListItem, ListItemText, Divider } from "@mui/material";
import Sidebar from "../components/admin/Sidebar";
import Header from "../components/admin/Header";

const drawerWidth = 240;

export default function AdminLayout() {
  
  return (
    <Box sx={{ display: "flex", minHeight: "100vh" }}>
      {/* 사이드바 */}
      <Sidebar/>

      {/* 메인 컨텐츠 */}
       <Box sx={{ flexGrow: 1, display: "flex", flexDirection: "column" }}>
        <Header />
        <Box component="main" sx={{ flexGrow: 1, p: 3, bgcolor: "#f9f9f9" }}>
          <Outlet />
        </Box>
      </Box>
    </Box>
  );
}
