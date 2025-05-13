import * as React from "react";
import {
  DataGrid,
  GridColDef,
  GridDeleteIcon,
  GridRowId,
} from "@mui/x-data-grid";
import Paper from "@mui/material/Paper";
import { api } from "../../api/api";
import { Box, Button, IconButton, Tooltip, Typography } from "@mui/material";
import { useNavigate } from "react-router-dom";
import Toolbar from "@mui/material/Toolbar";

const columns: GridColDef[] = [
  { field: "LEANAME", headerName: "Name", width: 300 },
  { field: "LOC_CITY", headerName: "City", width: 200 },
  { field: "LOC_STATE", headerName: "State", width: 200 },
  { field: "OPERATIONSTATUS", headerName: "Operation Status", width: 200 },
  {
    field: "LOC_ZIPCODE",
    headerName: "ZipCode",
    width: 200,
  },
  {
    field: "SCHOOLYEAR",
    headerName: "School Year",
    width: 200,
  },
];

const paginationModel = { page: 0, pageSize: 5 };

export default function DataTable() {
  const navigate = useNavigate();
  const [pageLimit, setPageLimit] = React.useState<number>(5);
  async function handleGetAllleasData() {
    const response = await api.get(`/lea-data?limit=${pageLimit}`);
    setData(response.data);
  }
  async function handleDeleteButtonClick() {
    if (!selectedIds) return;
    const response = await api.delete(`/lea-records`, {
      data: [...selectedIds],
    });
    if (response.status == 200) {
      await handleGetAllleasData();
    }
  }

  React.useEffect(() => {
    handleGetAllleasData();
  }, [pageLimit]);

  const [data, setData] = React.useState<any[]>();
  const [selectedIds, setSelectedIds] = React.useState<Set<GridRowId> | null>(
    null
  );

  return (
    <Box sx={{ width: "100%", maxWidth: 1100, margin: "2rem auto" }}>
      <Typography variant='h4' gutterBottom>
        Local Education Agency{" "}
      </Typography>
      <Paper sx={{ height: "fit-content", width: "100%" }}>
        <Toolbar>
          {!!selectedIds?.size && (
            <>
              <Typography
                sx={{ flex: "1 1 100%" }}
                color='inherit'
                variant='subtitle1'
                component='div'>
                {selectedIds.size} selected
              </Typography>
              <Tooltip title='Delete'>
                <IconButton onClick={handleDeleteButtonClick}>
                  <GridDeleteIcon />
                </IconButton>
              </Tooltip>
            </>
          )}
        </Toolbar>
        <DataGrid
          showToolbar={false}
          getRowId={(row) => row["DistrictNCESID"]}
          rows={data}
          columns={columns}
          initialState={{ pagination: { paginationModel } }}
          pageSizeOptions={[5, 10, 20, 30, 40, 50, 60, 70, 80, 90, 100]}
          checkboxSelection
          onRowSelectionModelChange={(e) => {
            console.log("row selection", e);
            setSelectedIds(e.ids);
          }}
          onPaginationModelChange={(e) => {
            setPageLimit(e.pageSize);
          }}
          sx={{ border: 0 }}
        />
      </Paper>

      <Button onClick={() => navigate("/")}>Home</Button>
    </Box>
  );
}
