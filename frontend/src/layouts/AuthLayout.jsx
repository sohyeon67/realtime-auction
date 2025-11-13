import { Box, Card, Container, IconButton } from "@mui/material";
import { Outlet, useNavigate } from "react-router-dom";
import SitemarkIcon from "../components/user/SitemarkIcon";

export default function AuthLayout() {

  const navigate = useNavigate();

  return (
    <Container component="main" maxWidth="sm" sx={{ mt: 12 }}>
      <Card variant="outlined" sx={{ p: 4 }}>
        <Box sx={{ display: 'flex', justifyContent: 'center', my: 3 }}>
          <IconButton color="primary" onClick={() => navigate("/")}>
            <SitemarkIcon />
          </IconButton>
        </Box>
        <Outlet />
      </Card>
    </Container>
  );
}