import React, { useState } from "react";
import Typography from "@mui/material/Typography";
import { Box, Button } from "@mui/material";
import FileUpload from "../local-education-agency/FileUpload";
import { useNavigate } from "react-router-dom";
import Header from "../../shared/Header";

const Home = () => {
  const navigate = useNavigate();
  return (
    <Box sx={{ width: "100%", maxWidth: 1100, margin: "2rem auto" }}>
      <Header subtitle='Local Education Agency' />
      <FileUpload />
      <br />
      <Button onClick={() => navigate("list")}>View/Edit uploaded data</Button>
      <br />
      <Button onClick={() => navigate("/")}>Home</Button>
    </Box>
  );
};

export default Home;
