import React, { useEffect, useState } from "react";
import { AuthConstants } from "../constants/auth";
import { Typography } from "@mui/material";
import { useNavigate } from "react-router-dom";

const Welcome = () => {
  const [user, setUser] = useState<any>();
  const userStr = localStorage.getItem(AuthConstants.USER);
  const navigate = useNavigate();

  useEffect(() => {
    if (!userStr) {
      navigate("/login");
    } else {
      const obj = JSON.parse(userStr);
      setUser(obj);
    }
  }, [userStr]);
  return (
    <>
      {user && (
        <Typography color='#5e7f8d'>
          Welcome {user.first_name[0].toUpperCase() + user.first_name.slice(1)}!
        </Typography>
      )}
    </>
  );
};

export default Welcome;
