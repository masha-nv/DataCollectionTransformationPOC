import * as React from "react";
import Box from "@mui/material/Box";
import Stepper from "@mui/material/Stepper";
import Step from "@mui/material/Step";
import StepLabel from "@mui/material/StepLabel";
import Button from "@mui/material/Button";
import Typography from "@mui/material/Typography";
import FileTable from "../../file/FileTable";
import ChooseDataSource from "./choose-data-source/ChooseDataSource";
import SelectTransferLocation from "./select-transfer-location/SelectTransferLocation";
import { DataTransferContext } from "../../../../store/dataTransfer/DataTransferProvider";
import ReviewAndTransfer from "./review-and-transfer/ReviewAndTransfer";
import { useNavigate } from "react-router-dom";
const steps = [
  { label: "Choose data source", component: FileTable },
  { label: "Select transfer location", component: <>todo</> },
  { label: "Review & Transfer", component: <>todo</> },
];

export default function Steps() {
  const { state } = React.useContext(DataTransferContext);
  const [activeStep, setActiveStep] = React.useState(0);
  const navigate = useNavigate();

  const handleNext = () => {
    setActiveStep((prevActiveStep) => prevActiveStep + 1);
    if (activeStep === steps.length - 1) {
      navigate("/data-transfer-tool/success");
    }
  };

  const handleBack = () => {
    setActiveStep((prevActiveStep) => prevActiveStep - 1);
  };

  const handleReset = () => {
    setActiveStep(0);
  };

  function isNextButtonDisabled(): boolean {
    if (activeStep === 0) {
      if (state.selectedFiles.size === 0) {
        return true;
      }
      return false;
    }
    return false;
  }

  return (
    <Box
      sx={{
        width: "100%",
        display: "flex",
        flexDirection: "column",
        gap: "2rem",
      }}>
      <Stepper activeStep={activeStep}>
        {steps.map(({ label, component }, index) => {
          const stepProps: { completed?: boolean } = {};
          const labelProps: {
            optional?: React.ReactNode;
          } = {};
          return (
            <Step key={label} {...stepProps}>
              <StepLabel {...labelProps}>{label}</StepLabel>
            </Step>
          );
        })}
      </Stepper>
      {activeStep === steps.length ? (
        <React.Fragment>
          <Typography sx={{ mt: 2, mb: 1 }}>
            All steps completed - you&apos;re finished
          </Typography>
          <Box sx={{ display: "flex", flexDirection: "row", pt: 2 }}>
            <Box sx={{ flex: "1 1 auto" }} />
            <Button onClick={handleReset}>Reset</Button>
          </Box>
        </React.Fragment>
      ) : (
        <Box sx={{ display: "flex", flexDirection: "column", gap: "2rem" }}>
          <Box>{activeStep === 0 && <ChooseDataSource />}</Box>
          <Box>{activeStep === 1 && <SelectTransferLocation />}</Box>
          <Box>{activeStep === 2 && <ReviewAndTransfer />}</Box>
          <Box sx={{ display: "flex", flexDirection: "row", pt: 2 }}>
            <Button
              color='secondary'
              variant='outlined'
              disabled={activeStep === 0}
              onClick={handleBack}
              sx={{ mr: 1 }}>
              Back
            </Button>
            <Box sx={{ flex: "1 1 auto" }} />
            <Button
              disabled={isNextButtonDisabled()}
              onClick={handleNext}
              color='primary'
              variant='contained'>
              {activeStep === steps.length - 1 ? "Finish" : "Next"}
            </Button>
          </Box>
        </Box>
      )}
    </Box>
  );
}
