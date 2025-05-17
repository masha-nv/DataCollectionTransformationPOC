import { Box } from "@mui/material";
import React from "react";
import Header from "./Header";
import Footer from "./Footer";
import { Outlet } from "react-router-dom";

const Layout = () => {
  return (
    <Box>
      <Header />
      <Box sx={{ display: "flex", flexDirection: "column", minHeight: "80vh" }}>
        <Outlet />
      </Box>
      <Footer />
    </Box>
  );
};

export default Layout;
