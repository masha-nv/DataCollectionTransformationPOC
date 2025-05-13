import React, { useEffect, useState } from "react";
import { api } from "../api/api";

const FileUpload = () => {
  const [leaUploadResponse, setLeaUploadResponse] = useState<{
    inserted: number;
    updated: number;
    invalid_rows: any[];
  }>();
  const [file, setFile] = useState<File | null>(null);

  useEffect(() => {
    if (file) {
      handleUploadFile(file);
    }
  }, [file]);

  async function handleUploadFile(file: File) {
    if (!file) return;
    const formData = new FormData();
    formData.append("file", file);
    const response = await api.post("/upload-lea-data", formData, {
      headers: { "Content-Type": "multipart/form-data" },
    });
    setLeaUploadResponse(response.data);
    setFile(null);
  }

  function onFileChange(e) {
    const file = e.target.files[0];
    if (file) {
      setFile(file);
    } else {
      setFile(null);
    }
  }

  return (
    <div>
      <label htmlFor='lea'>Upload LEA file for ingestion</label>
      <br />
      <input type='file' id='upload-lea' onChange={onFileChange} />
      <br />
      <br />
      {leaUploadResponse && (
        <div>
          <span>
            Inserted: <strong>{leaUploadResponse.inserted} records </strong>
          </span>
          <br />
          <span>
            Updated: <strong>{leaUploadResponse.updated} records</strong>
          </span>
        </div>
      )}
    </div>
  );
};

export default FileUpload;
