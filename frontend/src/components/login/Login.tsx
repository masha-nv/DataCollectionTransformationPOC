import { Box, Button, TextField, Typography } from "@mui/material";
import React, { useContext, useEffect, useReducer, useState } from "react";
import Divider from "../../shared/Divider";
import { api } from "../../api/api";
import { Link, useNavigate } from "react-router-dom";
import { AuthConstants } from "../../constants/auth";
import { isAuthenticated } from "../../api/utils";
import { GlobalStateContext } from "../../../store/GlobalStateProvider";
import { globalStateReducer, initialState } from "../../../store/globalState";
import classes from "./Login.module.scss";

const Login = () => {
  const ctx = useContext(GlobalStateContext);
  const [email, setEmail] = useState<string>();
  const [password, setPassword] = useState<string>();
  const navigate = useNavigate();
  const [passwordFiledType, setPasswordFieldType] = useState<
    "password" | "text"
  >("password");

  console.log("VITE_API_BASE_URL:", (import.meta as any).env.VITE_API_BASE_URL);

  useEffect(() => {
    if (ctx?.state.isLoggedIn) {
      navigate("/home");
    }
  }, [ctx, ctx?.state, ctx?.state.isLoggedIn]);

  async function handleLogin() {
    const res = await api.post("/login", { email, password });
    const data = res.data;
    if (data.access_token) {
      ctx?.dispatch({ type: "login", payload: data });
      navigate("/home");
    }
  }

  return (
    <Box className={classes.login}>
      <Box className={classes.container}>
        <Box className={classes.form}>
          <Typography className={classes.title}>
            MSIX DataFlow Sign In
          </Typography>
          <div>
            <TextField
              value={email}
              onChange={(e) => setEmail(e.target.value)}
              label='Email address'
              variant='outlined'
              sx={{ width: "100%", marginBottom: "2rem" }}
            />
            <TextField
              type={passwordFiledType}
              value={password}
              onChange={(e) => setPassword(e.target.value)}
              label='Password'
              variant='outlined'
              sx={{ width: "100%" }}
            />
            <Box className={classes.formButtons}>
              <Button
                onClick={() =>
                  setPasswordFieldType((prev) =>
                    prev === "password" ? "text" : "password"
                  )
                }
                type='button'
                style={{ textDecoration: "underline" }}
                color='secondary'>
                {passwordFiledType === "password"
                  ? "Show password"
                  : "Hide password"}
              </Button>
              <Button
                type='button'
                color='secondary'
                style={{ textDecoration: "underline" }}>
                Forgot password
              </Button>
            </Box>
          </div>
          <Button
            sx={{ width: "100%", height: "3rem" }}
            variant='contained'
            onClick={handleLogin}>
            Sign in
          </Button>
          <Button
            color='secondary'
            sx={{ width: "100%", height: "3rem" }}
            variant='outlined'>
            Login.gov
          </Button>
          <Button
            color='secondary'
            sx={{ width: "100%", height: "3rem" }}
            variant='outlined'>
            Department of Education Account
          </Button>
        </Box>
        <Box className={classes.noAccount}>
          <Typography>
            Dont't have an account?{" "}
            <Link className={classes.link} to='#'>
              Create your account now.
            </Link>
          </Typography>
        </Box>
      </Box>
    </Box>
  );
};

export default Login;
