import React from "react";
import { Box, Button, Typography } from "@mui/material";
import classes from "./DataTransferTool.module.scss";
import BackButton from "../../shared/back-button/BackButton";
import Steps from "./steps/Steps";

const DataTransferTool = () => {
  return (
    <>
      <BackButton text='Home' url='/home' />
      <Box className={classes.container}>
        <Box sx={{ width: "60%" }}>
          <Typography className={classes.title}>Data Transfer Tool</Typography>
          <Steps />
        </Box>
      </Box>
    </>
  );
};

export default DataTransferTool;
