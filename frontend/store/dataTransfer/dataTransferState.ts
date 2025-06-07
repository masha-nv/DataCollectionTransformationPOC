const initialState = {
  selectedFiles: new Set(),
  transferLocation: {
    value: "MSIX",
    displayText: `U.S. Department of Education Office of Migrant Education – Migrant
          Student Information Exchange (MSIX) System`,
  },
  files: [],
};

const dataTransferStateReducer = (
  state: typeof initialState,
  action: { type: string; payload: any }
) => {
  switch (action.type) {
    case "select":
      return {
        ...state,
        selectedFiles: action.payload.fileIds,
      };
    case "transferLocation":
      return {
        ...state,
        transferLocation: action.payload.transferLocation,
      };
    case "files":
      return { ...state, files: action.payload.files };

    default:
      return state;
  }
};

export { initialState, dataTransferStateReducer };
