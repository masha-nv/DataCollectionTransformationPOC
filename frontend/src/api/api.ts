import axios from "axios";

const baseURL = (import.meta as any).env.VITE_API_BASE_URL;
interface ImportMeta {
  env: {
    VITE_API_BASE_URL: string;
  };
}

const api = axios.create({
  baseURL: baseURL,
});

api.interceptors.request.use((config) => {
  const token = localStorage.getItem("access_token");
  if (token) {
    config.headers.Authorization = `Bearer ${token}`;
  }
  return config;
});

export { api };
