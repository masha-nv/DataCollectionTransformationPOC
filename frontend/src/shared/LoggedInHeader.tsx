import {
  AppBar,
  Box,
  Drawer,
  Icon,
  IconButton,
  Toolbar,
  Typography,
} from "@mui/material";
import MenuIcon from "@mui/icons-material/Menu";
import SearchIcon from "@mui/icons-material/Search";
import AccountCircleIcon from "@mui/icons-material/AccountCircle";
import AppsIcon from "@mui/icons-material/Apps";
import React, { useState } from "react";
import MenuList from "./MenuList";

const LoggedInHeader = () => {
  const [open, setOpen] = useState<boolean>(false);
  const toggleDrawer = (newOpen: boolean) => () => setOpen(newOpen);
  return (
    <>
      <AppBar
        position='static'
        color='transparent'
        sx={{ boxShadow: "none", height: "10vh", padding: "1rem 2rem" }}>
        <Toolbar sx={{ display: "flex", justifyContent: "space-between" }}>
          <Box sx={{ display: "flex", gap: "1rem", alignItems: "center" }}>
            <IconButton onClick={toggleDrawer(true)}>
              <MenuIcon sx={{ fontSize: "2rem" }} />
            </IconButton>
            <Typography color='#102f3c' fontWeight={"bold"} fontSize={"1.5rem"}>
              ED DataFlow
            </Typography>
          </Box>
          <Box sx={{ display: "flex", gap: "1rem" }}>
            <IconButton>
              <SearchIcon sx={{ fontSize: "2.5rem" }} />
            </IconButton>
            <IconButton>
              <AppsIcon sx={{ fontSize: "2.5rem" }} />
            </IconButton>
            <IconButton>
              <AccountCircleIcon sx={{ fontSize: "2.5rem" }} />
            </IconButton>
          </Box>
        </Toolbar>
      </AppBar>
      <Drawer open={open} onClose={toggleDrawer(false)}>
        <MenuList toggleDrawer={toggleDrawer} />
      </Drawer>
    </>
  );
};

export default LoggedInHeader;
