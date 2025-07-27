import React, { useState, useEffect } from 'react';
import { useParams, useNavigate } from 'react-router-dom';
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

const AdminSurveyEditPage: React.FC = () => {
  const { id } = useParams<{ id: string }>();
  const navigate = useNavigate();
  const { isAdmin } = useAuth();
  const [loading, setLoading] = useState(true);
  const [saving, setSaving] = useState(false);
  const [error, setError] = useState<string | null>(null);

  const [surveyData, setSurveyData] = useState<SurveyFormData>({
    title: '',
    description: '',
    status: 'DRAFT',
    startDate: null,
    endDate: null,
    allowMultipleResponses: false,
    requireAuthentication: true,
  });

  const [questions, setQuestions] = useState<QuestionFormData[]>([]);

  const questionTypes = [
    { value: 'TEXT', label: 'Text Response' },
    { value: 'MULTIPLE_CHOICE', label: 'Multiple Choice' },
    { value: 'RATING', label: 'Rating (1-5)' },
    { value: 'YES_NO', label: 'Yes/No' },
    { value: 'LIKERT_SCALE', label: 'Likert Scale (1-5)' },
  ];

  useEffect(() => {
    if (!isAdmin) {
              navigate('/login');
      return;
    }

    const fetchSurvey = async () => {
      if (!id) return;
      
      try {
        setLoading(true);
        const survey = await adminService.getSurveyById(parseInt(id));
        
        setSurveyData({
          title: survey.title,
          description: survey.description,
          status: survey.status,
          startDate: survey.startDate,
          endDate: survey.endDate,
          allowMultipleResponses: survey.allowMultipleResponses,
          requireAuthentication: survey.requireAuthentication,
        });

        setQuestions(survey.questions.map(q => ({
          text: q.text,
          type: q.type,
          required: q.required,
          orderIndex: q.orderIndex,
          maxResponses: q.maxResponses,
          allowMultipleAnswers: q.allowMultipleAnswers,
        })));
      } catch (err) {
        setError('Failed to load survey. Please try again later.');
        console.error('Error fetching survey:', err);
      } finally {
        setLoading(false);
      }
    };

    fetchSurvey();
  }, [id, isAdmin, navigate]);

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

    if (!id) return;

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
      setSaving(true);
      setError(null);

      await adminService.updateSurvey(parseInt(id), {
        ...surveyData,
        questions: questions.map(q => ({
          ...q,
          text: q.text.trim()
        }))
      });

      alert('Survey updated successfully!');
      navigate('/admin/dashboard');
    } catch (err: any) {
      setError(err.message || 'Failed to update survey. Please try again.');
    } finally {
      setSaving(false);
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
        <Button variant="contained" onClick={() => navigate('/admin/dashboard')}>
          Back to Dashboard
        </Button>
      </Container>
    );
  }

  return (
    <Container maxWidth="lg" sx={{ py: 4 }}>
      <Box sx={{ mb: 4 }}>
        <Typography variant="h3" component="h1" gutterBottom>
          Edit Survey
        </Typography>
        <Typography variant="h6" color="text.secondary">
          Modify survey details, questions, and settings
        </Typography>
      </Box>

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
                  disabled={saving}
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
                  disabled={saving}
                />
              </Grid>

              <Grid item xs={12} md={6}>
                <FormControl fullWidth>
                  <InputLabel>Status</InputLabel>
                  <Select
                    value={surveyData.status}
                    onChange={(e) => handleSurveyChange('status', e.target.value)}
                    label="Status"
                    disabled={saving}
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
                  disabled={saving}
                />
              </Grid>

              <Grid item xs={12} md={6}>
                <DateTimePicker
                  label="End Date"
                  value={surveyData.endDate ? new Date(surveyData.endDate) : null}
                  onChange={(date) => handleSurveyChange('endDate', date?.toISOString() || null)}
                  disabled={saving}
                />
              </Grid>

              <Grid item xs={12} md={6}>
                <Box sx={{ display: 'flex', flexDirection: 'column', gap: 2 }}>
                  <FormControlLabel
                    control={
                      <Switch
                        checked={surveyData.requireAuthentication}
                        onChange={(e) => handleSurveyChange('requireAuthentication', e.target.checked)}
                        disabled={saving}
                      />
                    }
                    label="Require user authentication"
                  />
                  <FormControlLabel
                    control={
                      <Switch
                        checked={surveyData.allowMultipleResponses}
                        onChange={(e) => handleSurveyChange('allowMultipleResponses', e.target.checked)}
                        disabled={saving}
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
                disabled={saving}
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
                        disabled={index === 0 || saving}
                      >
                        ↑
                      </IconButton>
                      <IconButton
                        size="small"
                        onClick={() => moveQuestion(index, 'down')}
                        disabled={index === questions.length - 1 || saving}
                      >
                        ↓
                      </IconButton>
                      <IconButton
                        size="small"
                        onClick={() => removeQuestion(index)}
                        disabled={saving}
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
                          disabled={saving}
                        />
                      </Grid>

                      <Grid item xs={12} md={6}>
                        <FormControl fullWidth>
                          <InputLabel>Question Type</InputLabel>
                          <Select
                            value={question.type}
                            onChange={(e) => updateQuestion(index, 'type', e.target.value)}
                            label="Question Type"
                            disabled={saving}
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
                              disabled={saving}
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
            disabled={saving}
          >
            Cancel
          </Button>
          <Button
            type="submit"
            variant="contained"
            startIcon={saving ? <CircularProgress size={20} /> : <Save />}
            disabled={saving}
          >
            {saving ? 'Saving...' : 'Save Changes'}
          </Button>
        </Box>
      </Box>
    </Container>
  );
};

export default AdminSurveyEditPage; 