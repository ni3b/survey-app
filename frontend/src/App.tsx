import React from 'react';
import { BrowserRouter as Router, Routes, Route } from 'react-router-dom';
import { ThemeProvider, createTheme } from '@mui/material/styles';
import { CssBaseline, Box } from '@mui/material';
import { LocalizationProvider } from '@mui/x-date-pickers/LocalizationProvider';
import { AdapterDateFns } from '@mui/x-date-pickers/AdapterDateFns';

// Components
import Header from './components/Header';
import Footer from './components/Footer';

// Pages
import HomePage from './pages/HomePage';
import SurveyListPage from './pages/SurveyListPage';
import SurveyDetailPage from './pages/SurveyDetailPage';
import AdminLoginPage from './pages/AdminLoginPage';
import AdminDashboardPage from './pages/AdminDashboardPage';
import AdminSurveyCreatePage from './pages/AdminSurveyCreatePage';
import AdminSurveyEditPage from './pages/AdminSurveyEditPage';
import AdminUserManagementPage from './pages/AdminUserManagementPage';

// Context and Providers
import { AuthProvider } from './context/AuthContext';
import { QueryProvider } from './providers/QueryProvider';
import { ToastProvider } from './providers/ToastProvider';

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
                      <Route path="/admin/login" element={<AdminLoginPage />} />
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
    </QueryProvider>
  );
}

export default App; 