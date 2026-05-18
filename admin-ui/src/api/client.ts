import axios from 'axios';
import { clearTokens, getAccessToken } from './authStorage';

const baseURL = import.meta.env.VITE_API_BASE_URL ?? 'http://localhost:8080';

export const apiClient = axios.create({
  baseURL,
  headers: {
    'Content-Type': 'application/json',
  },
});

apiClient.interceptors.request.use((config) => {
  const token = getAccessToken();
  if (token) {
    config.headers.Authorization = `Bearer ${token}`;
  }
  return config;
});

apiClient.interceptors.response.use(
  (response) => response,
  (error) => {
    if (error.response?.status === 401) {
      clearTokens();
    }
    return Promise.reject(error);
  },
);

export function getApiErrorMessage(error: unknown) {
  if (axios.isAxiosError(error)) {
    const body = error.response?.data;
    if (typeof body === 'string') return body;
    if (body?.message) return body.message;
    if (body?.errorMessage) return body.errorMessage;
    if (body?.errors && Array.isArray(body.errors)) return body.errors.join(', ');
    return error.message;
  }
  return 'Unexpected error';
}
