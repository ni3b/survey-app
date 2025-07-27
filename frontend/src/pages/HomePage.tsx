import React from 'react';
import {
  Container,
  Typography,
  Box,
  Button,
  Card,
  CardContent,
  Grid,
  Paper,
} from '@mui/material';
import { Link as RouterLink } from 'react-router-dom';
import {
  Assessment,
  Create,
  Analytics,
  Security,
} from '@mui/icons-material';

const HomePage: React.FC = () => {
  const features = [
    {
      icon: <Assessment fontSize="large" color="primary" />,
      title: 'Create Surveys',
      description: 'Design and customize surveys with various question types including text, rating, and multiple choice.',
    },
    {
      icon: <Create fontSize="large" color="primary" />,
      title: 'Participate',
      description: 'Take surveys and provide feedback. Your responses help improve products and services.',
    },
    {
      icon: <Analytics fontSize="large" color="primary" />,
      title: 'Analytics',
      description: 'View detailed analytics and insights from survey responses with interactive charts.',
    },
    {
      icon: <Security fontSize="large" color="primary" />,
      title: 'Secure',
      description: 'Your data is protected with secure authentication and encrypted storage.',
    },
  ];

  return (
    <Box sx={{ flexGrow: 1 }}>
      {/* Hero Section */}
      <Paper
        sx={{
          position: 'relative',
          backgroundColor: 'grey.800',
          color: '#fff',
          mb: 4,
          backgroundSize: 'cover',
          backgroundRepeat: 'no-repeat',
          backgroundPosition: 'center',
          backgroundImage: 'url(https://source.unsplash.com/random?survey)',
        }}
      >
        <Box
          sx={{
            position: 'absolute',
            top: 0,
            bottom: 0,
            right: 0,
            left: 0,
            backgroundColor: 'rgba(0,0,0,.3)',
          }}
        />
        <Grid container>
          <Grid item md={6}>
            <Box
              sx={{
                position: 'relative',
                p: { xs: 3, md: 6 },
                pr: { md: 0 },
              }}
            >
              <Typography variant="h2" color="inherit" gutterBottom>
                Survey Application
              </Typography>
              <Typography variant="h5" color="inherit" paragraph>
                Create, share, and analyze surveys with ease. Get valuable insights from your audience.
              </Typography>
              <Box sx={{ mt: 4 }}>
                <Button
                  variant="contained"
                  size="large"
                  component={RouterLink}
                  to="/surveys"
                  sx={{ mr: 2 }}
                >
                  Take Surveys
                </Button>
                <Button
                  variant="outlined"
                  size="large"
                  component={RouterLink}
                  to="/login"
                  sx={{ color: 'white', borderColor: 'white' }}
                >
                  Sign In
                </Button>
              </Box>
            </Box>
          </Grid>
        </Grid>
      </Paper>

      {/* Features Section */}
      <Container maxWidth="lg" sx={{ mb: 6 }}>
        <Typography variant="h3" component="h2" gutterBottom align="center">
          Features
        </Typography>
        <Grid container spacing={4}>
          {features.map((feature, index) => (
            <Grid item xs={12} sm={6} md={3} key={index}>
              <Card sx={{ height: '100%', textAlign: 'center' }}>
                <CardContent>
                  <Box sx={{ mb: 2 }}>
                    {feature.icon}
                  </Box>
                  <Typography gutterBottom variant="h5" component="h3">
                    {feature.title}
                  </Typography>
                  <Typography variant="body2" color="text.secondary">
                    {feature.description}
                  </Typography>
                </CardContent>
              </Card>
            </Grid>
          ))}
        </Grid>
      </Container>

      {/* Call to Action */}
      <Box sx={{ bgcolor: 'primary.main', color: 'white', py: 6 }}>
        <Container maxWidth="md">
          <Typography variant="h4" component="h2" gutterBottom align="center">
            Ready to get started?
          </Typography>
          <Typography variant="h6" align="center" paragraph>
            Join thousands of users who are already creating and participating in surveys.
          </Typography>
          <Box sx={{ textAlign: 'center', mt: 4 }}>
            <Button
              variant="contained"
              size="large"
              component={RouterLink}
              to="/surveys"
              sx={{ 
                bgcolor: 'white', 
                color: 'primary.main',
                '&:hover': {
                  bgcolor: 'grey.100',
                }
              }}
            >
              Start Now
            </Button>
          </Box>
        </Container>
      </Box>
    </Box>
  );
};

export default HomePage; 