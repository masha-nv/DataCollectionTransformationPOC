import { Box, Card, CardActions, CardContent, Typography } from "@mui/material";
import React from "react";
import classes from "./ChooseDataSource.module.scss";
import Instructions from "../../../../shared/instructions/Instructions";
import FileTable from "../../../file/FileTable";

const ChooseDataSource = () => {
  return (
    <Box sx={{ display: "flex", flexDirection: "column", gap: "2rem" }}>
      <Instructions />
      <FileTable />
    </Box>
  );
};

export default ChooseDataSource;
