import { Box, IconButton, Typography } from "@mui/material";
import React, { ReactNode, useContext } from "react";
import MSIXIcon from "../../assets/MSIXIcon.png";
import classes from "./Header.module.scss";
import SearchBar from "../search-bar/SearchBar";
import AppBarIcons from "../app-bar-icons/AppBarIcons";
import { GlobalStateContext } from "../../../store/GlobalStateProvider";

const Header = () => {
  const ctx = useContext(GlobalStateContext);
  return (
    <Box className={classes.container}>
      <Box className={classes.logo}>
        <img src={MSIXIcon} height={"40px"} />
        <Typography
          className={classes.logoText}
          sx={{ fontWeight: 100, fontSize: "2rem" }}>
          MSIX
        </Typography>
        <div className={classes.verticalBorder}></div>
        <Box className={classes.logoText}>
          <Typography sx={{ fontWeight: 100, fontSize: "2rem" }}>
            DataFlow
          </Typography>
        </Box>
      </Box>
      <Box sx={{ display: "flex", gap: "1rem" }}>
        <SearchBar />
        {ctx?.state?.isLoggedIn && <AppBarIcons />}
      </Box>
    </Box>
  );
};

export default Header;
