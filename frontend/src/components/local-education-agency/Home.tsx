import React, { useState } from "react";
import { Box, Button, Typography } from "@mui/material";
import FileUpload from "../../shared/FileUpload";
import { useNavigate } from "react-router-dom";
import Welcome from "../../shared/Welcome";

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
        LEA Data Upload Portal
      </Typography>
      <Welcome />
      <FileUpload postUrl='/lea' />
      <br />
      <Button onClick={() => navigate("list")}>View/Edit uploaded data</Button>
      <br />
    </Box>
  );
};

export default Home;
