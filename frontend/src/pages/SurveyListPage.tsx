import React, { useState, useEffect } from 'react';
import {
  Container,
  Typography,
  Box,
  Card,
  CardContent,
  CardActions,
  Button,
  Grid,
  Chip,
  CircularProgress,
  Alert,
  Paper,
} from '@mui/material';
import { Link as RouterLink } from 'react-router-dom';
import { Visibility, Schedule, TrendingUp } from '@mui/icons-material';
import { surveyService } from '../services/surveyService';
import { Survey } from '../types';

const SurveyListPage: React.FC = () => {
  const [surveys, setSurveys] = useState<Survey[]>([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState<string | null>(null);

  useEffect(() => {
    const fetchSurveys = async () => {
      try {
        setLoading(true);
        const activeSurveys = await surveyService.getActiveSurveys();
        setSurveys(activeSurveys);
      } catch (err) {
        setError('Failed to load surveys. Please try again later.');
        console.error('Error fetching surveys:', err);
      } finally {
        setLoading(false);
      }
    };

    fetchSurveys();
  }, []);

  const formatDate = (dateString: string | null) => {
    if (!dateString) return 'Not set';
    return new Date(dateString).toLocaleDateString('en-US', {
      year: 'numeric',
      month: 'short',
      day: 'numeric',
    });
  };

  const getStatusChip = (survey: Survey) => {
    const now = new Date();
    const startDate = survey.startDate ? new Date(survey.startDate) : null;
    const endDate = survey.endDate ? new Date(survey.endDate) : null;

    if (startDate && now < startDate) {
      return <Chip label="Coming Soon" color="warning" size="small" />;
    } else if (endDate && now > endDate) {
      return <Chip label="Ended" color="error" size="small" />;
    } else {
      return <Chip label="Active" color="success" size="small" />;
    }
  };

  if (loading) {
    return (
      <Container maxWidth="lg" sx={{ py: 4 }}>
        <Box display="flex" justifyContent="center" alignItems="center" minHeight="400px">
          <CircularProgress />
        </Box>
      </Container>
    );
  }

  if (error) {
    return (
      <Container maxWidth="lg" sx={{ py: 4 }}>
        <Alert severity="error" sx={{ mb: 2 }}>
          {error}
        </Alert>
      </Container>
    );
  }

  return (
    <Container maxWidth="lg" sx={{ py: 4 }}>
      <Box sx={{ mb: 4 }}>
        <Typography variant="h3" component="h1" gutterBottom>
          Active Surveys
        </Typography>
        <Typography variant="h6" color="text.secondary" paragraph>
          Browse and participate in our active surveys. Your feedback helps us improve!
        </Typography>
      </Box>

      {surveys.length === 0 ? (
        <Paper sx={{ p: 4, textAlign: 'center' }}>
          <Typography variant="h6" color="text.secondary" gutterBottom>
            No active surveys available at the moment.
          </Typography>
          <Typography variant="body2" color="text.secondary">
            Check back later for new surveys or contact an administrator.
          </Typography>
        </Paper>
      ) : (
        <Grid container spacing={3}>
          {surveys.map((survey) => (
            <Grid item xs={12} md={6} lg={4} key={survey.id}>
              <Card sx={{ height: '100%', display: 'flex', flexDirection: 'column' }}>
                <CardContent sx={{ flexGrow: 1 }}>
                  <Box sx={{ display: 'flex', justifyContent: 'space-between', alignItems: 'flex-start', mb: 2 }}>
                    <Typography variant="h6" component="h2" gutterBottom>
                      {survey.title}
                    </Typography>
                    {getStatusChip(survey)}
                  </Box>
                  
                  <Typography variant="body2" color="text.secondary" paragraph>
                    {survey.description}
                  </Typography>

                  <Box sx={{ display: 'flex', alignItems: 'center', mb: 1 }}>
                    <Schedule fontSize="small" sx={{ mr: 1, color: 'text.secondary' }} />
                    <Typography variant="caption" color="text.secondary">
                      {formatDate(survey.startDate)} - {formatDate(survey.endDate)}
                    </Typography>
                  </Box>

                  <Box sx={{ display: 'flex', alignItems: 'center', mb: 1 }}>
                    <TrendingUp fontSize="small" sx={{ mr: 1, color: 'text.secondary' }} />
                    <Typography variant="caption" color="text.secondary">
                      {survey.questions?.length || 0} questions
                    </Typography>
                  </Box>

                  {survey.totalResponses !== undefined && (
                    <Box sx={{ display: 'flex', alignItems: 'center' }}>
                      <Visibility fontSize="small" sx={{ mr: 1, color: 'text.secondary' }} />
                      <Typography variant="caption" color="text.secondary">
                        {survey.totalResponses} responses
                      </Typography>
                    </Box>
                  )}
                </CardContent>

                <CardActions>
                  <Button
                    size="small"
                    component={RouterLink}
                    to={`/surveys/${survey.id}`}
                    variant="contained"
                    fullWidth
                  >
                    Take Survey
                  </Button>
                </CardActions>
              </Card>
            </Grid>
          ))}
        </Grid>
      )}
    </Container>
  );
};

export default SurveyListPage; 