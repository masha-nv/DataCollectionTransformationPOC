import { Box, Typography } from "@mui/material";
import React from "react";

const Header = ({ subtitle }: { subtitle?: string }) => {
  return (
    <Box sx={{ textAlign: "center" }}>
      <Typography variant='h4' gutterBottom>
        Data Collection Transformation | Proof-of-Concept – OME & MSIX
      </Typography>
      <Typography variant='h5' gutterBottom>
        {subtitle}
      </Typography>
    </Box>
  );
};

export default Header;
