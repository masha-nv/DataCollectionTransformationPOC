import { Box, Typography } from "@mui/material";
import React from "react";
import DepartmentOfEducationIcon from "../assets/DOEIcon.svg";

const Header = ({ subtitle }: { subtitle?: string }) => {
  return (
    <Box
      sx={{
        display: "flex",
        backgroundColor: "#102f3c",
        width: "100vw",
        alignItems: "center",
        justifyContent: "space-between",
        padding: "1rem 2rem",
        height: "10vh",
      }}>
      <img
        src={DepartmentOfEducationIcon}
        width={"100vh"}
        style={{
          float: "left",
          paddingLeft: "2rem",
        }}
      />
      <Box
        sx={{
          display: "flex",
          flexDirection: "column",
          alignItems: "center",
          justifyContent: "center",
        }}>
        <Typography variant='h4' gutterBottom fontWeight={"bold"} color='white'>
          ED DataFlow
        </Typography>
        <ul
          style={{
            listStyle: "none",
            display: "flex",
            gap: "2rem",
            color: "#d3ecef",
          }}>
          <li>Home</li>
          <li>About Us</li>
          <li>Services</li>
          <li>Contact</li>
          <li>Support</li>
        </ul>
      </Box>

      <Typography variant='h5' gutterBottom>
        {subtitle}
      </Typography>
    </Box>
  );
};

export default Header;
