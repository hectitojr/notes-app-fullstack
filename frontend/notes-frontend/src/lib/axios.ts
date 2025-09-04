import axios from 'axios';
import { env } from './env';

export const api = axios.create({
  baseURL: env.API_BASE_URL,
  timeout: 10000,
});

api.interceptors.response.use(
  (res) => res,
  (err) => {
    // Normaliza error del backend (GlobalExceptionHandler)
    const data = err?.response?.data;
    const normalized = {
      status: err?.response?.status,
      message: data?.message ?? 'Error inesperado',
      code: data?.code,
      fieldErrors: data?.fieldErrors ?? {},
    };
    return Promise.reject(normalized);
  }
);
