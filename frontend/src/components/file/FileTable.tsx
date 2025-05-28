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
        margin: "10rem auto",
        display: "flex",
        flexDirection: "column",
        alignContent: "center",
        alignItems: "center",
      }}>
      <Typography variant='h5' color='#102f3c' marginBottom={".5rem"}>
        OME Data Extraction Portal{" "}
      </Typography>
      <Welcome />
      <Typography sx={{ width: "100%", marginTop: 10, marginBottom: 1 }}>
        Available Data
      </Typography>
      <Paper sx={{ height: "fit-content", width: "100%" }}>
        <Typography sx={{ padding: ".5rem", color: "#5e7f8d" }}>
          Please select data sets for extraction
        </Typography>
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
          checkboxSelection={true}
          onPaginationModelChange={(e) => {
            setPageLimit(e.pageSize);
          }}
          sx={{ border: 0 }}
        />
      </Paper>
      {selected && (
        <Button onClick={handleNavigateToReport}>View Report</Button>
      )}
      <Box
        sx={{
          marginTop: "5rem",
          width: "100%",
          display: "flex",
          flexDirection: "column",
          alignItems: "center",
        }}>
        <Typography sx={{ alignSelf: "flex-start" }}>
          Extraction Destination
        </Typography>
        <Select
          sx={{ marginBottom: "1rem", width: "100%" }}
          onChange={(e) => setDestination(e.target.value)}
          value={destination}>
          <MenuItem value='msix'>
            ED Office of Migrant Education - Migrant Student Information
            Exchange (MSIX) System
          </MenuItem>
          <MenuItem value={"location_one"}>Location 1</MenuItem>
          <MenuItem value={"location_two"}>Location 2</MenuItem>
          <MenuItem value={"location_three"}>Location 3</MenuItem>
        </Select>
        <br />
        <Button sx={{ width: "fit-content" }} variant='contained'>
          Extract
        </Button>
      </Box>
    </Box>
  );
}
