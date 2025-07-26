package com.survey.controller;

import com.survey.dto.AuthDto;
import com.survey.dto.QuestionDto;
import com.survey.dto.ResponseDto;
import com.survey.dto.SurveyDto;
import com.survey.service.ResponseService;
import com.survey.service.SurveyService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Public survey controller for user-facing survey operations.
 * 
 * @author Survey Team
 */
@RestController
@RequestMapping("/api")
@Tag(name = "Public Survey", description = "Public survey endpoints for users")
@CrossOrigin(origins = "*")
public class SurveyController {
    
    private static final Logger logger = LoggerFactory.getLogger(SurveyController.class);
    
    @Autowired
    private SurveyService surveyService;
    
    @Autowired
    private ResponseService responseService;
    
    /**
     * Get all active surveys.
     * 
     * @return list of active surveys
     */
    @GetMapping("/surveys/active")
    @Operation(summary = "Get active surveys", description = "Get all currently active surveys")
    public ResponseEntity<List<SurveyDto>> getActiveSurveys() {
        logger.info("Getting active surveys");
        List<SurveyDto> surveys = surveyService.getActiveSurveyDtos();
        logger.debug("Found {} active surveys", surveys.size());
        return ResponseEntity.ok(surveys);
    }
    
    /**
     * Get survey by ID.
     * 
     * @param id the survey ID
     * @return survey details
     */
    @GetMapping("/surveys/{id}")
    @Operation(summary = "Get survey by ID", description = "Get detailed information about a specific survey")
    public ResponseEntity<?> getSurveyById(@PathVariable Long id) {
        logger.info("Getting survey by ID: {}", id);
        return surveyService.getSurveyDtoById(id)
                .map(survey -> {
                    logger.debug("Survey found: {}", survey.getTitle());
                    return ResponseEntity.ok(survey);
                })
                .orElseGet(() -> {
                    logger.warn("Survey not found with ID: {}", id);
                    return ResponseEntity.notFound().build();
                });
    }
    
    /**
     * Get questions for a survey.
     * 
     * @param surveyId the survey ID
     * @return list of questions
     */
    @GetMapping("/surveys/{surveyId}/questions")
    @Operation(summary = "Get survey questions", description = "Get all questions for a specific survey")
    public ResponseEntity<?> getSurveyQuestions(@PathVariable Long surveyId) {
        return surveyService.getSurveyDtoById(surveyId)
                .map(surveyDto -> ResponseEntity.ok(surveyDto.getQuestions()))
                .orElse(ResponseEntity.notFound().build());
    }
    
    /**
     * Get top responses for a question.
     * 
     * @param questionId the question ID
     * @param limit maximum number of responses (default 5)
     * @param request the HTTP request
     * @return list of top responses
     */
    @GetMapping("/questions/{questionId}/responses")
    @Operation(summary = "Get top responses", description = "Get top responses for a question with most upvotes")
    public ResponseEntity<List<ResponseDto>> getTopResponses(
            @PathVariable Long questionId,
            @RequestParam(defaultValue = "5") int limit,
            HttpServletRequest request) {
        
        // Extract user ID from JWT token if available
        Long userId = extractUserIdFromRequest(request);
        
        List<ResponseDto> responses = responseService.getTopResponseDtosForQuestion(questionId, limit, userId);
        return ResponseEntity.ok(responses);
    }
    
    /**
     * Submit a response to a question.
     * 
     * @param questionId the question ID
     * @param responseDto the response data
     * @param request the HTTP request
     * @return the created response
     */
    @PostMapping("/responses")
    @Operation(summary = "Submit response", description = "Submit a new response to a question")
    public ResponseEntity<?> submitResponse(
            @RequestParam Long questionId,
            @Valid @RequestBody ResponseDto responseDto,
            HttpServletRequest request) {
        
        try {
            // Extract user ID from JWT token if available
            Long userId = extractUserIdFromRequest(request);
            
            String ipAddress = getClientIpAddress(request);
            String userAgent = request.getHeader("User-Agent");
            
            var response = responseService.submitResponse(questionId, responseDto, userId, ipAddress, userAgent);
            
            return ResponseEntity.ok(new ResponseDto(response));
            
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                    .body(new AuthDto.ErrorResponse("ERROR", e.getMessage(), 400));
        }
    }
    
