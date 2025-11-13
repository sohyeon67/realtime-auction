import { createTheme } from "@mui/material";
import { grey } from "@mui/material/colors";

const theme = createTheme({
  palette: {
    black: {
      main: "#000000",
      contrastText: "#ffffff",
    },
    lightGray: {
      main: grey[500],
      contrastText: "#ffffff",
    },
    naver: {
      main: "#03a94d",
      contrastText: "#ffffff",
    },
    google: {
      main: "#0b57d0",
      contrastText: "#ffffff",
    },
  }
});

export default theme;