import React, { useEffect, useRef, useState } from "react";
import { api } from "../api/api";
import { Typography, Box, Button, IconButton } from "@mui/material";
import CloudUploadIcon from "@mui/icons-material/CloudUpload";
import { styled } from "@mui/material/styles";
import AttachFileIcon from "@mui/icons-material/AttachFile";

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

const FileUpload = ({ postUrl }: { postUrl: string }) => {
  const [leaUploadResponse, setLeaUploadResponse] = useState<{
    inserted: number;
    message: string;
  }>();
  const [file, setFile] = useState<File | null>(null);
  const fileInputRef = useRef<HTMLInputElement>(null);

  useEffect(() => {
    if (file) {
      handleUploadFile(file);
    }
  }, [file]);

  async function handleUploadFile(file: File) {
    if (!file) return;
    const formData = new FormData();
    formData.append("file", file);
    const response = await api.post(postUrl, formData, {
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
      <Box
        onClick={() => fileInputRef.current?.click()}
        sx={{
          border: "1px dashed #5e7f8d",
          width: "30rem",
          height: "10rem",
          display: "flex",
          justifyContent: "center",
          flexDirection: "column",
          cursor: "pointer",
        }}>
        <Box
          sx={{
            display: "flex",
            justifyContent: "center",
            flexDirection: "column",
            alignContent: "center",
            alignItems: "center",
          }}>
          <AttachFileIcon sx={{ rotate: "45deg" }} />
          <Typography color='#5e7f8d'>Add an attachment (optional)</Typography>
          <VisuallyHiddenInput
            ref={fileInputRef}
            type='file'
            onChange={onFileChange}
          />
        </Box>
      </Box>

      {leaUploadResponse && (
        <div>
          <span>
            <strong>{leaUploadResponse.message} </strong>
          </span>
        </div>
      )}
    </Box>
  );
};

export default FileUpload;
