// Survey types
export interface Survey {
  id: number;
  title: string;
  description: string;
  status: 'DRAFT' | 'SCHEDULED' | 'ACTIVE' | 'CLOSED';
  startDate: string | null;
  endDate: string | null;
  createdAt: string;
  updatedAt: string;
  createdBy: string | null;
  questions: Question[];
  allowMultipleResponses: boolean;
  requireAuthentication: boolean;
  totalResponses: number;
  totalQuestions: number;
}

// Question types
export interface Question {
  id: number;
  text: string;
  type: 'TEXT' | 'MULTIPLE_CHOICE' | 'RATING' | 'YES_NO' | 'LIKERT_SCALE';
  orderIndex: number;
  required: boolean;
  createdAt: string;
  updatedAt: string;
  responses: Response[];
  maxResponses: number | null;
  allowMultipleAnswers: boolean;
  totalResponses: number;
  topResponses: Response[];
}

// Response types
export interface Response {
  id: number;
  text: string;
  createdAt: string;
  updatedAt: string;
  questionId: number;
  authorName: string;
  upvoteCount: number;
  hasUserUpvoted: boolean;
}

// User types
export interface User {
  id: number;
  username: string;
  email: string;
  role: 'USER' | 'ADMIN';
  active: boolean;
  createdAt: string;
  lastLogin: string | null;
}

// Authentication types
export interface LoginRequest {
  username: string;
  password: string;
}

export interface LoginResponse {
  token: string;
  username: string;
  role: string;
  message: string;
}

export interface AuthContextType {
  user: User | null;
  token: string | null;
  login: (username: string, password: string) => Promise<void>;
  logout: () => void;
  isAuthenticated: boolean;
  isAdmin: boolean;
}

// API Response types
export interface ApiResponse<T> {
  data: T;
  message?: string;
  error?: string;
}

// Statistics types
export interface SurveyStatistics {
  totalSurveys: number;
  activeSurveys: number;
  draftSurveys: number;
  closedSurveys: number;
}

export interface ResponseStatistics {
  totalResponses: number;
  totalUpvotes: number;
}

export interface UserStatistics {
  totalUsers: number;
  adminUsers: number;
  activeUsers: number;
}

// Form types
export interface SurveyFormData {
  title: string;
  description: string;
  status: string;
  startDate: string | null;
  endDate: string | null;
  allowMultipleResponses: boolean;
  requireAuthentication: boolean;
  questions?: QuestionFormData[];
}

export interface QuestionFormData {
  text: string;
  type: string;
  required: boolean;
  orderIndex: number;
  maxResponses: number | null;
  allowMultipleAnswers: boolean;
}

export interface ResponseFormData {
  text: string;
} 