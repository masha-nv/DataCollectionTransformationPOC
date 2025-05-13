import FileUpload from "./local-education-agency/FileUpload";
import "./App.css";
import { api } from "./api/api";
import { useState } from "react";
import React from "react";
import Table from "./local-education-agency/Table";

function App() {
  const [showLeaData, setShowLeaData] = useState<boolean>(false);

  return (
    <div className='app-container'>
      <h2>Data Collection Transformation | Proof-of-Concept – OME & MSIX</h2>
      <FileUpload />
      <br />
      <button onClick={() => setShowLeaData((pr) => !pr)}>
        {showLeaData ? "Hide LEA data" : "Show LEA data"}
      </button>

      {showLeaData && <Table />}
    </div>
  );
}

export default App;
