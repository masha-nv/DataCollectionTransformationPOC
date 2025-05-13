import React, { useState } from "react";
import { api } from "../../api/api";
import Table from "../local-education-agency/Table";
import Typography from "@mui/material/Typography";
import { Box, Button } from "@mui/material";
import FileUpload from "../local-education-agency/FileUpload";
import { Link, useNavigate } from "react-router-dom";

function LinkComponent() {
  return <Link to={"lea-list"}></Link>;
}

const Home = () => {
  const navigate = useNavigate();
  return (
    <Box sx={{ width: "100%", maxWidth: 1100, margin: "2rem auto" }}>
      <Typography variant='h4' gutterBottom>
        Data Collection Transformation | Proof-of-Concept – OME & MSIX
      </Typography>
      <FileUpload />
      <br />
      <Button onClick={() => navigate("lea-list")}>
        Go To Local Education Agency Report
      </Button>
    </Box>
  );
};

export default Home;
