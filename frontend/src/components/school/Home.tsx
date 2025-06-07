import React, { useState } from "react";
import Typography from "@mui/material/Typography";
import { Box, Button } from "@mui/material";
import { useNavigate } from "react-router-dom";
import Header from "../../shared/header/Header";
import Welcome from "../../shared/Welcome";
import FileUpload from "../../shared/file-upload/FileUpload";

const Home = () => {
  const navigate = useNavigate();
  return (
    <Box
      sx={{
        width: "100%",
        maxWidth: 1100,
        margin: "10rem auto",
        display: "flex",
        flexDirection: "column",
        alignContent: "center",
        alignItems: "center",
      }}>
      <Typography variant='h5' color='#102f3c' marginBottom={".5rem"}>
        School Data Upload Portal
      </Typography>
      <Welcome />
      <FileUpload postUrl='/school' />
      <br />
      <Button onClick={() => navigate("list")}>View/Edit uploaded data</Button>
    </Box>
  );
};

export default Home;
