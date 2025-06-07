import React, { useContext, useEffect, useRef, useState } from "react";
import { api } from "../../api/api";
import { Typography, Box, Button, IconButton } from "@mui/material";
import CloudUploadIcon from "@mui/icons-material/CloudUpload";
import { styled } from "@mui/material/styles";
import AttachFileIcon from "@mui/icons-material/AttachFile";
import ShareIcon from "@mui/icons-material/Share";
import UploadIcon from "@mui/icons-material/Upload";
import classes from "./FileUpload.module.scss";
import { FileDataContext } from "../../../store/fileData/FileDataProvider";

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
  const cxt = useContext(FileDataContext);
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
    const { preview_data, file_id, file_name, size } = response.data;
    cxt?.dispatch({
      type: "upload",
      payload: {
        data: preview_data,
        fileName: file_name,
        fileSize: size,
        fileId: file_id,
        file: file,
      },
    });
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
    <Box>
      <Box
        onClick={() => fileInputRef.current?.click()}
        className={classes.container}>
        <Box className={classes.innerContainer}>
          <UploadIcon sx={{ fontSize: "3rem" }} />
          <Typography>Add an attachment (optional)</Typography>
          <VisuallyHiddenInput
            ref={fileInputRef}
            type='file'
            onChange={onFileChange}
          />
        </Box>
      </Box>
    </Box>
  );
};

export default FileUpload;
