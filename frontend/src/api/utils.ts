import { AuthConstants } from "../constants/auth";

export function isAuthenticated(): boolean {
  const access_token = localStorage.getItem(AuthConstants.ACCESS_TOKEN);
  return !!access_token;
}
