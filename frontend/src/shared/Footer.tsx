import { Box, Typography } from "@mui/material";
import React from "react";

const Footer = () => {
  return (
    <Box
      sx={{
        width: "100vw",
        backgroundColor: "#d3ecef",
        display: "flex",
        flexDirection: "column",
        paddingX: "2rem",
        height: "10vh",
        justifyContent: "center",
      }}>
      <Typography>www.dataflow.ed.gov</Typography>
      <Typography fontWeight={"bold"}>
        An official website of the Department of Education
      </Typography>
    </Box>
  );
};

export default Footer;
