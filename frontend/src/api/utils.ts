import { AuthConstants } from "../constants/auth";
import { jwtDecode } from "jwt-decode";

export function isAuthenticated(): boolean {
  const access_token = localStorage.getItem(AuthConstants.ACCESS_TOKEN);
  const decoded_token = getDecodedToken(access_token);
  console.log(decoded_token);
  if (decoded_token) {
    const expiration = decoded_token.exp * 1000; // converts seconds to milliseconds
    const now = Date.now();
    return now < expiration;
  }
  return false;
}

function getDecodedToken(token: string | null): any | null {
  if (token) {
    try {
      return jwtDecode(token);
    } catch (error) {
      console.error("Invalid token", error);
      return null;
    }
  }
  return null;
}
