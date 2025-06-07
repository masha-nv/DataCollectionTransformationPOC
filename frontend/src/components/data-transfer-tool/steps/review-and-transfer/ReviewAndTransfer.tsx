import { Box, Typography } from "@mui/material";
import React, { useContext } from "react";
import { DataTransferContext } from "../../../../../store/dataTransfer/DataTransferProvider";
import FileInfo from "../../../../shared/file-info/FileInfo";
import { api } from "../../../../api/api";

const ReviewAndTransfer = () => {
  const ctx = useContext(DataTransferContext);
  const allFiles = ctx?.state.files;
  const selectedFileIds = ctx?.state.selectedFiles;
  const files = allFiles?.filter((f) => selectedFileIds?.has(f.id));

  return (
    <Box>
      <Box
        sx={{
          display: "flex",
          flexDirection: "column",
          gap: "1rem",
          marginBottom: "2rem",
        }}>
        <Typography>Transfer location</Typography>
        <Typography sx={{ opacity: ".7" }}>
          {ctx?.state.transferLocation.displayText}
        </Typography>
      </Box>
      <Box sx={{ marginTop: "2rem" }}>
        <Typography gutterBottom>Files</Typography>
        <Box sx={{ display: "flex", flexDirection: "column", gap: "1rem" }}>
          {files?.map((f) => (
            <FileInfo key={f} file={f} />
          ))}
        </Box>
      </Box>
    </Box>
  );
};

export default ReviewAndTransfer;
