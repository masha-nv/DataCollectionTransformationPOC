import { Box, Typography } from "@mui/material";
import React, { useContext } from "react";
import BackButton from "../back-button/BackButton";
import classes from "./SuccessDataUpload.module.scss";
import { useLocation } from "react-router-dom";
import CheckCircle from "@mui/icons-material/CheckCircleOutline";
import FileInfo from "../file-info/FileInfo";
import { FileDataContext } from "../../../store/fileData/FileDataProvider";

const SuccessDataUpload = () => {
  const ctx = useContext(FileDataContext);
  const { state } = useLocation();
  console.log("success", ctx?.state.data);
  return (
    <>
      <BackButton text='Home' url='/home' />
      <Box className={classes.container}>
        <Box>
          <Typography className={classes.title}>
            {state.dataType} data successfully uploaded!
          </Typography>
          <Typography className={classes.subtitle}>
            Your {state.dataType} data has been successfully uploaded. Thank
            you!
          </Typography>
        </Box>
        <CheckCircle sx={{ fontSize: "10rem", color: "#33754f" }} />
        <FileInfo file={ctx?.state.data.file} />
      </Box>
    </>
  );
};

export default SuccessDataUpload;
