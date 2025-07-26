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
  Chip,
  CircularProgress,
  Alert,
  Paper,
  List,
  ListItem,
  ListItemText,
  IconButton,
  FormControlLabel,
  Checkbox,
  Rating,
  Radio,
  RadioGroup,
  Accordion,
  AccordionSummary,
  AccordionDetails,
} from '@mui/material';
import {
  ThumbUp,
  ThumbUpOutlined,
  ExpandMore,
  Schedule,
  Person,
  TrendingUp,
} from '@mui/icons-material';
import { surveyService } from '../services/surveyService';
import { Survey, Question } from '../types';

const SurveyDetailPage: React.FC = () => {
  const { id } = useParams<{ id: string }>();
  const navigate = useNavigate();
  const [survey, setSurvey] = useState<Survey | null>(null);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState<string | null>(null);
  const [responses, setResponses] = useState<{ [questionId: number]: string }>({});
  const [anonymous, setAnonymous] = useState(false);
  const [submitting, setSubmitting] = useState(false);

  useEffect(() => {
    const fetchSurvey = async () => {
      if (!id) return;
      
      try {
        setLoading(true);
        const surveyData = await surveyService.getSurveyById(parseInt(id));
        setSurvey(surveyData);
      } catch (err) {
        setError('Failed to load survey. Please try again later.');
        console.error('Error fetching survey:', err);
      } finally {
        setLoading(false);
      }
    };

    fetchSurvey();
  }, [id]);

  const handleResponseChange = (questionId: number, value: string) => {
    setResponses(prev => ({
      ...prev,
      [questionId]: value
    }));
  };

  const handleUpvote = async (responseId: number) => {
    if (!survey) return;
    
    try {
      await surveyService.upvoteResponse(responseId);
      // Refresh survey data to get updated upvote counts
      const updatedSurvey = await surveyService.getSurveyById(survey.id);
      setSurvey(updatedSurvey);
    } catch (err) {
      console.error('Error upvoting response:', err);
    }
  };

  const handleSubmit = async () => {
    if (!survey) return;
    
    try {
      setSubmitting(true);
      
      // Submit responses for each question
      for (const [questionId, responseText] of Object.entries(responses)) {
        if (typeof responseText === 'string' && responseText.trim()) {
          await surveyService.submitResponse(parseInt(questionId), {
            text: responseText,
            anonymous
          });
        }
      }
      
      // Refresh survey data
      const updatedSurvey = await surveyService.getSurveyById(survey.id);
      setSurvey(updatedSurvey);
      setResponses({});
      
      alert('Your responses have been submitted successfully!');
    } catch (err) {
      console.error('Error submitting responses:', err);
      alert('Failed to submit responses. Please try again.');
    } finally {
      setSubmitting(false);
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

  const renderQuestionInput = (question: Question) => {
    const value = responses[question.id] || '';

    switch (question.type) {
      case 'TEXT':
        return (
          <TextField
            fullWidth
            multiline
            rows={3}
            variant="outlined"
            placeholder="Enter your response..."
            value={value}
            onChange={(e) => handleResponseChange(question.id, e.target.value)}
            required={question.required}
          />
        );

      case 'RATING':
        return (
          <Rating
            value={parseInt(value) || 0}
            onChange={(_, newValue) => handleResponseChange(question.id, newValue?.toString() || '')}
            max={5}
            size="large"
          />
        );

      case 'YES_NO':
        return (
          <RadioGroup
            value={value}
            onChange={(e) => handleResponseChange(question.id, e.target.value)}
          >
            <FormControlLabel value="yes" control={<Radio />} label="Yes" />
            <FormControlLabel value="no" control={<Radio />} label="No" />
          </RadioGroup>
        );

      case 'LIKERT_SCALE':
        return (
          <RadioGroup
            value={value}
            onChange={(e) => handleResponseChange(question.id, e.target.value)}
            row
          >
            <FormControlLabel value="1" control={<Radio />} label="1" />
            <FormControlLabel value="2" control={<Radio />} label="2" />
            <FormControlLabel value="3" control={<Radio />} label="3" />
            <FormControlLabel value="4" control={<Radio />} label="4" />
            <FormControlLabel value="5" control={<Radio />} label="5" />
          </RadioGroup>
        );

      default:
        return (
          <TextField
            fullWidth
            variant="outlined"
            placeholder="Enter your response..."
            value={value}
            onChange={(e) => handleResponseChange(question.id, e.target.value)}
            required={question.required}
          />
        );
    }
  };

  const renderTopResponses = (question: Question) => {
    if (!question.topResponses || question.topResponses.length === 0) {
      return (
        <Typography variant="body2" color="text.secondary">
          No responses yet. Be the first to respond!
        </Typography>
      );
    }

    return (
      <List dense>
        {question.topResponses.map((response) => (
          <ListItem key={response.id} sx={{ px: 0 }}>
            <ListItemText
              primary={response.text}
              secondary={
                <Box sx={{ display: 'flex', alignItems: 'center', mt: 1 }}>
                  <Typography variant="caption" color="text.secondary">
                    {response.anonymous ? 'Anonymous' : response.authorName}
                  </Typography>
                  <Box sx={{ ml: 2, display: 'flex', alignItems: 'center' }}>
                    <IconButton
                      size="small"
                      onClick={() => handleUpvote(response.id)}
                      color={response.hasUserUpvoted ? 'primary' : 'default'}
                    >
                      {response.hasUserUpvoted ? <ThumbUp /> : <ThumbUpOutlined />}
                    </IconButton>
                    <Typography variant="caption" sx={{ ml: 0.5 }}>
                      {response.upvoteCount}
                    </Typography>
                  </Box>
                </Box>
              }
            />
          </ListItem>
        ))}
      </List>
    );
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

  if (error || !survey) {
    return (
      <Container maxWidth="lg" sx={{ py: 4 }}>
        <Alert severity="error" sx={{ mb: 2 }}>
          {error || 'Survey not found'}
        </Alert>
        <Button variant="contained" onClick={() => navigate('/surveys')}>
          Back to Surveys
        </Button>
      </Container>
    );
  }

  return (
    <Container maxWidth="lg" sx={{ py: 4 }}>
      {/* Survey Header */}
      <Paper sx={{ p: 3, mb: 3 }}>
        <Typography variant="h4" component="h1" gutterBottom>
          {survey.title}
        </Typography>
        <Typography variant="body1" color="text.secondary" paragraph>
          {survey.description}
        </Typography>
        
        <Box sx={{ display: 'flex', flexWrap: 'wrap', gap: 2, mb: 2 }}>
          <Chip
            icon={<Schedule />}
            label={`${formatDate(survey.startDate)} - ${formatDate(survey.endDate)}`}
            variant="outlined"
          />
          <Chip
            icon={<TrendingUp />}
            label={`${survey.totalResponses} responses`}
            variant="outlined"
          />
          <Chip
            icon={<Person />}
            label={survey.anonymous ? 'Anonymous' : 'Named responses'}
            variant="outlined"
          />
        </Box>
      </Paper>

      {/* Questions */}
      <Grid container spacing={3}>
        <Grid item xs={12} md={8}>
          <Typography variant="h5" gutterBottom>
            Questions ({survey.questions.length})
          </Typography>
          
          {survey.questions.map((question, index) => (
            <Card key={question.id} sx={{ mb: 3 }}>
              <CardContent>
                <Box sx={{ display: 'flex', alignItems: 'center', mb: 2 }}>
                  <Typography variant="h6" component="h3">
                    {index + 1}. {question.text}
                  </Typography>
                  {question.required && (
                    <Chip label="Required" color="error" size="small" sx={{ ml: 1 }} />
                  )}
                </Box>
                
                <Box sx={{ mb: 3 }}>
                  {renderQuestionInput(question)}
                </Box>
              </CardContent>
            </Card>
          ))}

          {/* Submit Section */}
          <Card sx={{ mb: 3 }}>
            <CardContent>
              <FormControlLabel
                control={
                  <Checkbox
                    checked={anonymous}
                    onChange={(e) => setAnonymous(e.target.checked)}
                  />
                }
                label="Submit responses anonymously"
              />
              
              <Box sx={{ mt: 2 }}>
                <Button
                  variant="contained"
                  size="large"
                  onClick={handleSubmit}
                  disabled={submitting}
                  fullWidth
                >
                  {submitting ? 'Submitting...' : 'Submit Responses'}
                </Button>
              </Box>
            </CardContent>
          </Card>
        </Grid>

        {/* Top Responses Sidebar */}
        <Grid item xs={12} md={4}>
          <Typography variant="h6" gutterBottom>
            Top Responses
          </Typography>
          
          {survey.questions.map((question) => (
            <Accordion key={question.id} sx={{ mb: 1 }}>
              <AccordionSummary expandIcon={<ExpandMore />}>
                <Typography variant="subtitle2">
                  Q{question.orderIndex}: {question.text.substring(0, 50)}...
                </Typography>
              </AccordionSummary>
              <AccordionDetails>
                {renderTopResponses(question)}
              </AccordionDetails>
            </Accordion>
          ))}
        </Grid>
      </Grid>
    </Container>
  );
};

export default SurveyDetailPage; 