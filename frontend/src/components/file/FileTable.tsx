import * as React from "react";
import {
  DataGrid,
  GridColDef,
  GridColumnHeaderParams,
  GridRenderCellParams,
  GridRowParams,
  GridRowSelectionModel,
} from "@mui/x-data-grid";
import Paper from "@mui/material/Paper";
import { api } from "../../api/api";
import { Box, Button, MenuItem, Typography } from "@mui/material";
import { useNavigate } from "react-router-dom";
import Welcome from "../../shared/Welcome";
import Select, { SelectChangeEvent } from "@mui/material/Select";
import moment from "moment";
import { DataTransferContext } from "../../../store/dataTransfer/DataTransferProvider";
import { formatFileSize } from "../../utils/formatFileSize";

const columns: GridColDef[] = [
  {
    field: "name",
    width: 500,
    renderHeader: (params: GridColumnHeaderParams) => <strong>Name</strong>,
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
  {
    field: "size",
    width: 200,
    renderHeader: (params: GridColumnHeaderParams) => <strong>Size</strong>,
    renderCell: (params: GridRenderCellParams) => (
      <span>{formatFileSize(params.value)}</span>
    ),
  },
];

export default function DataTable() {
  const ctx = React.useContext(DataTransferContext);
  const [total, setTotal] = React.useState<number>(0);
  const [selectedRowIds, setSelectedRowIds] =
    React.useState<GridRowSelectionModel>({ type: "include", ids: new Set() });
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
    ctx?.dispatch({ type: "files", payload: { files: items } });
  }

  React.useEffect(() => {
    if (!ctx?.state.files.length) {
      getFiles();
    } else {
      setData(ctx?.state.files);
    }
  }, [paginationModel]);

  const [data, setData] = React.useState<any[]>();

  function handleSelectionChange(newSelection) {
    ctx?.dispatch({ type: "select", payload: { fileIds: newSelection.ids } });
  }

  React.useEffect(() => {
    const selected = ctx?.state.selectedFiles;
    setSelectedRowIds({ ...selectedRowIds, ids: selected });
  }, [ctx, ctx?.state]);

  return (
    <Box
      sx={{
        display: "flex",
        flexDirection: "column",
        alignContent: "center",
        alignItems: "center",
      }}>
      <Paper sx={{ height: "fit-content", width: "100%" }}>
        <DataGrid
          onRowSelectionModelChange={handleSelectionChange}
          disableRowSelectionOnClick
          showToolbar={false}
          getRowId={(row) => row["id"]}
          rows={data}
          // rowSelectionModel={selectedRowIds}
          rowCount={total}
          columns={columns}
          rowSelectionModel={selectedRowIds}
          paginationMode='server'
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
