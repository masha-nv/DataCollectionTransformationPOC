import { Button } from "@mui/material";
import React from "react";
import { useNavigate } from "react-router-dom";
import classes from "./BackButton.module.scss";
import ArrowBack from "@mui/icons-material/ArrowBack";

type Props = {
  text: string;
  url: string;
};
const BackButton = ({ text, url }: Props) => {
  const navigate = useNavigate();
  return (
    <Button
      sx={{ borderRadius: "1rem" }}
      startIcon={<ArrowBack />}
      variant='outlined'
      color='secondary'
      className={classes.button}
      onClick={() => navigate(url)}>
      {text}
    </Button>
  );
};

export default BackButton;
