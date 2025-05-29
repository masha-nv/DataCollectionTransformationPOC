import { Box, List, ListItem, ListItemButton, Typography } from "@mui/material";
import React, { useContext } from "react";
import { NavLink, useNavigate } from "react-router-dom";
import "./MenuList.css";
import { GlobalStateContext } from "../../store/GlobalStateProvider";

const MenuList = ({ toggleDrawer }) => {
  const navigate = useNavigate();
  const ctx = useContext(GlobalStateContext);
  function onLogout() {
    ctx?.dispatch({ type: "logout" });
    navigate("/");
  }
  return (
    <Box sx={{ width: 250 }} role='presentation' onClick={toggleDrawer(false)}>
      <List sx={{ height: "90vh" }}>
        {[
          { text: "LEA Data Upload Portal", path: "/lea" },
          { text: "School Data Upload Portal", path: "/school" },
          { text: "OME Data Extraction Portal", path: "/file" },
        ].map((item, index) => (
          <ListItem key={index} disablePadding>
            <ListItemButton>
              <NavLink
                className={"menu-list-item"}
                to={item.path}
                style={{ textDecoration: "none", color: "#102f3c" }}>
                <Typography>{item.text}</Typography>
              </NavLink>
            </ListItemButton>
          </ListItem>
        ))}
        <ListItem sx={{ marginTop: "80vh", cursor: "pointer" }}>
          <ListItemButton onClick={onLogout}>Logout</ListItemButton>
        </ListItem>
      </List>
    </Box>
  );
};

export default MenuList;
