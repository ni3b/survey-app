package com.survey.controller;

import com.survey.dto.SurveyDto;
import com.survey.dto.ResponseDto;
import com.survey.service.SurveyService;
import com.survey.service.ResponseService;
import com.survey.security.JwtTokenProvider;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.Parameter;
import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Max;
import java.util.List;

/**
 * Optimized survey controller using Spring Boot features to reduce custom code.
 * Uses pagination, validation, and built-in error handling.
 */
@RestController
@RequestMapping("/api/v2/surveys")
@Tag(name = "Optimized Survey", description = "Optimized survey APIs using Spring Boot features")
@CrossOrigin(origins = "*")
@Validated
public class OptimizedSurveyController {

    @Autowired
    private SurveyService surveyService;
    
    @Autowired
    private ResponseService responseService;
    
    @Autowired
    private JwtTokenProvider jwtTokenProvider;

    @GetMapping("/active")
    @Operation(summary = "Get all active surveys")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Successfully retrieved active surveys"),
        @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    public ResponseEntity<List<SurveyDto>> getActiveSurveys() {
        return ResponseEntity.ok(surveyService.getActiveSurveyDtos());
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get survey by ID")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Successfully retrieved survey"),
        @ApiResponse(responseCode = "404", description = "Survey not found"),
        @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    public ResponseEntity<SurveyDto> getSurveyById(
            @Parameter(description = "Survey ID") 
            @PathVariable Long id) {
        return surveyService.getSurveyDtoById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping
    @Operation(summary = "Get surveys with pagination and filtering")
    public ResponseEntity<Page<SurveyDto>> getSurveys(
            @PageableDefault(size = 10, sort = "createdAt") Pageable pageable,
            @Parameter(description = "Filter by status") 
            @RequestParam(required = false) String status,
            @Parameter(description = "Search by title") 
            @RequestParam(required = false) String title) {
        
        Page<SurveyDto> surveys = surveyService.getSurveysWithPagination(pageable, status, title);
        return ResponseEntity.ok(surveys);
    }

    @GetMapping("/{surveyId}/questions")
    @Operation(summary = "Get survey questions")
    public ResponseEntity<?> getSurveyQuestions(
            @Parameter(description = "Survey ID") 
            @PathVariable Long surveyId) {
        return surveyService.getSurveyDtoById(surveyId)
                .map(surveyDto -> ResponseEntity.ok(surveyDto.getQuestions()))
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/questions/{questionId}/responses")
    @Operation(summary = "Get top responses with validation")
    public ResponseEntity<List<ResponseDto>> getTopResponses(
            @Parameter(description = "Question ID") 
            @PathVariable Long questionId,
            @Parameter(description = "Maximum number of responses") 
            @RequestParam(defaultValue = "5") 
            @Min(1) @Max(50) int limit,
            HttpServletRequest request) {
        
        // Extract user ID from JWT token (optional for viewing responses)
        String token = extractTokenFromRequest(request);
        Long userId = token != null ? jwtTokenProvider.getUserIdFromToken(token) : null;
        
        List<ResponseDto> responses = responseService.getTopResponseDtosForQuestion(questionId, limit, userId);
        return ResponseEntity.ok(responses);
    }

    @PostMapping("/responses")
    @Operation(summary = "Submit response with validation")
    public ResponseEntity<ResponseDto> submitResponse(
            @Parameter(description = "Question ID") 
            @RequestParam Long questionId,
            @Valid @RequestBody ResponseDto responseDto,
            HttpServletRequest request) {
        
        // Extract user ID from JWT token
        String token = extractTokenFromRequest(request);
        Long userId = jwtTokenProvider.getUserIdFromToken(token);
        
        if (userId == null) {
            return ResponseEntity.status(401).build();
        }
        
        ResponseDto savedResponse = responseService.createResponseDto(questionId, responseDto, userId);
        return ResponseEntity.ok(savedResponse);
    }

    @PostMapping("/responses/{responseId}/upvote")
    @Operation(summary = "Upvote response")
    public ResponseEntity<?> upvoteResponse(
            @Parameter(description = "Response ID") 
            @PathVariable Long responseId,
            HttpServletRequest request) {
        
        // Extract user ID from JWT token
        String token = extractTokenFromRequest(request);
        Long userId = jwtTokenProvider.getUserIdFromToken(token);
        
        if (userId == null) {
            return ResponseEntity.status(401).build();
        }
        
        responseService.upvoteResponse(responseId, userId);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/responses/{responseId}/upvote")
    @Operation(summary = "Remove upvote")
    public ResponseEntity<?> removeUpvote(
            @Parameter(description = "Response ID") 
            @PathVariable Long responseId,
            HttpServletRequest request) {
        
        // Extract user ID from JWT token
        String token = extractTokenFromRequest(request);
        Long userId = jwtTokenProvider.getUserIdFromToken(token);
        
        if (userId == null) {
            return ResponseEntity.status(401).build();
        }
        
        responseService.removeUpvote(responseId, userId);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/questions/{questionId}/statistics")
    @Operation(summary = "Get question statistics")
    public ResponseEntity<?> getQuestionStatistics(
            @Parameter(description = "Question ID") 
            @PathVariable Long questionId) {
        
        return ResponseEntity.ok(responseService.getQuestionStatistics(questionId));
    }
    
    /**
     * Extract JWT token from request header.
     */
    private String extractTokenFromRequest(HttpServletRequest request) {
        String bearerToken = request.getHeader("Authorization");
        if (bearerToken != null && bearerToken.startsWith("Bearer ")) {
            return bearerToken.substring(7);
        }
        return null;
    }
} 