import React, { useContext, useEffect, useState } from "react";
import { Box, Button, Typography } from "@mui/material";
import { useNavigate } from "react-router-dom";
import classes from "./Home.module.scss";
import FileUpload from "../../../shared/file-upload/FileUpload";
import BackButton from "../../../shared/back-button/BackButton";
import { FileDataContext } from "../../../../store/fileData/FileDataProvider";
import SelectedFile from "../../../shared/selected-file/SelectedFile";
import { api } from "../../../api/api";
import Instructions from "../../../shared/instructions/Instructions";

const Home = () => {
  const navigate = useNavigate();
  const cxt = useContext(FileDataContext);
  const [fileData, setFileData] = useState<any>();

  useEffect(() => {
    console.log(cxt?.state);
    if (cxt && cxt.state && Object.keys(cxt.state.data).length > 0) {
      setFileData(cxt.state.data);
    } else {
      setFileData(null);
    }
  }, [cxt, cxt?.state]);

  async function handleUpload() {
    const file = fileData.file;
    console.log("file is", file);
    if (!file) return;
    const formData = new FormData();
    formData.append("file", file);
    try {
      const response = await api.post("/lea", formData, {
        headers: { "Content-Type": "multipart/form-data" },
      });
      console.log("response from lea file upload", response);
      if (response.status === 200) {
        navigate("/success", { state: { dataType: "LEA" } });
      } else {
        throw new Error("something went wrong and lea file was not uploaded");
      }
    } catch {
      console.log("something went wrong and lea file was not uploaded");
    }
  }

  return (
    <>
      <BackButton text='Home' url='/home' />
      <Box className={classes.container}>
        <Box className={classes.innerContainer}>
          <Typography className={classes.title}>
            Local Education Agency (LEA) Data Upload Tool
          </Typography>
          <Instructions />
          <Box sx={{ marginTop: "4rem" }}>
            {!!fileData ? <SelectedFile /> : <FileUpload postUrl='/preview' />}
          </Box>
          <Box className={classes.actionButtons}>
            <Button
              variant='outlined'
              color='secondary'
              onClick={() => navigate("/home")}>
              Exit tool
            </Button>

            <Button
              variant='contained'
              color='primary'
              onClick={handleUpload}
              disabled={!fileData}>
              Continue
            </Button>
          </Box>
        </Box>
      </Box>
    </>
  );
};

export default Home;
