import "./App.css";
import { Routes, Route } from "react-router-dom";
import Home from "./components/home/Home";
import React from "react";
import Table from "./components/local-education-agency/Table";

function App() {
  return (
    <Routes>
      <Route path='/' element={<Home />} />
      <Route path='lea-list' element={<Table />} />
    </Routes>
  );
}

export default App;
