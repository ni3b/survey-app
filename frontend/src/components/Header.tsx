import React from 'react';
import {
  AppBar,
  Toolbar,
  Typography,
  Button,
  Box,
  Container,
} from '@mui/material';
import { Link as RouterLink, useNavigate } from 'react-router-dom';
import { useAuth } from '../context/AuthContext';

const Header: React.FC = () => {
  const { user, isAuthenticated, isAdmin, logout } = useAuth();
  const navigate = useNavigate();

  const handleLogout = () => {
    logout();
    navigate('/');
  };

  return (
    <AppBar position="static">
      <Container maxWidth="lg">
        <Toolbar>
          <Typography
            variant="h6"
            component={RouterLink}
            to="/"
            sx={{
              flexGrow: 1,
              textDecoration: 'none',
              color: 'inherit',
              fontWeight: 'bold',
            }}
          >
            Survey Application
          </Typography>

          <Box sx={{ display: 'flex', gap: 2 }}>
            <Button
              color="inherit"
              component={RouterLink}
              to="/surveys"
            >
              Surveys
            </Button>

            {isAuthenticated && isAdmin ? (
              <>
                <Button
                  color="inherit"
                  component={RouterLink}
                  to="/admin/dashboard"
                >
                  Dashboard
                </Button>
                <Button
                  color="inherit"
                  component={RouterLink}
                  to="/admin/users"
                >
                  Users
                </Button>
                <Button
                  color="inherit"
                  component={RouterLink}
                  to="/admin/surveys/create"
                >
                  Create Survey
                </Button>
                <Button
                  color="inherit"
                  onClick={handleLogout}
                >
                  Logout ({user?.username})
                </Button>
              </>
            ) : (
              <Button
                color="inherit"
                component={RouterLink}
                to="/admin/login"
              >
                Admin Login
              </Button>
            )}
          </Box>
        </Toolbar>
      </Container>
    </AppBar>
  );
};

export default Header; 