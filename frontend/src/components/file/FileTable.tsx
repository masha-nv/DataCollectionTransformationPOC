import * as React from "react";
import {
  DataGrid,
  GridColDef,
  GridDeleteIcon,
  GridRowId,
  GridRowParams,
} from "@mui/x-data-grid";
import Paper from "@mui/material/Paper";
import { api } from "../../api/api";
import { Box, Button, IconButton, Tooltip, Typography } from "@mui/material";
import { useNavigate } from "react-router-dom";
import Toolbar from "@mui/material/Toolbar";

const columns: GridColDef[] = [
  { field: "upload_datetime", headerName: "Upload Date/Time", width: 500 },
  { field: "model_type", headerName: "Entity", width: 500 },
];

const paginationModel = { page: 0, pageSize: 5 };

export default function DataTable() {
  const navigate = useNavigate();
  const [pageLimit, setPageLimit] = React.useState<number>(5);
  async function getFiles() {
    const response = await api.get(`/files?limit=${pageLimit}`);
    setData(response.data);
  }

  React.useEffect(() => {
    getFiles();
  }, [pageLimit]);

  const [data, setData] = React.useState<any[]>();
  const [selected, setSelected] = React.useState<GridRowParams | null>(null);

  function handleNavigateToReport() {
    if (!selected) return;
    const path =
      selected?.row.model_type === "LEA"
        ? `/lea/list?fileId=${selected.id}`
        : `/school/list?fileId=${selected.id}`;
    navigate(path);
  }

  return (
    <Box sx={{ width: "100%", maxWidth: 1100, margin: "2rem auto" }}>
      <Typography variant='h4' gutterBottom>
        Local Education Agency{" "}
      </Typography>
      <Paper sx={{ height: "fit-content", width: "100%" }}>
        <DataGrid
          showToolbar={false}
          getRowId={(row) => row["id"]}
          rows={data}
          columns={columns}
          onRowClick={(e) => {
            console.log(e);
            setSelected(e);
          }}
          initialState={{ pagination: { paginationModel } }}
          pageSizeOptions={[5, 10]}
          checkboxSelection={false}
          onPaginationModelChange={(e) => {
            setPageLimit(e.pageSize);
          }}
          sx={{ border: 0 }}
        />
      </Paper>
      {selected && (
        <Button onClick={handleNavigateToReport}>View Report</Button>
      )}
      <br />
      <Button onClick={() => navigate("/")}>Home</Button>
    </Box>
  );
}
