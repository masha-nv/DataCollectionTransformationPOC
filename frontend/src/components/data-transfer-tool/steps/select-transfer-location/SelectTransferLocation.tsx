import { Box, Select, Typography } from "@mui/material";
import React, { useContext, useState } from "react";
import MenuItem from "@mui/material/MenuItem";
import { DataTransferContext } from "../../../../../store/dataTransfer/DataTransferProvider";
const options = [
  {
    value: "MSIX",
    displayText: `U.S. Department of Education Office of Migrant Education – Migrant
          Student Information Exchange (MSIX) System`,
  },
];

const SelectTransferLocation = () => {
  const { dispatch, state } = useContext(DataTransferContext);
  return (
    <Box>
      <Select
        sx={{ width: "100%" }}
        value={state.transferLocation.value}
        onChange={(e) => {
          console.log("event", e);
          dispatch({
            type: "transferLocation",
            payload: {
              transferLocation: {
                value: e.target.value,
                displayText: options.find((o) => o.value === e.target.value)
                  ?.displayText,
              },
            },
          });
        }}>
        {options.map((i) => (
          <MenuItem key={i.value} value={i.value}>
            {i.displayText}
          </MenuItem>
        ))}
      </Select>
    </Box>
  );
};

export default SelectTransferLocation;
