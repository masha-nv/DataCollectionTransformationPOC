import { Box } from "@mui/material";
import React, { useContext } from "react";
import Header from "./Header";
import Footer from "./Footer";
import { Outlet } from "react-router-dom";
import LoggedInHeader from "./LoggedInHeader";
import { GlobalStateContext } from "../../store/GlobalStateProvider";

const Layout = () => {
  const ctx = useContext(GlobalStateContext);
  return (
    <Box>
      {ctx?.state?.isLoggedIn ? <LoggedInHeader /> : <Header />}
      <Box sx={{ display: "flex", flexDirection: "column", minHeight: "80vh" }}>
        <Outlet />
      </Box>
      <Footer />
    </Box>
  );
};

export default Layout;