    /**
     * Upvote a response.
     * 
     * @param responseId the response ID
     * @param request the HTTP request
     * @return success response
     */
    @PostMapping("/responses/{responseId}/upvote")
    @Operation(summary = "Upvote response", description = "Upvote a response (requires authentication)")
    public ResponseEntity<?> upvoteResponse(
            @PathVariable Long responseId,
            HttpServletRequest request) {
        
        try {
            Long userId = extractUserIdFromRequest(request);
            if (userId == null) {
                return ResponseEntity.badRequest()
                        .body(new AuthDto.ErrorResponse("UNAUTHORIZED", "Authentication required", 401));
            }
            
            String ipAddress = getClientIpAddress(request);
            boolean success = responseService.upvoteResponse(responseId, userId, ipAddress);
            
            if (success) {
                return ResponseEntity.ok(new AuthDto.LoginResponse("Upvote successful"));
            } else {
                return ResponseEntity.badRequest()
                        .body(new AuthDto.ErrorResponse("ERROR", "Failed to upvote", 400));
            }
            
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                    .body(new AuthDto.ErrorResponse("ERROR", e.getMessage(), 400));
        }
    }
    
    /**
     * Remove upvote from a response.
     * 
     * @param responseId the response ID
     * @param request the HTTP request
     * @return success response
     */
    @DeleteMapping("/responses/{responseId}/upvote")
    @Operation(summary = "Remove upvote", description = "Remove upvote from a response (requires authentication)")
    public ResponseEntity<?> removeUpvote(
            @PathVariable Long responseId,
            HttpServletRequest request) {
        
        try {
            Long userId = extractUserIdFromRequest(request);
            if (userId == null) {
                return ResponseEntity.badRequest()
                        .body(new AuthDto.ErrorResponse("UNAUTHORIZED", "Authentication required", 401));
            }
            
            boolean success = responseService.removeUpvote(responseId, userId);
            
            if (success) {
                return ResponseEntity.ok(new AuthDto.LoginResponse("Upvote removed"));
            } else {
                return ResponseEntity.badRequest()
                        .body(new AuthDto.ErrorResponse("ERROR", "Failed to remove upvote", 400));
            }
            
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                    .body(new AuthDto.ErrorResponse("ERROR", e.getMessage(), 400));
        }
    }
    
    /**
     * Get response statistics for a question.
     * 
     * @param questionId the question ID
     * @return response statistics
     */
    @GetMapping("/questions/{questionId}/statistics")
    @Operation(summary = "Get question statistics", description = "Get response statistics for a question")
    public ResponseEntity<?> getQuestionStatistics(@PathVariable Long questionId) {
        try {
            var statistics = responseService.getResponseStatisticsForQuestion(questionId);
            return ResponseEntity.ok(statistics);
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                    .body(new AuthDto.ErrorResponse("ERROR", e.getMessage(), 400));
        }
    }
    
    /**
     * Extract user ID from JWT token in request.
     * 
     * @param request the HTTP request
     * @return user ID or null if not found
     */
    private Long extractUserIdFromRequest(HttpServletRequest request) {
        // This is a simplified implementation
        // In a real application, you would extract the user ID from the JWT token
        String authHeader = request.getHeader("Authorization");
        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            // For now, return null to indicate anonymous user
            // In a real implementation, you would decode the JWT and extract user ID
            return null;
        }
        return null;
    }
    
    /**
     * Get client IP address from request.
     * 
     * @param request the HTTP request
     * @return client IP address
     */
    private String getClientIpAddress(HttpServletRequest request) {
        String xForwardedFor = request.getHeader("X-Forwarded-For");
        if (xForwardedFor != null && !xForwardedFor.isEmpty() && !"unknown".equalsIgnoreCase(xForwardedFor)) {
            return xForwardedFor.split(",")[0];
        }
        
        String xRealIp = request.getHeader("X-Real-IP");
        if (xRealIp != null && !xRealIp.isEmpty() && !"unknown".equalsIgnoreCase(xRealIp)) {
            return xRealIp;
        }
        
        return request.getRemoteAddr();
    }
} 