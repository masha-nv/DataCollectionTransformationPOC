import { Box, Typography } from "@mui/material";
import React, { ReactNode } from "react";
import MSIXIcon from "../../assets/MSIXIcon.png";
import classes from "./Footer.module.scss";

const Footer = () => {
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
        <Box sx={{ marginLeft: "2rem" }}>
          <Typography sx={{ fontWeight: "50" }}>www.msix.ed.gov</Typography>
          <Typography sx={{ fontWeight: "500", fontSize: "1.2rem" }}>
            Migrant Student Information Exchange
          </Typography>
        </Box>
      </Box>
    </Box>
  );
};

export default Footer;
