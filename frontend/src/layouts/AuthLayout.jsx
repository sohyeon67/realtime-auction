import { Box, Button, Card, Container, createTheme, IconButton, Paper, ThemeProvider } from "@mui/material";
import { Outlet, useNavigate } from "react-router-dom";
import HomeIcon from '@mui/icons-material/Home';

export default function AuthLayout() {

  const navigate = useNavigate();

  return (
    <Container component="main" maxWidth="sm" sx={{ mt: 10 }}>
      <Card variant="outlined" sx={{ p: 4 }}>
        <Box sx={{ display: 'flex', justifyContent: 'center', mb: 2 }}>
          <IconButton color="primary" onClick={() => navigate("/")}>
            <HomeIcon fontSize="large"/>
          </IconButton>
        </Box>
        <Outlet />
      </Card>
    </Container>
  );
}