import GavelIcon from '@mui/icons-material/Gavel';
import { Box } from '@mui/material';

export default function SitemarkIcon() {
  return (
    <Box display="flex" alignItems="center" gap={1} sx={{ mr: 2 }}>
      <GavelIcon />
      <Box display="flex" flexDirection="column" lineHeight={1}>
        <span style={{ fontWeight: 600 }}>ready</span>
        <span style={{ fontWeight: 600 }}>auction</span>
      </Box>
    </Box>
  );
}
