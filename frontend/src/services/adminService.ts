import axios from 'axios';
import { Survey, SurveyFormData, QuestionFormData } from '../types';

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

export const adminService = {
  /**
   * Get all surveys (admin view)
   */
  async getAllSurveys(): Promise<Survey[]> {
    try {
      const response = await apiClient.get('/admin/surveys');
      return response.data;
    } catch (error: any) {
      throw new Error('Failed to fetch surveys');
    }
  },

  /**
   * Get survey by ID (admin view)
   */
  async getSurveyById(id: number): Promise<Survey> {
    try {
      const response = await apiClient.get(`/admin/surveys/${id}`);
      return response.data;
    } catch (error: any) {
      throw new Error('Failed to fetch survey');
    }
  },

  /**
   * Create a new survey
   */
  async createSurvey(surveyData: SurveyFormData): Promise<Survey> {
    try {
      const response = await apiClient.post('/admin/surveys', surveyData);
      return response.data;
    } catch (error: any) {
      if (error.response?.data?.error) {
        throw new Error(error.response.data.error);
      }
      throw new Error('Failed to create survey');
    }
  },

  /**
   * Update an existing survey
   */
  async updateSurvey(id: number, surveyData: SurveyFormData): Promise<Survey> {
    try {
      const response = await apiClient.put(`/admin/surveys/${id}`, surveyData);
      return response.data;
    } catch (error: any) {
      if (error.response?.data?.error) {
        throw new Error(error.response.data.error);
      }
      throw new Error('Failed to update survey');
    }
  },

  /**
   * Delete a survey
   */
  async deleteSurvey(id: number): Promise<void> {
    try {
      await apiClient.delete(`/admin/surveys/${id}`);
    } catch (error: any) {
      if (error.response?.data?.error) {
        throw new Error(error.response.data.error);
      }
      throw new Error('Failed to delete survey');
    }
  },

  /**
   * Publish a survey
   */
  async publishSurvey(id: number): Promise<Survey> {
    try {
      const response = await apiClient.post(`/admin/surveys/${id}/publish`);
      return response.data;
    } catch (error: any) {
      if (error.response?.data?.error) {
        throw new Error(error.response.data.error);
      }
      throw new Error('Failed to publish survey');
    }
  },

  /**
   * Schedule a survey
   */
  async scheduleSurvey(id: number, startDate: string): Promise<Survey> {
    try {
      const response = await apiClient.post(`/admin/surveys/${id}/schedule`, {
        startDate,
      });
      return response.data;
    } catch (error: any) {
      if (error.response?.data?.error) {
        throw new Error(error.response.data.error);
      }
      throw new Error('Failed to schedule survey');
    }
  },

  /**
   * Close a survey
   */
  async closeSurvey(id: number): Promise<Survey> {
    try {
      const response = await apiClient.post(`/admin/surveys/${id}/close`);
      return response.data;
    } catch (error: any) {
      if (error.response?.data?.error) {
        throw new Error(error.response.data.error);
      }
      throw new Error('Failed to close survey');
    }
  },

  /**
   * Add a question to a survey
   */
  async addQuestion(surveyId: number, questionData: QuestionFormData): Promise<Survey> {
    try {
      const response = await apiClient.post(`/admin/surveys/${surveyId}/questions`, questionData);
      return response.data;
    } catch (error: any) {
      if (error.response?.data?.error) {
        throw new Error(error.response.data.error);
      }
      throw new Error('Failed to add question');
    }
  },

  /**
   * Update a question in a survey
   */
  async updateQuestion(surveyId: number, questionId: number, questionData: QuestionFormData): Promise<void> {
    try {
      await apiClient.put(`/admin/surveys/${surveyId}/questions/${questionId}`, questionData);
    } catch (error: any) {
      if (error.response?.data?.error) {
        throw new Error(error.response.data.error);
      }
      throw new Error('Failed to update question');
    }
  },

  /**
   * Delete a question from a survey
   */
  async deleteQuestion(surveyId: number, questionId: number): Promise<Survey> {
    try {
      const response = await apiClient.delete(`/admin/surveys/${surveyId}/questions/${questionId}`);
      return response.data;
    } catch (error: any) {
      if (error.response?.data?.error) {
        throw new Error(error.response.data.error);
      }
      throw new Error('Failed to delete question');
    }
  },

  /**
   * Get analytics data
   */
  async getAnalytics(): Promise<any> {
    try {
      const response = await apiClient.get('/admin/analytics');
      return response.data;
    } catch (error: any) {
      throw new Error('Failed to fetch analytics');
    }
  },

  /**
   * Get survey statistics
   */
  async getSurveyStatistics(surveyId: number): Promise<any> {
    try {
      const response = await apiClient.get(`/admin/surveys/${surveyId}/statistics`);
      return response.data;
    } catch (error: any) {
      throw new Error('Failed to fetch survey statistics');
    }
  },

  /**
   * Get all responses for a survey
   */
  async getAllResponsesForSurvey(surveyId: number): Promise<any[]> {
    try {
      const response = await apiClient.get(`/admin/surveys/${surveyId}/responses`);
      return response.data;
    } catch (error: any) {
      throw new Error('Failed to fetch survey responses');
    }
  },

  /**
   * Export survey data
   */
  async exportSurveyData(surveyId: number): Promise<any> {
    try {
      const response = await apiClient.get(`/admin/surveys/${surveyId}/export`);
      return response.data;
    } catch (error: any) {
      throw new Error('Failed to export survey data');
    }
  },

  // User Management Methods
  /**
   * Get all users
   */
  async getAllUsers(): Promise<any[]> {
    try {
      const response = await apiClient.get('/admin/users');
      return response.data;
    } catch (error: any) {
      throw new Error('Failed to fetch users');
    }
  },

  /**
   * Get user by ID
   */
  async getUserById(id: number): Promise<any> {
    try {
      const response = await apiClient.get(`/admin/users/${id}`);
      return response.data;
    } catch (error: any) {
      throw new Error('Failed to fetch user');
    }
  },

  /**
   * Create a new user
   */
  async createUser(userData: any): Promise<any> {
    try {
      const response = await apiClient.post('/admin/users', userData);
      return response.data;
    } catch (error: any) {
      if (error.response?.data?.error) {
        throw new Error(error.response.data.error);
      }
      throw new Error('Failed to create user');
    }
  },

  /**
   * Update an existing user
   */
  async updateUser(id: number, userData: any): Promise<any> {
    try {
      const response = await apiClient.put(`/admin/users/${id}`, userData);
      return response.data;
    } catch (error: any) {
      if (error.response?.data?.error) {
        throw new Error(error.response.data.error);
      }
      throw new Error('Failed to update user');
    }
  },

  /**
   * Delete a user
   */
  async deleteUser(id: number): Promise<void> {
    try {
      await apiClient.delete(`/admin/users/${id}`);
    } catch (error: any) {
      if (error.response?.data?.error) {
        throw new Error(error.response.data.error);
      }
      throw new Error('Failed to delete user');
    }
  },

  /**
   * Change user role
   */
  async changeUserRole(id: number, role: string): Promise<void> {
    try {
      await apiClient.put(`/admin/users/${id}/role`, { role });
    } catch (error: any) {
      if (error.response?.data?.error) {
        throw new Error(error.response.data.error);
      }
      throw new Error('Failed to change user role');
    }
  },

  /**
   * Change user status (activate/deactivate)
   */
  async changeUserStatus(id: number, active: boolean): Promise<void> {
    try {
      await apiClient.put(`/admin/users/${id}/status`, { active });
    } catch (error: any) {
      if (error.response?.data?.error) {
        throw new Error(error.response.data.error);
      }
      throw new Error('Failed to change user status');
    }
  },

  /**
   * Get users by role
   */
  async getUsersByRole(role: string): Promise<any[]> {
    try {
      const response = await apiClient.get(`/admin/users/role/${role}`);
      return response.data;
    } catch (error: any) {
      throw new Error('Failed to fetch users by role');
    }
  },

  /**
   * Search users
   */
  async searchUsers(searchTerm: string): Promise<any[]> {
    try {
      const response = await apiClient.get(`/admin/users/search?searchTerm=${encodeURIComponent(searchTerm)}`);
      return response.data;
    } catch (error: any) {
      throw new Error('Failed to search users');
    }
  },

  /**
   * Get user statistics
   */
  async getUserStatistics(): Promise<any> {
    try {
      const response = await apiClient.get('/admin/users/statistics');
      return response.data;
    } catch (error: any) {
      throw new Error('Failed to fetch user statistics');
    }
  },
}; 