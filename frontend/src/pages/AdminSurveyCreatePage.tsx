import React, { useState } from 'react';
import { useNavigate } from 'react-router-dom';
import {
  Container,
  Typography,
  Box,
  Card,
  CardContent,
  TextField,
  Button,
  Grid,
  FormControlLabel,
  Checkbox,
  Switch,
  FormControl,
  InputLabel,
  Select,
  MenuItem,
  IconButton,
  List,
  ListItem,

  Paper,
  Alert,
  CircularProgress,
  Divider,
} from '@mui/material';
import {
  Add,
  Delete,
  DragIndicator,
  Save,
} from '@mui/icons-material';
import { DateTimePicker } from '@mui/x-date-pickers/DateTimePicker';
import { useAuth } from '../context/AuthContext';
import { adminService } from '../services/adminService';
import { SurveyFormData, QuestionFormData } from '../types';

const AdminSurveyCreatePage: React.FC = () => {
  const navigate = useNavigate();
  const { isAdmin } = useAuth();
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState<string | null>(null);

  const [surveyData, setSurveyData] = useState<SurveyFormData>({
    title: '',
    description: '',
    status: 'DRAFT',
    startDate: null,
    endDate: null,
    anonymous: false,
    allowMultipleResponses: false,
  });

  const [questions, setQuestions] = useState<QuestionFormData[]>([]);

  const questionTypes = [
    { value: 'TEXT', label: 'Text Response' },
    { value: 'MULTIPLE_CHOICE', label: 'Multiple Choice' },
    { value: 'RATING', label: 'Rating (1-5)' },
    { value: 'YES_NO', label: 'Yes/No' },
    { value: 'LIKERT_SCALE', label: 'Likert Scale (1-5)' },
  ];

  const handleSurveyChange = (field: keyof SurveyFormData, value: any) => {
    setSurveyData(prev => ({
      ...prev,
      [field]: value
    }));
  };

  const addQuestion = () => {
    const newQuestion: QuestionFormData = {
      text: '',
      type: 'TEXT',
      required: false,
      orderIndex: questions.length + 1,
      maxResponses: null,
      allowMultipleAnswers: false,
    };
    setQuestions(prev => [...prev, newQuestion]);
  };

  const updateQuestion = (index: number, field: keyof QuestionFormData, value: any) => {
    setQuestions(prev => prev.map((q, i) => 
      i === index ? { ...q, [field]: value } : q
    ));
  };

  const removeQuestion = (index: number) => {
    setQuestions(prev => prev.filter((_, i) => i !== index));
  };

  const moveQuestion = (index: number, direction: 'up' | 'down') => {
    if (
      (direction === 'up' && index === 0) ||
      (direction === 'down' && index === questions.length - 1)
    ) {
      return;
    }

    setQuestions(prev => {
      const newQuestions = [...prev];
      const targetIndex = direction === 'up' ? index - 1 : index + 1;
      [newQuestions[index], newQuestions[targetIndex]] = [newQuestions[targetIndex], newQuestions[index]];
      
      // Update orderIndex
      return newQuestions.map((q, i) => ({ ...q, orderIndex: i + 1 }));
    });
  };

  const handleSubmit = async (e: React.FormEvent) => {
    e.preventDefault();

    if (!surveyData.title.trim()) {
      setError('Survey title is required');
      return;
    }

    if (questions.length === 0) {
      setError('At least one question is required');
      return;
    }

    const invalidQuestions = questions.filter(q => !q.text.trim());
    if (invalidQuestions.length > 0) {
      setError('All questions must have text');
      return;
    }

    try {
      setLoading(true);
      setError(null);

      await adminService.createSurvey({
        ...surveyData,
        questions: questions.map(q => ({
          ...q,
          text: q.text.trim()
        }))
      });

      alert('Survey created successfully!');
      navigate('/admin/dashboard');
    } catch (err: any) {
      setError(err.message || 'Failed to create survey. Please try again.');
    } finally {
      setLoading(false);
    }
  };

  if (!isAdmin) {
    navigate('/admin/login');
    return null;
  }

  return (
    <Container maxWidth="lg" sx={{ py: 4 }}>
      <Box sx={{ mb: 4 }}>
        <Typography variant="h3" component="h1" gutterBottom>
          Create New Survey
        </Typography>
        <Typography variant="h6" color="text.secondary">
          Design and configure your survey with questions and settings
        </Typography>
      </Box>

      {error && (
        <Alert severity="error" sx={{ mb: 3 }}>
          {error}
        </Alert>
      )}

      <Box component="form" onSubmit={handleSubmit}>
        {/* Survey Details */}
        <Card sx={{ mb: 3 }}>
          <CardContent>
            <Typography variant="h6" gutterBottom>
              Survey Details
            </Typography>
            
            <Grid container spacing={3}>
              <Grid item xs={12}>
                <TextField
                  fullWidth
                  label="Survey Title"
                  value={surveyData.title}
                  onChange={(e) => handleSurveyChange('title', e.target.value)}
                  required
                  disabled={loading}
                />
              </Grid>

              <Grid item xs={12}>
                <TextField
                  fullWidth
                  label="Description"
                  value={surveyData.description}
                  onChange={(e) => handleSurveyChange('description', e.target.value)}
                  multiline
                  rows={3}
                  disabled={loading}
                />
              </Grid>

              <Grid item xs={12} md={6}>
                <FormControl fullWidth>
                  <InputLabel>Status</InputLabel>
                  <Select
                    value={surveyData.status}
                    onChange={(e) => handleSurveyChange('status', e.target.value)}
                    label="Status"
                    disabled={loading}
                  >
                    <MenuItem value="DRAFT">Draft</MenuItem>
                    <MenuItem value="SCHEDULED">Scheduled</MenuItem>
                    <MenuItem value="ACTIVE">Active</MenuItem>
                    <MenuItem value="CLOSED">Closed</MenuItem>
                  </Select>
                </FormControl>
              </Grid>

              <Grid item xs={12} md={6}>
                <DateTimePicker
                  label="Start Date"
                  value={surveyData.startDate ? new Date(surveyData.startDate) : null}
                  onChange={(date) => handleSurveyChange('startDate', date?.toISOString() || null)}
                  disabled={loading}
                />
              </Grid>

              <Grid item xs={12} md={6}>
                <DateTimePicker
                  label="End Date"
                  value={surveyData.endDate ? new Date(surveyData.endDate) : null}
                  onChange={(date) => handleSurveyChange('endDate', date?.toISOString() || null)}
                  disabled={loading}
                />
              </Grid>

              <Grid item xs={12} md={6}>
                <Box sx={{ display: 'flex', flexDirection: 'column', gap: 2 }}>
                  <FormControlLabel
                    control={
                      <Switch
                        checked={surveyData.anonymous}
                        onChange={(e) => handleSurveyChange('anonymous', e.target.checked)}
                        disabled={loading}
                      />
                    }
                    label="Allow anonymous responses"
                  />
                  <FormControlLabel
                    control={
                      <Switch
                        checked={surveyData.allowMultipleResponses}
                        onChange={(e) => handleSurveyChange('allowMultipleResponses', e.target.checked)}
                        disabled={loading}
                      />
                    }
                    label="Allow multiple responses per user"
                  />
                </Box>
              </Grid>
            </Grid>
          </CardContent>
        </Card>

        {/* Questions */}
        <Card sx={{ mb: 3 }}>
          <CardContent>
            <Box sx={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center', mb: 2 }}>
              <Typography variant="h6">
                Questions ({questions.length})
              </Typography>
              <Button
                variant="outlined"
                startIcon={<Add />}
                onClick={addQuestion}
                disabled={loading}
              >
                Add Question
              </Button>
            </Box>

            {questions.length === 0 ? (
              <Paper sx={{ p: 3, textAlign: 'center' }}>
                <Typography variant="body2" color="text.secondary">
                  No questions added yet. Click "Add Question" to get started.
                </Typography>
              </Paper>
            ) : (
              <List>
                {questions.map((question, index) => (
                  <ListItem key={index} sx={{ flexDirection: 'column', alignItems: 'stretch' }}>
                    <Box sx={{ display: 'flex', alignItems: 'center', width: '100%', mb: 2 }}>
                      <DragIndicator sx={{ mr: 1, color: 'text.secondary' }} />
                      <Typography variant="subtitle2" sx={{ mr: 2 }}>
                        Question {index + 1}
                      </Typography>
                      <Box sx={{ flexGrow: 1 }} />
                      <IconButton
                        size="small"
                        onClick={() => moveQuestion(index, 'up')}
                        disabled={index === 0 || loading}
                      >
                        ↑
                      </IconButton>
                      <IconButton
                        size="small"
                        onClick={() => moveQuestion(index, 'down')}
                        disabled={index === questions.length - 1 || loading}
                      >
                        ↓
                      </IconButton>
                      <IconButton
                        size="small"
                        onClick={() => removeQuestion(index)}
                        disabled={loading}
                        color="error"
                      >
                        <Delete />
                      </IconButton>
                    </Box>

                    <Grid container spacing={2}>
                      <Grid item xs={12}>
                        <TextField
                          fullWidth
                          label="Question Text"
                          value={question.text}
                          onChange={(e) => updateQuestion(index, 'text', e.target.value)}
                          required
                          disabled={loading}
                        />
                      </Grid>

                      <Grid item xs={12} md={6}>
                        <FormControl fullWidth>
                          <InputLabel>Question Type</InputLabel>
                          <Select
                            value={question.type}
                            onChange={(e) => updateQuestion(index, 'type', e.target.value)}
                            label="Question Type"
                            disabled={loading}
                          >
                            {questionTypes.map(type => (
                              <MenuItem key={type.value} value={type.value}>
                                {type.label}
                              </MenuItem>
                            ))}
                          </Select>
                        </FormControl>
                      </Grid>

                      <Grid item xs={12} md={6}>
                        <FormControlLabel
                          control={
                            <Checkbox
                              checked={question.required}
                              onChange={(e) => updateQuestion(index, 'required', e.target.checked)}
                              disabled={loading}
                            />
                          }
                          label="Required"
                        />
                      </Grid>
                    </Grid>

                    {index < questions.length - 1 && <Divider sx={{ mt: 2 }} />}
                  </ListItem>
                ))}
              </List>
            )}
          </CardContent>
        </Card>

        {/* Actions */}
        <Box sx={{ display: 'flex', gap: 2, justifyContent: 'flex-end' }}>
          <Button
            variant="outlined"
            onClick={() => navigate('/admin/dashboard')}
            disabled={loading}
          >
            Cancel
          </Button>
          <Button
            type="submit"
            variant="contained"
            startIcon={loading ? <CircularProgress size={20} /> : <Save />}
            disabled={loading}
          >
            {loading ? 'Creating...' : 'Create Survey'}
          </Button>
        </Box>
      </Box>
    </Container>
  );
};

export default AdminSurveyCreatePage; 