import React, { createContext, ReactNode, useReducer } from "react";
import { dataTransferStateReducer, initialState } from "./dataTransferState";

interface DataTransferContextProps {
  state: typeof initialState;
  dispatch: React.Dispatch<any>;
}

export const DataTransferContext = createContext<
  DataTransferContextProps | undefined
>(undefined);

interface FileDataProviderProps {
  children: ReactNode;
}

export const DataTransferProvider: React.FC<FileDataProviderProps> = ({
  children,
}) => {
  const [state, dispatch] = useReducer(dataTransferStateReducer, initialState);
  return (
    <DataTransferContext.Provider value={{ state, dispatch }}>
      {children}
    </DataTransferContext.Provider>
  );
};
