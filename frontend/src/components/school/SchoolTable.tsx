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
import Header from "../../shared/Header";

const columns: GridColDef[] = [
  { field: "education_entity_name", headerName: "Name", width: 300 },
  { field: "city", headerName: "City", width: 200 },
  { field: "state", headerName: "State", width: 200 },
  {
    field: "school_operational_status",
    headerName: "School Operation Status",
    width: 200,
  },
  {
    field: "zip_code",
    headerName: "ZipCode",
    width: 200,
  },
  {
    field: "school_year",
    headerName: "School Year",
    width: 200,
  },
];

export default function LEATable() {
  const navigate = useNavigate();
  const [searchParams] = useSearchParams();
  const [total, setTotal] = React.useState<number>(0);
  const [paginationModel, setPaginationModel] = React.useState<{
    page: number;
    pageSize: number;
  }>({ page: 0, pageSize: 5 });

  async function getData() {
    const fileId = searchParams.get("fileId");
    const endpoint = fileId
      ? `/school/${fileId}?limit=${paginationModel.pageSize}&offset=${paginationModel.page}`
      : `/school?limit=${paginationModel.pageSize}&offset=${paginationModel.page}`;
    const {
      data: { items, total },
    } = await api.get(endpoint);
    setData(items);
    setTotal(total);
  }
  async function handleDeleteButtonClick() {
    if (!selectedIds) return;
    const response = await api.delete(`/school`, {
      data: [...selectedIds],
    });
    if (response.status == 200) {
      await getData();
    }
  }

  React.useEffect(() => {
    getData();
  }, [paginationModel]);

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
          paginationMode='server'
          showToolbar={false}
          getRowId={(row) => row["id"]}
          rows={data}
          rowCount={total}
          columns={columns}
          initialState={{ pagination: { paginationModel } }}
          pageSizeOptions={[5, 10, 25, 50, 100]}
          checkboxSelection
          onRowSelectionModelChange={(e) => {
            console.log("row selection", e);
            setSelectedIds(e.ids);
          }}
          onPaginationModelChange={(e) => {
            setPaginationModel({ pageSize: e.pageSize, page: e.page });
          }}
          sx={{ border: 0 }}
        />
      </Paper>

      <Button onClick={() => navigate("/school")}>Back</Button>
    </Box>
  );
}
