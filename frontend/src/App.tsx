import React from 'react';
import { BrowserRouter as Router, Routes, Route } from 'react-router-dom';
import { ThemeProvider, createTheme } from '@mui/material/styles';
import { CssBaseline, Box } from '@mui/material';
import { LocalizationProvider } from '@mui/x-date-pickers/LocalizationProvider';
import { AdapterDateFns } from '@mui/x-date-pickers/AdapterDateFns';
import { ToastContainer } from 'react-toastify';
import 'react-toastify/dist/ReactToastify.css';

// Components
import Header from './components/Header';
import Footer from './components/Footer';

// Pages
import HomePage from './pages/HomePage';
import SurveyListPage from './pages/SurveyListPage';
import SurveyDetailPage from './pages/SurveyDetailPage';
import CommonLoginPage from './pages/CommonLoginPage';

import AdminDashboardPage from './pages/AdminDashboardPage';
import AdminSurveyCreatePage from './pages/AdminSurveyCreatePage';
import AdminSurveyEditPage from './pages/AdminSurveyEditPage';
import AdminUserManagementPage from './pages/AdminUserManagementPage';

// Context and Providers
import { AuthProvider } from './context/AuthContext';
import { QueryProvider } from './providers/QueryProvider';
import { ToastProvider } from './providers/ToastProvider';

// Error Handling
import ErrorBoundary from './components/ErrorBoundary';

// Create theme
const theme = createTheme({
  palette: {
    primary: {
      main: '#1976d2',
    },
    secondary: {
      main: '#dc004e',
    },
  },
  typography: {
    fontFamily: 'Roboto, Arial, sans-serif',
  },
});

function App() {
  return (
    <ErrorBoundary>
      <QueryProvider>
        <ToastProvider>
          <ThemeProvider theme={theme}>
            <LocalizationProvider dateAdapter={AdapterDateFns}>
              <CssBaseline />
              <AuthProvider>
                <Router>
                  <Box sx={{ display: 'flex', flexDirection: 'column', minHeight: '100vh' }}>
                    <Header />
                    <Box component="main" sx={{ flexGrow: 1 }}>
                      <Routes>
                        <Route path="/" element={<HomePage />} />
                        <Route path="/surveys" element={<SurveyListPage />} />
                        <Route path="/surveys/:id" element={<SurveyDetailPage />} />
                        <Route path="/login" element={<CommonLoginPage />} />

                        <Route path="/admin/dashboard" element={<AdminDashboardPage />} />
                        <Route path="/admin/users" element={<AdminUserManagementPage />} />
                        <Route path="/admin/surveys/create" element={<AdminSurveyCreatePage />} />
                        <Route path="/admin/surveys/:id/edit" element={<AdminSurveyEditPage />} />
                      </Routes>
                    </Box>
                    <Footer />
                  </Box>
                </Router>
              </AuthProvider>
            </LocalizationProvider>
          </ThemeProvider>
        </ToastProvider>
        <ToastContainer />
      </QueryProvider>
    </ErrorBoundary>
  );
}

export default App; 