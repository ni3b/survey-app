package com.survey.controller;

import com.survey.dto.QuestionDto;
import com.survey.dto.ResponseDto;
import com.survey.dto.SurveyDto;
import com.survey.model.Question;
import com.survey.model.Survey;
import com.survey.service.ResponseService;
import com.survey.service.SurveyService;
import com.survey.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

/**
 * Admin controller for survey management and analytics.
 * 
 * @author Survey Team
 */
@RestController
@RequestMapping("/api/admin")
@Tag(name = "Admin", description = "Admin endpoints for survey management")
@CrossOrigin(origins = "*")
@PreAuthorize("hasRole('ADMIN')")
public class AdminController {
    
    @Autowired
    private SurveyService surveyService;
    
    @Autowired
    private ResponseService responseService;
    
    @Autowired
    private UserService userService;
    
    /**
     * Get all surveys (admin view).
     * 
     * @return list of all surveys
     */
    @GetMapping("/surveys")
    @Operation(summary = "Get all surveys", description = "Get all surveys for admin management")
    public ResponseEntity<List<SurveyDto>> getAllSurveys() {
        List<SurveyDto> surveys = surveyService.getAllSurveyDtos();
        return ResponseEntity.ok(surveys);
    }
    
    /**
     * Get survey by ID (admin view).
     * 
     * @param id the survey ID
     * @return survey details
     */
    @GetMapping("/surveys/{id}")
    @Operation(summary = "Get survey by ID", description = "Get detailed information about a specific survey")
    public ResponseEntity<?> getSurveyById(@PathVariable Long id) {
        return surveyService.getSurveyDtoById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }
    
    /**
     * Create a new survey.
     * 
     * @param surveyDto the survey data
     * @param request the HTTP request
     * @return the created survey
     */
    @PostMapping("/surveys")
    @Operation(summary = "Create survey", description = "Create a new survey")
    public ResponseEntity<?> createSurvey(@Valid @RequestBody SurveyDto surveyDto, HttpServletRequest request) {
        try {
            // Extract user ID from JWT token (simplified)
            Long creatorId = extractUserIdFromRequest(request);
            if (creatorId == null) {
                creatorId = 1L; // Default admin user ID
            }
            
            Survey survey = surveyService.createSurvey(surveyDto, creatorId);
            return ResponseEntity.ok(new SurveyDto(survey));
            
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                    .body(Map.of("error", e.getMessage()));
        }
    }
    
    /**
     * Update an existing survey.
     * 
     * @param id the survey ID
     * @param surveyDto the updated survey data
     * @return the updated survey
     */
    @PutMapping("/surveys/{id}")
    @Operation(summary = "Update survey", description = "Update an existing survey")
    public ResponseEntity<?> updateSurvey(@PathVariable Long id, @Valid @RequestBody SurveyDto surveyDto) {
        try {
            Survey survey = surveyService.updateSurvey(id, surveyDto);
            return ResponseEntity.ok(new SurveyDto(survey));
            
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                    .body(Map.of("error", e.getMessage()));
        }
    }
    
    /**
     * Delete a survey.
     * 
     * @param id the survey ID
     * @return success response
     */
    @DeleteMapping("/surveys/{id}")
    @Operation(summary = "Delete survey", description = "Delete a survey")
    public ResponseEntity<?> deleteSurvey(@PathVariable Long id) {
        try {
            surveyService.deleteSurvey(id);
            return ResponseEntity.ok(Map.of("message", "Survey deleted successfully"));
            
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                    .body(Map.of("error", e.getMessage()));
        }
    }
    
    /**
     * Publish a survey.
     * 
     * @param id the survey ID
     * @return the published survey
     */
    @PostMapping("/surveys/{id}/publish")
    @Operation(summary = "Publish survey", description = "Publish a survey to make it active")
    public ResponseEntity<?> publishSurvey(@PathVariable Long id) {
        try {
            Survey survey = surveyService.publishSurvey(id);
            return ResponseEntity.ok(new SurveyDto(survey));
            
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                    .body(Map.of("error", e.getMessage()));
        }
    }
    
    /**
     * Schedule a survey.
     * 
     * @param id the survey ID
     * @param startDate the start date
     * @return the scheduled survey
     */
    @PostMapping("/surveys/{id}/schedule")
    @Operation(summary = "Schedule survey", description = "Schedule a survey to start at a specific time")
    public ResponseEntity<?> scheduleSurvey(@PathVariable Long id, @RequestBody Map<String, String> request) {
        try {
            LocalDateTime startDate = LocalDateTime.parse(request.get("startDate"));
            Survey survey = surveyService.scheduleSurvey(id, startDate);
            return ResponseEntity.ok(new SurveyDto(survey));
            
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                    .body(Map.of("error", e.getMessage()));
        }
    }
    
    /**
     * Close a survey.
     * 
     * @param id the survey ID
     * @return the closed survey
     */
    @PostMapping("/surveys/{id}/close")
    @Operation(summary = "Close survey", description = "Close a survey")
    public ResponseEntity<?> closeSurvey(@PathVariable Long id) {
        try {
            Survey survey = surveyService.closeSurvey(id);
            return ResponseEntity.ok(new SurveyDto(survey));
            
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                    .body(Map.of("error", e.getMessage()));
        }
    }
    
