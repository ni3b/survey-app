import axios from 'axios';
import { LoginRequest, LoginResponse, User } from '../types';
import { handleError } from './errorService';

const API_BASE_URL = process.env.REACT_APP_API_URL || '/api';

// Create axios instance with base configuration
const apiClient = axios.create({
  baseURL: API_BASE_URL,
  headers: {
    'Content-Type': 'application/json',
  },
});

// Add request interceptor to include auth token
apiClient.interceptors.request.use(
  (config) => {
    const token = localStorage.getItem('token');
    if (token) {
      config.headers.Authorization = `Bearer ${token}`;
    }
    return config;
  },
  (error) => {
    return Promise.reject(error);
  }
);

// Add response interceptor to handle auth errors
apiClient.interceptors.response.use(
  (response) => response,
  (error) => {
    if (error.response?.status === 401) {
      localStorage.removeItem('token');
      window.location.href = '/admin/login';
    }
    return Promise.reject(error);
  }
);

export const authService = {
  /**
   * Login user
   */
  async login(credentials: LoginRequest): Promise<LoginResponse> {
    try {
      const response = await apiClient.post('/auth/login', credentials);
      return response.data;
    } catch (error: any) {
      handleError(error, { component: 'AuthService', action: 'login' }, false);
      throw error; // Re-throw to let the component handle it
    }
  },

  /**
   * Get current user information
   */
  async getCurrentUser(): Promise<User> {
    try {
      const response = await apiClient.get('/auth/me');
      return response.data;
    } catch (error: any) {
      handleError(error, { component: 'AuthService', action: 'getCurrentUser' }, false);
      throw error;
    }
  },

  /**
   * Validate JWT token
   */
  async validateToken(token: string): Promise<boolean> {
    try {
      const response = await apiClient.get('/auth/validate', {
        headers: {
          Authorization: `Bearer ${token}`,
        },
      });
      return response.data.message === 'Token is valid';
    } catch (error) {
      return false;
    }
  },

  /**
   * Logout user
   */
  logout(): void {
    localStorage.removeItem('token');
  },
}; 