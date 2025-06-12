import React from "react";
import Card from "@mui/material/Card";
import CardActions from "@mui/material/CardActions";
import CardContent from "@mui/material/CardContent";
import { Box, Button, Typography } from "@mui/material";
import classes from "./DataFlowTools.module.scss";
import { useNavigate } from "react-router-dom";

const DataFlowTools = () => {
  const navigate = useNavigate();
  return (
    <Box>
      <Typography className={classes.title}>MSIX DataFlow Tools</Typography>
      <Box className={classes.cardContainer}>
        <Card sx={{ paddingLeft: "2rem" }}>
          <CardContent>
            <Typography className={classes.cardTitle}>
              Local Education Agency (LEA) Data Upload Tool
            </Typography>
            <Typography>
              A secure online platform for school districts and similar agencies
              to upload, validate, and submit required educational data to state
              or federal education authorities.
            </Typography>
            <Typography sx={{ fontWeight: "bolder", marginY: "1rem" }}>
              Who this is for:
            </Typography>
            <ul style={{ marginLeft: "1.5rem" }}>
              <li>State Education Agencies</li>
              <li>Education Data Coordinators</li>
            </ul>
            <CardActions sx={{ marginTop: "3rem" }}>
              <Button
                onClick={() => navigate("/lea")}
                color='primary'
                variant='contained'>
                Go to LEA data upload tool
              </Button>
              <Button variant='outlined'>Edit data model</Button>
            </CardActions>
          </CardContent>
        </Card>
        <Card sx={{ paddingLeft: "2rem" }}>
          <CardContent>
            <Typography className={classes.cardTitle}>
              School Data Upload Tool
            </Typography>
            <Typography>
              A secure online platform for school districts and similar agencies
              to upload, validate, and submit required educational data to state
              or federal education authorities.
            </Typography>
            <Typography sx={{ fontWeight: "bolder", marginY: "1rem" }}>
              Who this is for:
            </Typography>
            <ul style={{ marginLeft: "1.5rem" }}>
              <li>State Education Agencies</li>
              <li>Education Data Coordinators</li>
            </ul>
            <CardActions sx={{ marginTop: "3rem" }}>
              <Button color='primary' variant='contained'>
                Go to school data upload tool
              </Button>
              <Button variant='outlined'>Edit data model</Button>
            </CardActions>
          </CardContent>
        </Card>
        <Card sx={{ paddingLeft: "2rem" }}>
          <CardContent>
            <Typography className={classes.cardTitle}>
              Data Transfer Tool
            </Typography>
            <Typography>
              A secure online platform for school districts and similar agencies
              to upload, validate, and submit required educational data to state
              or federal education authorities.
            </Typography>
            <Typography sx={{ fontWeight: "bolder", marginY: "1rem" }}>
              Who this is for:
            </Typography>
            <ul style={{ marginLeft: "1.5rem" }}>
              <li>MSIX System Administrator</li>
            </ul>
            <CardActions sx={{ marginTop: "3rem" }}>
              <Button
                color='primary'
                variant='contained'
                onClick={() => navigate("/data-transfer-tool")}>
                Go to transfer tool
              </Button>
              <Button variant='outlined'>Add more</Button>
            </CardActions>
          </CardContent>
        </Card>
      </Box>
    </Box>
  );
};

export default DataFlowTools;