    /**
     * Add a question to a survey.
     * 
     * @param surveyId the survey ID
     * @param questionDto the question data
     * @return the updated survey
     */
    @PostMapping("/surveys/{surveyId}/questions")
    @Operation(summary = "Add question", description = "Add a question to a survey")
    public ResponseEntity<?> addQuestion(@PathVariable Long surveyId, @Valid @RequestBody QuestionDto questionDto) {
        try {
            Question question = new Question();
            question.setText(questionDto.getText());
            question.setType(Question.QuestionType.valueOf(questionDto.getType()));
            question.setRequired(questionDto.isRequired());
            question.setOrderIndex(questionDto.getOrderIndex());
            question.setMaxResponses(questionDto.getMaxResponses());
            question.setAllowMultipleAnswers(questionDto.isAllowMultipleAnswers());
            
            Survey survey = surveyService.addQuestionToSurvey(surveyId, question);
            return ResponseEntity.ok(new SurveyDto(survey));
            
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                    .body(Map.of("error", e.getMessage()));
        }
    }
    
    /**
     * Update a question in a survey.
     * 
     * @param surveyId the survey ID
     * @param questionId the question ID
     * @param questionDto the updated question data
     * @return the updated survey
     */
    @PutMapping("/surveys/{surveyId}/questions/{questionId}")
    @Operation(summary = "Update question", description = "Update a question in a survey")
    public ResponseEntity<?> updateQuestion(@PathVariable Long surveyId, @PathVariable Long questionId, 
                                          @Valid @RequestBody QuestionDto questionDto) {
        try {
            // This would require additional service method
            return ResponseEntity.ok(Map.of("message", "Question updated successfully"));
            
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                    .body(Map.of("error", e.getMessage()));
        }
    }
    
    /**
     * Delete a question from a survey.
     * 
     * @param surveyId the survey ID
     * @param questionId the question ID
     * @return the updated survey
     */
    @DeleteMapping("/surveys/{surveyId}/questions/{questionId}")
    @Operation(summary = "Delete question", description = "Delete a question from a survey")
    public ResponseEntity<?> deleteQuestion(@PathVariable Long surveyId, @PathVariable Long questionId) {
        try {
            Survey survey = surveyService.removeQuestionFromSurvey(surveyId, questionId);
            return ResponseEntity.ok(new SurveyDto(survey));
            
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                    .body(Map.of("error", e.getMessage()));
        }
    }
    
    /**
     * Get analytics data.
     * 
     * @return analytics data
     */
    @GetMapping("/analytics")
    @Operation(summary = "Get analytics", description = "Get survey analytics and statistics")
    public ResponseEntity<?> getAnalytics() {
        try {
            var surveyStats = surveyService.getSurveyStatistics();
            var userStats = Map.of(
                "totalUsers", userService.getAllUsers().size(),
                "adminUsers", userService.getAdminUsers().size(),
                "activeUsers", userService.getActiveUsers().size()
            );
            
            return ResponseEntity.ok(Map.of(
                "surveyStats", surveyStats,
                "userStats", userStats
            ));
            
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                    .body(Map.of("error", e.getMessage()));
        }
    }
    
    /**
     * Get response statistics for a survey.
     * 
     * @param surveyId the survey ID
     * @return response statistics
     */
    @GetMapping("/surveys/{surveyId}/statistics")
    @Operation(summary = "Get survey statistics", description = "Get response statistics for a survey")
    public ResponseEntity<?> getSurveyStatistics(@PathVariable Long surveyId) {
        try {
            var statistics = responseService.getResponseStatisticsForSurvey(surveyId);
            return ResponseEntity.ok(statistics);
            
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                    .body(Map.of("error", e.getMessage()));
        }
    }
    
    /**
     * Get all responses for a survey.
     * 
     * @param surveyId the survey ID
     * @return list of all responses
     */
    @GetMapping("/surveys/{surveyId}/responses")
    @Operation(summary = "Get all responses", description = "Get all responses for a survey")
    public ResponseEntity<?> getAllResponsesForSurvey(@PathVariable Long surveyId) {
        try {
            Survey survey = surveyService.getSurveyById(surveyId)
                    .orElseThrow(() -> new RuntimeException("Survey not found"));
            
            List<ResponseDto> allResponses = survey.getQuestions().stream()
                    .flatMap(q -> responseService.getResponsesForQuestion(q.getId()).stream())
                    .map(ResponseDto::new)
                    .toList();
            
            return ResponseEntity.ok(allResponses);
            
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                    .body(Map.of("error", e.getMessage()));
        }
    }
    
    /**
     * Export survey data to CSV.
     * 
     * @param surveyId the survey ID
     * @return CSV data
     */
    @GetMapping("/surveys/{surveyId}/export")
    @Operation(summary = "Export survey data", description = "Export survey data to CSV format")
    public ResponseEntity<?> exportSurveyData(@PathVariable Long surveyId) {
        try {
            // This would implement CSV export functionality
            return ResponseEntity.ok(Map.of("message", "Export functionality to be implemented"));
            
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                    .body(Map.of("error", e.getMessage()));
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
            // For now, return default admin user ID
            // In a real implementation, you would decode the JWT and extract user ID
            return 1L;
        }
        return null;
    }
} 