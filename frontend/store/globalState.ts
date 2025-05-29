import { isAuthenticated } from "../src/api/utils";
import { AuthConstants } from "../src/constants/auth";

const initialState = {
  isLoggedIn: isAuthenticated(),
};

const globalStateReducer = (
  state: typeof initialState,
  action: { type: string; payload: any }
) => {
  switch (action.type) {
    case "login":
      localStorage.setItem(
        AuthConstants.ACCESS_TOKEN,
        action.payload.access_token
      );
      localStorage.setItem(
        AuthConstants.USER,
        JSON.stringify(action.payload.user)
      );
      return { ...state, isLoggedIn: true };
    case "logout":
      localStorage.removeItem(AuthConstants.ACCESS_TOKEN);
      localStorage.removeItem(AuthConstants.USER);
      return { ...state, isLoggedIn: false };
    default:
      return state;
  }
};

export { initialState, globalStateReducer };
