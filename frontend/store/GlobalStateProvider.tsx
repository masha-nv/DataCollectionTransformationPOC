import React, { createContext, useReducer, ReactNode } from "react";
import { globalStateReducer, initialState } from "./globalState";

interface GlobalStateContextProps {
  state: typeof initialState;
  dispatch: React.Dispatch<any>;
}

export const GlobalStateContext = createContext<
  GlobalStateContextProps | undefined
>(undefined);

interface GlobalStateProviderProps {
  children: ReactNode;
}

export const GlobalStateProvider: React.FC<GlobalStateProviderProps> = ({
  children,
}) => {
  const [state, dispatch] = useReducer(globalStateReducer, initialState);
  return (
    <GlobalStateContext.Provider value={{ state, dispatch }}>
      {children}
    </GlobalStateContext.Provider>
  );
};
