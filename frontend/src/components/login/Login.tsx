import { Box, Button, TextField, Typography } from "@mui/material";
import React, { useState } from "react";
import Divider from "../../shared/Divider";
import { api } from "../../api/api";

const Login = () => {
  const [email, setEmail] = useState<string>();
  const [password, setPassword] = useState<string>();

  async function handleLogin() {
    const res = await api.post("/login", { email, password });
    const data = res.data;
    if (data.access_token) {
      localStorage.setItem("access_token", data.access_token);
    }
  }

  return (
    <Box
      sx={{
        display: "flex",
        flexDirection: "column",
        alignItems: "center",
        width: "100%",
        justifyContent: "center",
      }}>
      <Box
        sx={{
          display: "flex",
          flexDirection: "column",
          alignItems: "center",
          marginTop: "4rem",
        }}>
        <Typography variant='h4' color='#102f3c' fontWeight={"bold"}>
          ED DataFlow User Login
        </Typography>
        <Divider />
      </Box>
      <Box
        sx={{
          display: "flex",
          flexDirection: "column",
          alignItems: "center",
          width: "50%",
          marginTop: "4rem",
          gap: "1rem",
        }}>
        <TextField
          value={email}
          onChange={(e) => setEmail(e.target.value)}
          label='Username'
          variant='outlined'
          sx={{ width: "100%" }}
        />
        <TextField
          type='password'
          value={password}
          onChange={(e) => setPassword(e.target.value)}
          label='Password'
          variant='outlined'
          sx={{ width: "100%" }}
        />
        <Button
          sx={{ width: "100%", marginTop: "1rem" }}
          variant='contained'
          onClick={handleLogin}>
          Log In
        </Button>
        <Divider />
      </Box>
    </Box>
  );
};

export default Login;
