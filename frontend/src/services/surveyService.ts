import axios from 'axios';
import { Survey, Response, ResponseFormData } from '../types';

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

export const surveyService = {
  /**
   * Get all active surveys
   */
  async getActiveSurveys(): Promise<Survey[]> {
    try {
      const response = await apiClient.get('/surveys/active');
      return response.data;
    } catch (error: any) {
      throw new Error('Failed to fetch active surveys');
    }
  },

  /**
   * Get survey by ID
   */
  async getSurveyById(id: number): Promise<Survey> {
    try {
      const response = await apiClient.get(`/surveys/${id}`);
      return response.data;
    } catch (error: any) {
      throw new Error('Failed to fetch survey');
    }
  },

  /**
   * Get top responses for a question
   */
  async getTopResponses(questionId: number, limit: number = 5): Promise<Response[]> {
    try {
      const response = await apiClient.get(`/questions/${questionId}/responses?limit=${limit}`);
      return response.data;
    } catch (error: any) {
      throw new Error('Failed to fetch responses');
    }
  },

  /**
   * Submit a response to a question
   */
  async submitResponse(questionId: number, responseData: ResponseFormData): Promise<Response> {
    try {
      const response = await apiClient.post('/responses', responseData, {
        params: { questionId },
      });
      return response.data;
    } catch (error: any) {
      if (error.response?.data?.message) {
        throw new Error(error.response.data.message);
      }
      throw new Error('Failed to submit response');
    }
  },

  /**
   * Upvote a response
   */
  async upvoteResponse(responseId: number): Promise<void> {
    try {
      await apiClient.post(`/responses/${responseId}/upvote`);
    } catch (error: any) {
      if (error.response?.data?.message) {
        throw new Error(error.response.data.message);
      }
      throw new Error('Failed to upvote response');
    }
  },

  /**
   * Remove upvote from a response
   */
  async removeUpvote(responseId: number): Promise<void> {
    try {
      await apiClient.delete(`/responses/${responseId}/upvote`);
    } catch (error: any) {
      if (error.response?.data?.message) {
        throw new Error(error.response.data.message);
      }
      throw new Error('Failed to remove upvote');
    }
  },

  /**
   * Get question statistics
   */
  async getQuestionStatistics(questionId: number): Promise<any> {
    try {
      const response = await apiClient.get(`/questions/${questionId}/statistics`);
      return response.data;
    } catch (error: any) {
      throw new Error('Failed to fetch question statistics');
    }
  },
}; 