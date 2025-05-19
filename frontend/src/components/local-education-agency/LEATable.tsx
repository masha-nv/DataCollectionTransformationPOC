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
import { useNavigate, useSearchParams } from "react-router-dom";
import Toolbar from "@mui/material/Toolbar";

const columns: GridColDef[] = [
  { field: "lea_name", headerName: "Name", width: 300 },
  { field: "loc_city", headerName: "City", width: 200 },
  { field: "loc_state", headerName: "State", width: 200 },
  { field: "operational_status", headerName: "Operation Status", width: 200 },
  {
    field: "loc_zipcode",
    headerName: "ZipCode",
    width: 200,
  },
  {
    field: "school_year",
    headerName: "School Year",
    width: 200,
  },
];

const paginationModel = { page: 0, pageSize: 5 };

export default function LEATable() {
  const navigate = useNavigate();
  const [pageLimit, setPageLimit] = React.useState<number>(5);
  const [searchParams] = useSearchParams();

  async function getData() {
    const fileId = searchParams.get("fileId");
    const endpoint = fileId
      ? `/lea/${fileId}?limit=${pageLimit}`
      : `/lea?limit=${pageLimit}`;
    const response = await api.get(endpoint);
    setData(response.data);
  }
  async function handleDeleteButtonClick() {
    if (!selectedIds) return;
    const response = await api.delete(`/lea`, {
      data: [...selectedIds],
    });
    if (response.status == 200) {
      await getData();
    }
  }

  React.useEffect(() => {
    getData();
  }, [pageLimit]);

  const [data, setData] = React.useState<any[]>();
  const [selectedIds, setSelectedIds] = React.useState<Set<GridRowId> | null>(
    null
  );

  return (
    <Box sx={{ width: "100%", maxWidth: 1100, margin: "2rem auto" }}>
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
          getRowId={(row) => row["district_nces_id"]}
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

      <Button onClick={() => navigate("/lea")}>Back</Button>
    </Box>
  );
}
