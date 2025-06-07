import "./App.css";
import { Routes, Route, Navigate, Outlet } from "react-router-dom";
import Home from "./components/home/Home";
import React from "react";
import LEATable from "./components/local-education-agency/table/Table";
import LEAHome from "./components/local-education-agency/home/Home";
import SchoolHome from "./components/school/Home";
import SchoolTable from "./components/school/SchoolTable";
import FileTable from "./components/file/FileTable";
import Login from "./components/login/Login";
import { ThemeProvider } from "@mui/material";
import theme from "./styles/theme";
import Layout from "./shared/layout/Layout";
import { isAuthenticated } from "./api/utils";
import { FileDataProvider } from "../store/fileData/FileDataProvider";
import SuccessDataUpload from "./shared/success-data-upload/SuccessDataUpload";
import DataTransferTool from "./components/data-transfer-tool/DataTransferTool";
import { DataTransferProvider } from "../store/dataTransfer/DataTransferProvider";
import Success from "./components/data-transfer-tool/steps/success/Success";

function ProtectedRoute({ children }) {
  if (!isAuthenticated()) {
    return <Navigate to='/login' replace />;
  }
  return <> {children}</>;
}

function ProtectedLayout() {
  return (
    <ProtectedRoute>
      <FileDataProvider>
        <Outlet />
      </FileDataProvider>
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
            <Route path='/home' element={<Home />} />
            <Route path='/lea' element={<LEAHome />} />
            <Route path='/lea/list' element={<LEATable />} />
            <Route path='/school' element={<SchoolHome />} />
            <Route path='/school/list' element={<SchoolTable />} />
            <Route path='/file' element={<FileTable />} />
            <Route path='/success' element={<SuccessDataUpload />} />
            <Route path='/data-transfer-tool/success' element={<Success />} />

            <Route
              path='/data-transfer-tool'
              element={
                <DataTransferProvider>
                  <DataTransferTool />
                </DataTransferProvider>
              }
            />
          </Route>
        </Route>
      </Routes>
    </ThemeProvider>
  );
}

export default App;
