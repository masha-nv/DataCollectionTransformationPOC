import "./App.css";
import { Routes, Route } from "react-router-dom";
import Home from "./components/home/Home";
import React from "react";
import LEATable from "./components/local-education-agency/LEATable";
import LEAHome from "./components/local-education-agency/Home";
import SchoolHome from "./components/school/Home";
import SchoolTable from "./components/school/SchoolTable";
import FileTable from "./components/file/FileTable";

function App() {
  return (
    <Routes>
      <Route path='/' element={<Home />} />
      <Route path='/lea' element={<LEAHome />} />
      <Route path='/lea/list' element={<LEATable />} />
      <Route path='/school' element={<SchoolHome />} />
      <Route path='/school/list' element={<SchoolTable />} />
      <Route path='/file' element={<FileTable />} />
    </Routes>
  );
}

export default App;
