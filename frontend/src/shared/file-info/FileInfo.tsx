import { Box, Typography } from "@mui/material";
import React, { useContext, useEffect } from "react";
import { FileDataContext } from "../../../store/fileData/FileDataProvider";
import classes from "./FileInfo.module.scss";
import { faFileCsv } from "@fortawesome/free-solid-svg-icons";
import { FontAwesomeIcon } from "@fortawesome/react-fontawesome";
import { formatFileSize } from "../../utils/formatFileSize";
import { useNavigate } from "react-router-dom";
import CSVIcon from "../CSVIcon";

const FileInfo = ({ file }) => {
  const navigate = useNavigate();
  useEffect(() => {
    if (!file) {
      navigate("..");
    }
  }, [file]);

  return (
    <Box sx={{ width: "60%" }}>
      <Box className={classes.container}>
        <Box sx={{ display: "flex", gap: ".5rem", alignItems: "center" }}>
          <CSVIcon />
          <Typography>{file.name}</Typography>
        </Box>
        <Typography>{formatFileSize(file.size)}</Typography>
      </Box>
    </Box>
  );
};

export default FileInfo;
