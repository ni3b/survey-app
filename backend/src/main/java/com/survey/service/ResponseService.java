package com.survey.service;

import com.survey.dto.ResponseDto;
import com.survey.model.Question;
import com.survey.model.Response;
import com.survey.model.Survey;
import com.survey.model.User;
import com.survey.repository.QuestionRepository;
import com.survey.repository.ResponseRepository;
import com.survey.repository.UpvoteRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import com.survey.model.Upvote;

/**
 * Service class for response and upvote management.
 * 
 * @author Survey Team
 */
@Service
public class ResponseService {
    
    @Autowired
    private ResponseRepository responseRepository;
    
    @Autowired
    private QuestionRepository questionRepository;
    
    @Autowired
    private UpvoteRepository upvoteRepository;
    
    @Autowired
    private UserService userService;
    
    @Autowired
    private SurveyService surveyService;
    
    /**
     * Submit a response to a question.
     * 
     * @param questionId the question ID
     * @param responseDto the response data
     * @param userId the user ID (can be null for anonymous responses)
     * @param ipAddress the IP address
     * @param userAgent the user agent
     * @return the created response
     */
    public Response submitResponse(Long questionId, ResponseDto responseDto, Long userId, 
                                 String ipAddress, String userAgent) {
        Question question = questionRepository.findById(questionId)
                .orElseThrow(() -> new RuntimeException("Question not found"));
        
        Survey survey = question.getSurvey();
        
        // Check if survey is active
        if (!survey.isActive()) {
            throw new RuntimeException("Survey is not active");
        }
        
        // Check if user has already responded (if not anonymous and multiple responses not allowed)
        if (userId != null && !survey.isAllowMultipleResponses()) {
            List<Response> existingResponses = responseRepository.findByQuestionIdAndUserId(questionId, userId);
            if (!existingResponses.isEmpty()) {
                throw new RuntimeException("User has already responded to this question");
            }
        }
        
        Response response = new Response();
        response.setText(responseDto.getText());
        response.setQuestion(question);
        response.setAnonymous(responseDto.isAnonymous());
        response.setIpAddress(ipAddress);
        response.setUserAgent(userAgent);
        
        // Set user if not anonymous
        if (userId != null && !responseDto.isAnonymous()) {
            User user = userService.findById(userId)
                    .orElseThrow(() -> new RuntimeException("User not found"));
            response.setUser(user);
        }
        
        return responseRepository.save(response);
    }
    
    /**
     * Create a response DTO from a question ID and response data.
     * 
     * @param questionId the question ID
     * @param responseDto the response data
     * @return the created response DTO
     */
    public ResponseDto createResponseDto(Long questionId, ResponseDto responseDto) {
        Response response = submitResponse(questionId, responseDto, null, null, null);
        return new ResponseDto(response, false);
    }
    
    /**
     * Get responses for a question.
     * 
     * @param questionId the question ID
     * @return list of responses for the question
     */
    public List<Response> getResponsesForQuestion(Long questionId) {
        return responseRepository.findByQuestionId(questionId);
    }
    
    /**
     * Get top responses for a question (with most upvotes).
     * 
     * @param questionId the question ID
     * @param limit maximum number of responses to return
     * @return list of top responses
     */
    public List<Response> getTopResponsesForQuestion(Long questionId, int limit) {
        Question question = questionRepository.findById(questionId)
                .orElseThrow(() -> new RuntimeException("Question not found"));
        
        return question.getTopResponses(limit);
    }
    
    /**
     * Get top responses for a question as DTOs.
     * 
     * @param questionId the question ID
     * @param limit maximum number of responses to return
     * @param userId the user ID for checking upvotes
     * @return list of top response DTOs
     */
    public List<ResponseDto> getTopResponseDtosForQuestion(Long questionId, int limit, Long userId) {
        List<Response> responses = getTopResponsesForQuestion(questionId, limit);
        
        return responses.stream()
                .map(response -> {
                    boolean hasUserUpvoted = userId != null && response.hasUserUpvoted(
                            userService.findById(userId).orElse(null));
                    return new ResponseDto(response, hasUserUpvoted);
                })
                .collect(Collectors.toList());
    }
    
