import React, { useContext } from "react";
import classes from "./SelectedFile.module.scss";
import { Box, Button, Divider, Icon, Typography } from "@mui/material";
import { FileDataContext } from "../../../store/fileData/FileDataProvider";
import { FontAwesomeIcon } from "@fortawesome/react-fontawesome";
import { faFileCsv } from "@fortawesome/free-solid-svg-icons";
import { useNavigate } from "react-router-dom";
import CSVIcon from "../CSVIcon";

const SelectedFile = () => {
  const navigate = useNavigate();
  const {
    dispatch,
    state: {
      data: { file },
    },
  } = useContext(FileDataContext);
  console.log("data", file);

  function onDelete() {
    dispatch({ type: "delete" });
  }
  return (
    <Box className={classes.container}>
      <Box className={classes.innerContainer}>
        <Typography fontWeight={"600"}>Selected file</Typography>
        <Button
          onClick={onDelete}
          color='info'
          className={classes.button}
          siz='small'>
          Change file
        </Button>
      </Box>
      <Divider />
      <Box className={classes.innerContainer}>
        <Box sx={{ display: "flex", gap: ".5rem", alignItems: "center" }}>
          <CSVIcon />
          <Typography>{file.name}</Typography>
        </Box>
        <Box>
          <Button
            color='info'
            onClick={() => navigate("list")}
            className={classes.button}
            siz='small'>
            Edit data
          </Button>
          <Button
            color='info'
            onClick={onDelete}
            className={classes.button}
            siz='small'>
            Delete File
          </Button>
        </Box>
      </Box>
    </Box>
  );
};

export default SelectedFile;
