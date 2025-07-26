import React from 'react';
import { useQuery } from '@tanstack/react-query';
import { useForm } from 'react-hook-form';
import toast from 'react-hot-toast';
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
  TextField,
  FormControl,
  InputLabel,
  Select,
  MenuItem,
} from '@mui/material';
import { DataGrid, GridColDef } from '@mui/x-data-grid';
import { Link as RouterLink } from 'react-router-dom';
import { Visibility, Schedule, TrendingUp } from '@mui/icons-material';
import { surveyService } from '../services/surveyService';
import { Survey } from '../types';
import clsx from 'clsx';

interface SurveyListFilters {
  status?: string;
  title?: string;
}

const OptimizedSurveyList: React.FC = () => {
  const { register, handleSubmit, watch } = useForm<SurveyListFilters>();
  const filters = watch();

  // Use React Query for data fetching with caching
  const {
    data: surveys = [],
    isLoading,
    error,
    refetch
  } = useQuery({
    queryKey: ['surveys', filters],
    queryFn: () => surveyService.getActiveSurveys(),
    staleTime: 5 * 60 * 1000, // 5 minutes
    gcTime: 10 * 60 * 1000, // 10 minutes (renamed from cacheTime)
    retry: 3
  });

  // Handle error with useEffect
  React.useEffect(() => {
    if (error) {
      toast.error('Failed to load surveys');
      console.error('Error fetching surveys:', error);
    }
  }, [error]);

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

  // DataGrid columns configuration
  const columns: GridColDef[] = [
    {
      field: 'title',
      headerName: 'Survey Title',
      flex: 1,
      renderCell: (params) => (
        <Box>
          <Typography variant="subtitle2" fontWeight="bold">
            {params.value}
          </Typography>
          <Typography variant="caption" color="text.secondary">
            {params.row.description}
          </Typography>
        </Box>
      )
    },
    {
      field: 'status',
      headerName: 'Status',
      width: 120,
      renderCell: (params) => getStatusChip(params.row)
    },
    {
      field: 'startDate',
      headerName: 'Duration',
      width: 200,
      renderCell: (params) => (
        <Box sx={{ display: 'flex', alignItems: 'center' }}>
          <Schedule fontSize="small" sx={{ mr: 1, color: 'text.secondary' }} />
          <Typography variant="caption">
            {formatDate(params.value)} - {formatDate(params.row.endDate)}
          </Typography>
        </Box>
      )
    },
    {
      field: 'totalResponses',
      headerName: 'Responses',
      width: 120,
      renderCell: (params) => (
        <Box sx={{ display: 'flex', alignItems: 'center' }}>
          <TrendingUp fontSize="small" sx={{ mr: 1, color: 'text.secondary' }} />
          <Typography variant="caption">
            {params.value || 0}
          </Typography>
        </Box>
      )
    },
    {
      field: 'actions',
      headerName: 'Actions',
      width: 120,
      sortable: false,
      renderCell: (params) => (
        <Button
          size="small"
          component={RouterLink}
          to={`/surveys/${params.row.id}`}
          variant="contained"
        >
          Take Survey
        </Button>
      )
    }
  ];

  const handleFiltersSubmit = (data: SurveyListFilters) => {
    toast.success('Filters applied');
    refetch();
  };

  if (isLoading) {
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
          Failed to load surveys. Please try again later.
        </Alert>
        <Button variant="contained" onClick={() => refetch()}>
          Retry
        </Button>
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

      {/* Filters */}
      <Paper sx={{ p: 3, mb: 3 }}>
        <Box component="form" onSubmit={handleSubmit(handleFiltersSubmit)}>
          <Grid container spacing={2} alignItems="center">
            <Grid item xs={12} md={4}>
              <TextField
                fullWidth
                label="Search by title"
                {...register('title')}
                placeholder="Enter survey title..."
              />
            </Grid>
            <Grid item xs={12} md={4}>
              <FormControl fullWidth>
                <InputLabel>Status</InputLabel>
                <Select {...register('status')} label="Status">
                  <MenuItem value="">All</MenuItem>
                  <MenuItem value="ACTIVE">Active</MenuItem>
                  <MenuItem value="SCHEDULED">Scheduled</MenuItem>
                  <MenuItem value="CLOSED">Closed</MenuItem>
                </Select>
              </FormControl>
            </Grid>
            <Grid item xs={12} md={4}>
              <Button
                type="submit"
                variant="contained"
                fullWidth
                sx={{ height: 56 }}
              >
                Apply Filters
              </Button>
            </Grid>
          </Grid>
        </Box>
      </Paper>

      {/* Survey List */}
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
        <Box sx={{ height: 600, width: '100%' }}>
          <DataGrid
            rows={surveys}
            columns={columns}
            initialState={{
              pagination: {
                paginationModel: { page: 0, pageSize: 10 },
              },
            }}
            pageSizeOptions={[5, 10, 25]}
            disableRowSelectionOnClick
            disableColumnMenu
            sx={{
              '& .MuiDataGrid-cell': {
                borderBottom: '1px solid #e0e0e0',
              },
              '& .MuiDataGrid-columnHeaders': {
                backgroundColor: '#f5f5f5',
                borderBottom: '2px solid #e0e0e0',
              },
            }}
          />
        </Box>
      )}

      {/* Alternative Card View */}
      <Box sx={{ mt: 4 }}>
        <Typography variant="h6" gutterBottom>
          Card View
        </Typography>
        <Grid container spacing={3}>
          {surveys.map((survey) => (
            <Grid item xs={12} md={6} lg={4} key={survey.id}>
              <Card 
                sx={{ 
                  height: '100%', 
                  display: 'flex', 
                  flexDirection: 'column',
                  transition: 'transform 0.2s ease-in-out',
                  '&:hover': {
                    transform: 'translateY(-4px)',
                    boxShadow: 4,
                  }
                }}
              >
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
      </Box>
    </Container>
  );
};

export default OptimizedSurveyList; 