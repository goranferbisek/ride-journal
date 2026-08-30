import axios from "axios";
import type {AxiosInstance, AxiosResponse, InternalAxiosRequestConfig} from "axios";

const BASE_URL = "http://localhost:8080/api/v1";

let jwtToken: string | null = null;
export const setAuthToken = (token: string | null) => {
  jwtToken = token
};

const api: AxiosInstance = axios.create({
  baseURL: BASE_URL,
  headers: {
    Accept: "application/json"
  },
  timeout: 10000,
});

api.interceptors.request.use((config: InternalAxiosRequestConfig) => {
  if (jwtToken) {
    config.headers.set("Authorization", `Bearer ${jwtToken}`);
  }
  return config;
});

api.interceptors.response.use(
  (response: AxiosResponse) => response,
  (error) => {
    if (error.response?.status === 401) {
      setAuthToken(null);
      if (!error.config?.url?.includes("/auth/login")) {
        window.location.href = "/login";
      }
    }
    return Promise.reject(error);
  }
);

export default api;