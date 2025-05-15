import React, { useState } from "react";
import { api } from "../../api/api";
import Typography from "@mui/material/Typography";
import { Box, Button } from "@mui/material";
import FileUpload from "../local-education-agency/FileUpload";
import { Link, useNavigate } from "react-router-dom";
import Header from "../../shared/Header";

function LinkComponent() {
  return <Link to={"lea-list"}></Link>;
}

const Home = () => {
  const navigate = useNavigate();
  return (
    <Box sx={{ width: "100%", maxWidth: 1100, margin: "2rem auto" }}>
      <Header />
      <Button onClick={() => navigate("lea")}>
        Handle LEA Files / Access Reports
      </Button>
      <br />
      <Button onClick={() => navigate("school")}>
        Handle School Files / Access Reports
      </Button>
      <br />
      <Button onClick={() => navigate("file")}>View all files</Button>
    </Box>
  );
};

export default Home;
