import { Card, CardContent, Typography } from "@mui/material";
import React from "react";
import { Link } from "react-router-dom";
import classes from "./Welcome.module.scss";

const Welcome = () => {
  return (
    <Card sx={{ width: "100%" }}>
      <CardContent sx={{ paddingLeft: "2rem" }}>
        <Typography className={classes.title}>
          Welcome to MSIX DataFlow
        </Typography>
        <ul style={{ marginLeft: "1.5rem" }}>
          <li>
            If you are need of assistance for date upload contact{" "}
            <Link to='#'>support</Link>.
          </li>
          <li>
            Down our templates for data upload <Link to='#'>here</Link>.
          </li>
          <li>
            Learn more about the <Link to='#'>purpose of each tool</Link> and{" "}
            <Link to='#'>how the data is used</Link>.
          </li>
        </ul>
      </CardContent>
    </Card>
  );
};

export default Welcome;
