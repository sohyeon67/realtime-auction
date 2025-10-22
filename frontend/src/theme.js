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
  }
});

export default theme;