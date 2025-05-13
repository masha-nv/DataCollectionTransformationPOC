import React, { useEffect, useState } from "react";
import { api } from "../../api/api";
import { Typography, Box, Button } from "@mui/material";
import CloudUploadIcon from "@mui/icons-material/CloudUpload";
import { styled } from "@mui/material/styles";

const VisuallyHiddenInput = styled("input")({
  clip: "rect(0 0 0 0)",
  clipPath: "inset(50%)",
  height: 1,
  overflow: "hidden",
  position: "absolute",
  bottom: 0,
  left: 0,
  whiteSpace: "nowrap",
  width: 1,
});

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
    <Box sx={{ marginTop: 10 }}>
      <Button
        component='label'
        role={undefined}
        variant='contained'
        tabIndex={-1}
        startIcon={<CloudUploadIcon />}>
        Upload LEA files
        <VisuallyHiddenInput type='file' onChange={onFileChange} />
      </Button>

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
    </Box>
  );
};

export default FileUpload;
