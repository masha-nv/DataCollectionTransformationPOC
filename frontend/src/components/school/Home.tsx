import React, { useState } from "react";
import { Box, Button } from "@mui/material";
import { useNavigate } from "react-router-dom";
import FileUpload from "./FileUpload";

const Home = () => {
  const navigate = useNavigate();
  return (
    <Box sx={{ width: "100%", maxWidth: 1100, margin: "2rem auto" }}>
      <FileUpload />
      <br />
      <Button onClick={() => navigate("list")}>View/Edit uploaded data</Button>
      <br />
      <Button onClick={() => navigate("/")}>Home</Button>
    </Box>
  );
};

export default Home;
