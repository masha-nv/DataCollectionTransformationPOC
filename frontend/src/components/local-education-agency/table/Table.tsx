import * as React from "react";
import {
  DataGrid,
  GridColDef,
  GridDeleteIcon,
  GridRowId,
} from "@mui/x-data-grid";
import Paper from "@mui/material/Paper";
import { Box, Button, IconButton, Tooltip, Typography } from "@mui/material";
import { useNavigate, useSearchParams } from "react-router-dom";
import Toolbar from "@mui/material/Toolbar";
import { api } from "../../../api/api";
import classes from "./Table.module.scss";
import BackButton from "../../../shared/back-button/BackButton";

const columns: GridColDef[] = [
  { field: "lea_name", headerName: "Name", width: 200 },
  { field: "loc_city", headerName: "City", width: 100 },
  { field: "loc_state", headerName: "State", width: 100 },
  { field: "operational_status", headerName: "Operation Status", width: 100 },
  {
    field: "loc_zipcode",
    headerName: "ZipCode",
    width: 150,
  },
  {
    field: "school_year",
    headerName: "School Year",
    width: 150,
  },
];

export default function Table() {
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
      ? `/lea/${fileId}?limit=${paginationModel.pageSize}&offset=${paginationModel.page}`
      : `/lea?limit=${paginationModel.pageSize}&offset=${paginationModel.page}`;
    const {
      data: { items, total },
    } = await api.get(endpoint);
    setData(items);
    setTotal(total);
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
  }, [paginationModel]);

  const [data, setData] = React.useState<any[]>();
  const [selectedIds, setSelectedIds] = React.useState<Set<GridRowId> | null>(
    null
  );

  return (
    <>
      <BackButton text={"Home"} url='/home' />
      <Box className={classes.container}>
        <Typography className={classes.title}>
          Local Education Agency (LEA) Data Upload Tool
        </Typography>
        <Paper sx={{ height: "fit-content", width: "100%" }}>
          {!!selectedIds?.size && (
            <Toolbar>
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
            </Toolbar>
          )}

          <DataGrid
            paginationMode='server'
            showToolbar={false}
            getRowId={(row) => row["district_nces_id"]}
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
        <Box
          sx={{
            display: "flex",
            justifyContent: "space-between",
            marginTop: "2rem",
          }}>
          <Button
            variant='outlined'
            color='secondary'
            onClick={() => navigate("/home")}>
            Exit tool
          </Button>
          <Button
            variant='contained'
            color='primary'
            onClick={() => navigate("/lea")}>
            Done editing
          </Button>
        </Box>
      </Box>
    </>
  );
}
