import React, { useState, useEffect } from 'react';
import { useNavigate, Link as RouterLink } from 'react-router-dom';
import {
  Container,
  Typography,
  Box,
  Grid,
  Card,
  CardContent,
  Button,
  CircularProgress,
  Alert,
  List,
  ListItem,
  ListItemText,
  ListItemIcon,
  Chip,
  IconButton,
} from '@mui/material';
import {
  Add,
  Assessment,
  TrendingUp,
  People,
  Schedule,
  Edit,
  Delete,
  Visibility,
  Download,
} from '@mui/icons-material';
import {
  BarChart,
  Bar,
  XAxis,
  YAxis,
  CartesianGrid,
  Tooltip,
  ResponsiveContainer,
  PieChart,
  Pie,
  Cell,
} from 'recharts';
import { useAuth } from '../context/AuthContext';
import { adminService } from '../services/adminService';
import { Survey, SurveyStatistics, ResponseStatistics, UserStatistics } from '../types';

const AdminDashboardPage: React.FC = () => {
  const navigate = useNavigate();
  const { isAdmin } = useAuth();
  const [surveys, setSurveys] = useState<Survey[]>([]);
  const [statistics, setStatistics] = useState<{
    surveyStats: SurveyStatistics;
    responseStats: ResponseStatistics;
    userStats: UserStatistics;
  } | null>(null);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState<string | null>(null);

  useEffect(() => {
    if (!isAdmin) {
              navigate('/login');
      return;
    }

    const fetchDashboardData = async () => {
      try {
        setLoading(true);
        const [surveysData, statsData] = await Promise.all([
          adminService.getAllSurveys(),
          adminService.getAnalytics()
        ]);
        setSurveys(surveysData);
        setStatistics(statsData);
      } catch (err) {
        setError('Failed to load dashboard data. Please try again later.');
        console.error('Error fetching dashboard data:', err);
      } finally {
        setLoading(false);
      }
    };

    fetchDashboardData();
  }, [isAdmin, navigate]);

  const handleDeleteSurvey = async (surveyId: number) => {
    if (!window.confirm('Are you sure you want to delete this survey? This action cannot be undone.')) {
      return;
    }

    try {
      await adminService.deleteSurvey(surveyId);
      setSurveys(prev => prev.filter(survey => survey.id !== surveyId));
    } catch (err) {
      console.error('Error deleting survey:', err);
      alert('Failed to delete survey. Please try again.');
    }
  };

  const handleExportSurvey = async (surveyId: number) => {
    try {
      const blob = await adminService.exportSurveyData(surveyId);
      const url = window.URL.createObjectURL(blob);
      const a = document.createElement('a');
      a.href = url;
      a.download = `survey-${surveyId}-data.csv`;
      document.body.appendChild(a);
      a.click();
      window.URL.revokeObjectURL(url);
      document.body.removeChild(a);
    } catch (err) {
      console.error('Error exporting survey:', err);
      alert('Failed to export survey data. Please try again.');
    }
  };

  const formatDate = (dateString: string | null) => {
    if (!dateString) return 'Not set';
    return new Date(dateString).toLocaleDateString('en-US', {
      year: 'numeric',
      month: 'short',
      day: 'numeric',
    });
  };

  const getStatusColor = (status: string) => {
    switch (status) {
      case 'ACTIVE': return 'success';
      case 'DRAFT': return 'warning';
      case 'SCHEDULED': return 'info';
      case 'CLOSED': return 'error';
      default: return 'default';
    }
  };

  const chartData = statistics ? [
    { name: 'Active', value: statistics.surveyStats.activeSurveys, color: '#4caf50' },
    { name: 'Draft', value: statistics.surveyStats.draftSurveys, color: '#ff9800' },
    { name: 'Closed', value: statistics.surveyStats.closedSurveys, color: '#f44336' },
  ] : [];

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
      {/* Header */}
      <Box sx={{ mb: 4 }}>
        <Box sx={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center', mb: 2 }}>
          <Typography variant="h3" component="h1">
            Admin Dashboard
          </Typography>
          <Button
            variant="contained"
            startIcon={<Add />}
            component={RouterLink}
            to="/admin/surveys/create"
          >
            Create Survey
          </Button>
        </Box>
        <Typography variant="h6" color="text.secondary">
          Manage surveys, view analytics, and monitor user engagement
        </Typography>
      </Box>

      {/* Statistics Cards */}
      {statistics && (
        <Grid container spacing={3} sx={{ mb: 4 }}>
          <Grid item xs={12} sm={6} md={3}>
            <Card>
              <CardContent>
                <Box sx={{ display: 'flex', alignItems: 'center' }}>
                  <Assessment sx={{ fontSize: 40, color: 'primary.main', mr: 2 }} />
                  <Box>
                    <Typography variant="h4" component="div">
                      {statistics.surveyStats.totalSurveys}
                    </Typography>
                    <Typography variant="body2" color="text.secondary">
                      Total Surveys
                    </Typography>
                  </Box>
                </Box>
              </CardContent>
            </Card>
          </Grid>

          <Grid item xs={12} sm={6} md={3}>
            <Card>
              <CardContent>
                <Box sx={{ display: 'flex', alignItems: 'center' }}>
                  <TrendingUp sx={{ fontSize: 40, color: 'success.main', mr: 2 }} />
                  <Box>
                    <Typography variant="h4" component="div">
                      {statistics.responseStats.totalResponses}
                    </Typography>
                    <Typography variant="body2" color="text.secondary">
                      Total Responses
                    </Typography>
                  </Box>
                </Box>
              </CardContent>
            </Card>
          </Grid>

          <Grid item xs={12} sm={6} md={3}>
            <Card>
              <CardContent>
                <Box sx={{ display: 'flex', alignItems: 'center' }}>
                  <People sx={{ fontSize: 40, color: 'info.main', mr: 2 }} />
                  <Box>
                    <Typography variant="h4" component="div">
                      {statistics.userStats.totalUsers}
                    </Typography>
                    <Typography variant="body2" color="text.secondary">
                      Total Users
                    </Typography>
                  </Box>
                </Box>
              </CardContent>
            </Card>
          </Grid>

          <Grid item xs={12} sm={6} md={3}>
            <Card>
              <CardContent>
                <Box sx={{ display: 'flex', alignItems: 'center' }}>
                  <Schedule sx={{ fontSize: 40, color: 'warning.main', mr: 2 }} />
                  <Box>
                    <Typography variant="h4" component="div">
                      {statistics.surveyStats.activeSurveys}
                    </Typography>
                    <Typography variant="body2" color="text.secondary">
                      Active Surveys
                    </Typography>
                  </Box>
                </Box>
              </CardContent>
            </Card>
          </Grid>
        </Grid>
      )}

      {/* Charts */}
      <Grid container spacing={3} sx={{ mb: 4 }}>
        <Grid item xs={12} md={6}>
          <Card>
            <CardContent>
              <Typography variant="h6" gutterBottom>
                Survey Status Distribution
              </Typography>
              <ResponsiveContainer width="100%" height={300}>
                <PieChart>
                  <Pie
                    data={chartData}
                    cx="50%"
                    cy="50%"
                    labelLine={false}
                    label={({ name, percent }) => `${name} ${(percent * 100).toFixed(0)}%`}
                    outerRadius={80}
                    fill="#8884d8"
                    dataKey="value"
                  >
                    {chartData.map((entry, index) => (
                      <Cell key={`cell-${index}`} fill={entry.color} />
                    ))}
                  </Pie>
                  <Tooltip />
                </PieChart>
              </ResponsiveContainer>
            </CardContent>
          </Card>
        </Grid>

        <Grid item xs={12} md={6}>
          <Card>
            <CardContent>
              <Typography variant="h6" gutterBottom>
                Response Trends
              </Typography>
              <ResponsiveContainer width="100%" height={300}>
                <BarChart data={surveys.slice(0, 5).map(survey => ({
                  name: survey.title.substring(0, 15) + '...',
                  responses: survey.totalResponses
                }))}>
                  <CartesianGrid strokeDasharray="3 3" />
                  <XAxis dataKey="name" />
                  <YAxis />
                  <Tooltip />
                  <Bar dataKey="responses" fill="#8884d8" />
                </BarChart>
              </ResponsiveContainer>
            </CardContent>
          </Card>
        </Grid>
      </Grid>

      {/* Recent Surveys */}
      <Card>
        <CardContent>
          <Box sx={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center', mb: 2 }}>
            <Typography variant="h6">
              Recent Surveys
            </Typography>
            <Button
              variant="outlined"
              component={RouterLink}
              to="/admin/surveys"
            >
              View All
            </Button>
          </Box>

          <List>
            {surveys.slice(0, 5).map((survey) => (
              <ListItem
                key={survey.id}
                divider
                secondaryAction={
                  <Box>
                    <IconButton
                      component={RouterLink}
                      to={`/surveys/${survey.id}`}
                      size="small"
                    >
                      <Visibility />
                    </IconButton>
                    <IconButton
                      component={RouterLink}
                      to={`/admin/surveys/${survey.id}/edit`}
                      size="small"
                    >
                      <Edit />
                    </IconButton>
                    <IconButton
                      onClick={() => handleExportSurvey(survey.id)}
                      size="small"
                    >
                      <Download />
                    </IconButton>
                    <IconButton
                      onClick={() => handleDeleteSurvey(survey.id)}
                      size="small"
                      color="error"
                    >
                      <Delete />
                    </IconButton>
                  </Box>
                }
              >
                <ListItemIcon>
                  <Assessment />
                </ListItemIcon>
                <ListItemText
                  primary={survey.title}
                  secondary={
                    <Box sx={{ display: 'flex', alignItems: 'center', gap: 1 }}>
                      <Chip
                        label={survey.status}
                        color={getStatusColor(survey.status) as any}
                        size="small"
                      />
                      <Typography variant="caption">
                        {survey.totalResponses} responses
                      </Typography>
                      <Typography variant="caption">
                        • {formatDate(survey.startDate)} - {formatDate(survey.endDate)}
                      </Typography>
                    </Box>
                  }
                />
              </ListItem>
            ))}
          </List>
        </CardContent>
      </Card>
    </Container>
  );
};

export default AdminDashboardPage; 