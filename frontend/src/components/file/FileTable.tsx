import * as React from "react";
import {
  DataGrid,
  GridColDef,
  GridColumnHeaderParams,
  GridRenderCellParams,
  GridRowParams,
} from "@mui/x-data-grid";
import Paper from "@mui/material/Paper";
import { api } from "../../api/api";
import { Box, Button, MenuItem, Typography } from "@mui/material";
import { useNavigate } from "react-router-dom";
import Welcome from "../../shared/Welcome";
import Select, { SelectChangeEvent } from "@mui/material/Select";
import moment from "moment";

const columns: GridColDef[] = [
  {
    field: "model_type",
    width: 850,
    renderHeader: (params: GridColumnHeaderParams) => (
      <strong>File Types</strong>
    ),
  },
  {
    field: "upload_datetime",
    width: 200,
    renderHeader: (params: GridColumnHeaderParams) => (
      <strong>Upload Date</strong>
    ),
    renderCell: (params: GridRenderCellParams) => (
      <span>{moment(params.value).format("MM/DD/YYYY")}</span>
    ),
  },
];

export default function DataTable() {
  const navigate = useNavigate();
  const [total, setTotal] = React.useState<number>(0);
  const [paginationModel, setPaginationModel] = React.useState<{
    page: number;
    pageSize: number;
  }>({ page: 0, pageSize: 5 });

  async function getFiles() {
    const {
      data: { items, total },
    } = await api.get(
      `/files?limit=${paginationModel.pageSize}&offset=${paginationModel.page}`
    );
    setData(items);
    setTotal(total);
  }

  React.useEffect(() => {
    getFiles();
  }, [paginationModel]);

  const [data, setData] = React.useState<any[]>();
  const [selected, setSelected] = React.useState<GridRowParams | null>(null);
  const [destination, setDestination] = React.useState("msix");

  function handleNavigateToReport() {
    if (!selected) return;
    const path =
      selected?.row.model_type === "LEA"
        ? `/lea/list?fileId=${selected.id}`
        : `/school/list?fileId=${selected.id}`;
    navigate(path);
  }

  return (
    <Box
      sx={{
        width: "100%",
        maxWidth: 1100,
        display: "flex",
        flexDirection: "column",
        alignContent: "center",
        alignItems: "center",
      }}>
      <Paper sx={{ height: "fit-content", width: "100%" }}>
        <DataGrid
          showToolbar={false}
          getRowId={(row) => row["id"]}
          rows={data}
          rowCount={total}
          columns={columns}
          onRowClick={(e) => {
            console.log(e);
            setSelected(e);
          }}
          initialState={{ pagination: { paginationModel } }}
          pageSizeOptions={[5, 10, 25, 50, 100]}
          checkboxSelection={true}
          onPaginationModelChange={(e) => {
            setPaginationModel({ pageSize: e.pageSize, page: e.page });
          }}
          sx={{ border: 0 }}
        />
      </Paper>
    </Box>
  );
}
