import React, { useState } from "react";
import { api } from "../../api/api";
import Typography from "@mui/material/Typography";
import { Box, Button } from "@mui/material";
import { Link, useNavigate } from "react-router-dom";
import Card from "@mui/material/Card";
import CardActions from "@mui/material/CardActions";
import CardContent from "@mui/material/CardContent";
import classes from "./Home.module.scss";
import Welcome from "./welcome/Welcome";
import DataFlowTools from "./data-flow-tools/DataFlowTools";
import FAQ from "./faq/FAQ";

const Home = () => {
  const navigate = useNavigate();
  return (
    <Box className={classes.container}>
      <Box className={classes.innerContainer}>
        <Welcome />
        <DataFlowTools />
        <FAQ />
      </Box>
    </Box>
  );
};

export default Home;
