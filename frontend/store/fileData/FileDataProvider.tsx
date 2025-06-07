import React, { createContext, ReactNode, useReducer } from "react";
import { fileDataStateReducer, initialState } from "./fileDataState";

interface FileDataContextProps {
  state: typeof initialState;
  dispatch: React.Dispatch<any>;
}

export const FileDataContext = createContext<FileDataContextProps | undefined>(
  undefined
);

interface FileDataProviderProps {
  children: ReactNode;
}

export const FileDataProvider: React.FC<FileDataProviderProps> = ({
  children,
}) => {
  const [state, dispatch] = useReducer(fileDataStateReducer, initialState);
  return (
    <FileDataContext.Provider value={{ state, dispatch }}>
      {children}
    </FileDataContext.Provider>
  );
};