    /**
     * Upvote a response.
     * 
     * @param responseId the response ID
     * @param userId the user ID
     * @param ipAddress the IP address
     * @return true if upvote successful, false otherwise
     */
    public boolean upvoteResponse(Long responseId, Long userId, String ipAddress) {
        Response response = responseRepository.findById(responseId)
                .orElseThrow(() -> new RuntimeException("Response not found"));
        
        User user = userService.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));
        
        // Check if user has already upvoted this response
        if (upvoteRepository.existsByUserIdAndResponseId(userId, responseId)) {
            return false; // Already upvoted
        }
        
        // Create upvote
        Upvote upvote = new Upvote();
        upvote.setResponse(response);
        upvote.setUser(user);
        upvote.setIpAddress(ipAddress);
        upvoteRepository.save(upvote);
        
        return true;
    }
    
    /**
     * Upvote a response (simplified version for anonymous users).
     * 
     * @param responseId the response ID
     * @param userId the user ID (can be null for anonymous)
     */
    public void upvoteResponse(Long responseId, Long userId) {
        if (userId != null) {
            upvoteResponse(responseId, userId, null);
        }
        // For anonymous users, we could implement IP-based tracking if needed
    }
    
    /**
     * Remove upvote from a response.
     * 
     * @param responseId the response ID
     * @param userId the user ID
     * @return true if upvote removal successful, false otherwise
     */
    public boolean removeUpvote(Long responseId, Long userId) {
        Optional<com.survey.model.Upvote> upvote = upvoteRepository.findByUserIdAndResponseId(userId, responseId);
        
        if (upvote.isPresent()) {
            upvoteRepository.delete(upvote.get());
            return true;
        }
        
        return false;
    }
    
    /**
     * Get responses by user.
     * 
     * @param userId the user ID
     * @return list of responses by the user
     */
    public List<Response> getResponsesByUser(Long userId) {
        return responseRepository.findByUserId(userId);
    }
    
    /**
     * Get responses by user as DTOs.
     * 
     * @param userId the user ID
     * @return list of response DTOs by the user
     */
    public List<ResponseDto> getResponseDtosByUser(Long userId) {
        return getResponsesByUser(userId).stream()
                .map(ResponseDto::new)
                .collect(Collectors.toList());
    }
    
    /**
     * Get recent responses.
     * 
     * @param limit maximum number of responses to return
     * @return list of recent responses
     */
    public List<Response> getRecentResponses(int limit) {
        return responseRepository.findRecentResponses(limit);
    }
    
    /**
     * Get responses with most upvotes.
     * 
     * @param limit maximum number of responses to return
     * @return list of responses with most upvotes
     */
    public List<Response> getResponsesWithMostUpvotes(int limit) {
        return responseRepository.findResponsesWithMostUpvotes(limit);
    }
    
    /**
     * Get responses created within a date range.
     * 
     * @param startDate start date
     * @param endDate end date
     * @return list of responses in the date range
     */
    public List<Response> getResponsesInDateRange(LocalDateTime startDate, LocalDateTime endDate) {
        return responseRepository.findByCreatedAtBetween(startDate, endDate);
    }
    
    /**
     * Get response statistics for a survey.
     * 
     * @param surveyId the survey ID
     * @return response statistics
     */
    public ResponseStatistics getResponseStatisticsForSurvey(Long surveyId) {
        Survey survey = surveyService.getSurveyById(surveyId)
                .orElseThrow(() -> new RuntimeException("Survey not found"));
        
        long totalResponses = survey.getQuestions().stream()
                .mapToLong(q -> responseRepository.countByQuestion(q))
                .sum();
        
        long totalUpvotes = survey.getQuestions().stream()
                .mapToLong(Question::getTotalUpvotes)
                .sum();
        
        long anonymousResponses = survey.getQuestions().stream()
                .mapToLong(q -> responseRepository.countByQuestionAndAnonymous(q, true))
                .sum();
        
        return new ResponseStatistics(totalResponses, totalUpvotes, anonymousResponses);
    }
    
    /**
     * Get response statistics for a question.
     * 
     * @param questionId the question ID
     * @return response statistics
     */
    public ResponseStatistics getResponseStatisticsForQuestion(Long questionId) {
        List<Response> responses = getResponsesForQuestion(questionId);
        
        long totalResponses = responses.size();
        long totalUpvotes = responses.stream()
                .mapToLong(response -> response.getUpvotes().size())
                .sum();
        long anonymousResponses = responses.stream()
                .filter(Response::isAnonymous)
                .count();
        
        return new ResponseStatistics(totalResponses, totalUpvotes, anonymousResponses);
    }
    
    /**
     * Get question statistics (alias for getResponseStatisticsForQuestion).
     * 
     * @param questionId the question ID
     * @return question statistics
     */
    public ResponseStatistics getQuestionStatistics(Long questionId) {
        return getResponseStatisticsForQuestion(questionId);
    }
    
    /**
     * Search responses by text.
     * 
     * @param text the search term
     * @return list of matching responses
     */
    public List<Response> searchResponsesByText(String text) {
        return responseRepository.findByTextContainingIgnoreCase(text);
    }
    
    /**
     * Delete a response (admin only).
     * 
     * @param responseId the response ID
     */
    public void deleteResponse(Long responseId) {
        Response response = responseRepository.findById(responseId)
                .orElseThrow(() -> new RuntimeException("Response not found"));
        
        responseRepository.delete(response);
    }
    
    /**
     * Response statistics class.
     */
    public static class ResponseStatistics {
        private final long totalResponses;
        private final long totalUpvotes;
        private final long anonymousResponses;
        
        public ResponseStatistics(long totalResponses, long totalUpvotes, long anonymousResponses) {
            this.totalResponses = totalResponses;
            this.totalUpvotes = totalUpvotes;
            this.anonymousResponses = anonymousResponses;
        }
        
        // Getters
        public long getTotalResponses() { return totalResponses; }
        public long getTotalUpvotes() { return totalUpvotes; }
        public long getAnonymousResponses() { return anonymousResponses; }
        public long getNamedResponses() { return totalResponses - anonymousResponses; }
    }
} 