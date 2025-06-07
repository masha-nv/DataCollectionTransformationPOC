import { Box } from "@mui/material";
import React, { useContext } from "react";
import Header from "../header/Header";
import Footer from "../footer/Footer";
import { Outlet } from "react-router-dom";
import classes from "./Layout.module.scss";

const Layout = () => {
  return (
    <Box>
      <Header />
      <Box className={classes.container}>
        <Outlet />
      </Box>
      <Footer />
    </Box>
  );
};

export default Layout;
