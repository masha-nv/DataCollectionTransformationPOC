const initialState = {
  data: {},
};

const fileDataStateReducer = (
  state: typeof initialState,
  action: { type: string; payload: any }
) => {
  switch (action.type) {
    case "upload":
      return {
        ...state,
        data: {
          fileName: action.payload.fileName,
          data: action.payload.data,
          fileSize: action.payload.fileSize,
          fileId: action.payload.fileId,
          file: action.payload.file,
        },
      };
    case "delete":
      return {
        ...state,
        data: {},
      };
    default:
      return state;
  }
};

export { initialState, fileDataStateReducer };
