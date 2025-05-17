import "./App.css";
import { Routes, Route, Navigate, Outlet } from "react-router-dom";
import Home from "./components/home/Home";
import React from "react";
import LEATable from "./components/local-education-agency/LEATable";
import LEAHome from "./components/local-education-agency/Home";
import SchoolHome from "./components/school/Home";
import SchoolTable from "./components/school/SchoolTable";
import FileTable from "./components/file/FileTable";
import Login from "./components/login/login";
import { ThemeProvider } from "@mui/material";
import theme from "./styles/theme";
import Layout from "./shared/Layout";
import { isAuthenticated } from "./api/utils";

function ProtectedRoute({ children }) {
  if (!isAuthenticated()) {
    return <Navigate to='/login' replace />;
  }
  return <> {children}</>;
}

function ProtectedLayout() {
  return (
    <ProtectedRoute>
      <Outlet />
    </ProtectedRoute>
  );
}

function App() {
  return (
    <ThemeProvider theme={theme}>
      <Routes>
        <Route path='/' element={<Layout />}>
          <Route path='/' element={<Navigate to='/login' />} />
          <Route path='/login' element={<Login />} />
          <Route element={<ProtectedLayout />}>
            <Route path='/lea' element={<LEAHome />} />
            <Route path='/lea/list' element={<LEATable />} />
            <Route path='/school' element={<SchoolHome />} />
            <Route path='/school/list' element={<SchoolTable />} />
            <Route path='/file' element={<FileTable />} />
          </Route>
        </Route>
      </Routes>
    </ThemeProvider>
  );
}

export default App;
