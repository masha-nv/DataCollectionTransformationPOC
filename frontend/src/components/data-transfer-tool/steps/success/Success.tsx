import { Box, Typography } from "@mui/material";
import React, { useContext } from "react";
import classes from "./Success.module.scss";
import BackButton from "../../../../shared/back-button/BackButton";
import { useLocation } from "react-router-dom";
import CheckCircle from "@mui/icons-material/CheckCircleOutline";
import { FileDataContext } from "../../../store/fileData/FileDataProvider";
import FileInfo from "../../../../shared/file-info/FileInfo";
import { DataTransferContext } from "../../../../../store/dataTransfer/DataTransferProvider";

const Success = () => {
  const ctx = useContext(DataTransferContext);
  const allFiles = ctx?.state.files;
  const selectedFileIds = ctx?.state.selectedFiles;
  const files = allFiles?.filter((f) => selectedFileIds?.has(f.id));

  return (
    <>
      <BackButton text='Home' url='/home' />
      <Box className={classes.container}>
        <Box>
          <Typography className={classes.title}>
            Data successfully transferred!
          </Typography>
          <Typography className={classes.subtitle}>
            You data has been successfully uploaded. Thank you!
          </Typography>
        </Box>
        <CheckCircle sx={{ fontSize: "10rem", color: "#33754f" }} />
        <Box sx={{ display: "flex", flexDirection: "row", gap: "1rem" }}>
          {files?.map((f) => (
            <FileInfo key={f} file={f} />
          ))}
        </Box>
      </Box>
    </>
  );
};

export default Success;
