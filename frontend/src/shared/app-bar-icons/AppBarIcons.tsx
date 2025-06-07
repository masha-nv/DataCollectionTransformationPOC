import React, { useState } from "react";
import MenuIcon from "@mui/icons-material/Menu";
import { Drawer, IconButton } from "@mui/material";
import MenuList from "../MenuList";
import AccountCircleIcon from "@mui/icons-material/AccountCircle";

const Flyout = () => {
  const [open, setOpen] = useState<boolean>(false);
  const toggleDrawer = (newOpen: boolean) => () => null; //setOpen(newOpen);
  return (
    <div>
      <IconButton onClick={toggleDrawer(true)}>
        <MenuIcon sx={{ fontSize: "2rem" }} />
      </IconButton>
      <IconButton>
        <AccountCircleIcon sx={{ fontSize: "2rem" }} />
      </IconButton>
      <Drawer open={open} onClose={toggleDrawer(false)}>
        <MenuList toggleDrawer={toggleDrawer} />
      </Drawer>
    </div>
  );
};

export default Flyout;
