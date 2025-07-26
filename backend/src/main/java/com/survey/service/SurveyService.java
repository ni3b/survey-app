package com.survey.service;

import com.survey.dto.SurveyDto;
import com.survey.model.Question;
import com.survey.model.Response;
import com.survey.model.Survey;
import com.survey.model.User;
import com.survey.repository.ResponseRepository;
import com.survey.repository.SurveyRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;

/**
 * Service class for survey management operations.
 * 
 * @author Survey Team
 */
@Service
public class SurveyService {
    
    @Autowired
    private SurveyRepository surveyRepository;
    
    @Autowired
    private ResponseRepository responseRepository;
    
    @Autowired
    private UserService userService;
    
    /**
     * Get all surveys.
     * 
     * @return list of all surveys
     */
    public List<Survey> getAllSurveys() {
        return surveyRepository.findAll();
    }
    
    /**
     * Get all surveys as DTOs.
     * 
     * @return list of survey DTOs
     */
    public List<SurveyDto> getAllSurveyDtos() {
        return getAllSurveyDtosWithResponses();
    }
    
    /**
     * Get all surveys as DTOs with responses.
     * This method fetches questions and responses separately to avoid MultipleBagFetchException.
     * 
     * @return list of survey DTOs with responses
     */
    public List<SurveyDto> getAllSurveyDtosWithResponses() {
        List<Survey> surveys = surveyRepository.findAllWithQuestionsAndResponses();
        List<SurveyDto> surveyDtos = new ArrayList<>();
        
        for (Survey survey : surveys) {
            surveyDtos.add(new SurveyDto(survey));
        }
        
        return surveyDtos;
    }
    
    /**
     * Get survey by ID.
     * 
     * @param id the survey ID
     * @return Optional containing the survey if found
     */
    public Optional<Survey> getSurveyById(Long id) {
        return surveyRepository.findById(id);
    }
    
    /**
     * Get survey DTO by ID.
     * 
     * @param id the survey ID
     * @return Optional containing the survey DTO if found
     */
    public Optional<SurveyDto> getSurveyDtoById(Long id) {
        return getSurveyDtoByIdWithResponses(id);
    }
    
    /**
     * Get survey DTO by ID with responses.
     * This method fetches questions and responses separately to avoid MultipleBagFetchException.
     * 
     * @param id the survey ID
     * @return Optional containing the survey DTO with responses if found
     */
    public Optional<SurveyDto> getSurveyDtoByIdWithResponses(Long id) {
        Optional<Survey> surveyOpt = surveyRepository.findByIdWithQuestionsAndResponses(id);
        if (surveyOpt.isPresent()) {
            Survey survey = surveyOpt.get();
            return Optional.of(new SurveyDto(survey));
        }
        return Optional.empty();
    }
    
    /**
     * Get active surveys.
     * 
     * @return list of active surveys
     */
    public List<Survey> getActiveSurveys() {
        return surveyRepository.findActiveSurveys(LocalDateTime.now());
    }
    
    /**
     * Get active surveys as DTOs.
     * 
     * @return list of active survey DTOs
     */
    public List<SurveyDto> getActiveSurveyDtos() {
        List<Survey> surveys = surveyRepository.findActiveSurveysWithQuestionsAndResponses(LocalDateTime.now());
        List<SurveyDto> surveyDtos = new ArrayList<>();
        
        for (Survey survey : surveys) {
            surveyDtos.add(new SurveyDto(survey));
        }
        
        return surveyDtos;
    }
    
    /**
     * Get surveys by status.
     * 
     * @param status the survey status
     * @return list of surveys with the specified status
     */
    public List<Survey> getSurveysByStatus(Survey.SurveyStatus status) {
        return surveyRepository.findByStatus(status);
    }
    
    /**
     * Get surveys by creator.
     * 
     * @param userId the creator's user ID
     * @return list of surveys created by the user
     */
    public List<Survey> getSurveysByCreator(Long userId) {
        return userService.findById(userId)
                .map(surveyRepository::findByCreatedBy)
                .orElse(List.of());
    }
    
    /**
     * Create a new survey.
     * 
     * @param surveyDto the survey data
     * @param creatorId the creator's user ID
     * @return the created survey DTO
     */
    public SurveyDto createSurvey(SurveyDto surveyDto, Long creatorId) {
        User creator = userService.findById(creatorId)
                .orElseThrow(() -> new RuntimeException("Creator not found"));
        
        Survey survey = new Survey();
        survey.setTitle(surveyDto.getTitle());
        survey.setDescription(surveyDto.getDescription());
        survey.setStatus(Survey.SurveyStatus.valueOf(surveyDto.getStatus()));
        survey.setStartDate(surveyDto.getStartDate());
        survey.setEndDate(surveyDto.getEndDate());
        survey.setAllowMultipleResponses(surveyDto.isAllowMultipleResponses());
        survey.setRequireAuthentication(surveyDto.isRequireAuthentication());
        survey.setCreatedBy(creator);
        
        Survey savedSurvey = surveyRepository.save(survey);
        return new SurveyDto(savedSurvey);
    }
    
    /**
     * Update an existing survey.
     * 
     * @param id the survey ID
     * @param surveyDto the updated survey data
     * @return the updated survey DTO
     */
    public SurveyDto updateSurvey(Long id, SurveyDto surveyDto) {
        Survey survey = surveyRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Survey not found"));
        
        // Only allow updates if survey is not active
        if (Survey.SurveyStatus.ACTIVE.equals(survey.getStatus())) {
            throw new RuntimeException("Cannot update active survey");
        }
        
        survey.setTitle(surveyDto.getTitle());
        survey.setDescription(surveyDto.getDescription());
        survey.setStartDate(surveyDto.getStartDate());
        survey.setEndDate(surveyDto.getEndDate());
        survey.setAllowMultipleResponses(surveyDto.isAllowMultipleResponses());
        survey.setRequireAuthentication(surveyDto.isRequireAuthentication());
        
        Survey savedSurvey = surveyRepository.save(survey);
        return new SurveyDto(savedSurvey);
    }
    
    /**
     * Delete a survey.
     * 
     * @param id the survey ID
     */
    public void deleteSurvey(Long id) {
        Survey survey = surveyRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Survey not found"));
        
        // Only allow deletion if survey is not active
        if (Survey.SurveyStatus.ACTIVE.equals(survey.getStatus())) {
            throw new RuntimeException("Cannot delete active survey");
        }
        
        surveyRepository.delete(survey);
    }
    
    /**
     * Publish a survey (change status to ACTIVE).
     * 
     * @param id the survey ID
     * @return the published survey DTO
     */
    public SurveyDto publishSurvey(Long id) {
        Survey survey = surveyRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Survey not found"));
        
        if (survey.getQuestions().isEmpty()) {
            throw new RuntimeException("Cannot publish survey without questions");
        }
        
        survey.setStatus(Survey.SurveyStatus.ACTIVE);
        Survey savedSurvey = surveyRepository.save(survey);
        return new SurveyDto(savedSurvey);
    }
    
    /**
     * Schedule a survey.
     * 
     * @param id the survey ID
     * @param startDate the start date
     * @return the scheduled survey DTO
     */
    public SurveyDto scheduleSurvey(Long id, LocalDateTime startDate) {
        Survey survey = surveyRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Survey not found"));
        
        survey.setStatus(Survey.SurveyStatus.SCHEDULED);
        survey.setStartDate(startDate);
        Survey savedSurvey = surveyRepository.save(survey);
        return new SurveyDto(savedSurvey);
    }
    
    /**
     * Close a survey.
     * 
     * @param id the survey ID
     * @return the closed survey DTO
     */
    public SurveyDto closeSurvey(Long id) {
        Survey survey = surveyRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Survey not found"));
        
        survey.setStatus(Survey.SurveyStatus.CLOSED);
        Survey savedSurvey = surveyRepository.save(survey);
        return new SurveyDto(savedSurvey);
    }
    
    /**
     * Add a question to a survey.
     * 
     * @param surveyId the survey ID
     * @param question the question to add
     * @return the updated survey DTO
     */
    public SurveyDto addQuestionToSurvey(Long surveyId, Question question) {
        Survey survey = surveyRepository.findById(surveyId)
                .orElseThrow(() -> new RuntimeException("Survey not found"));
        
        if (Survey.SurveyStatus.ACTIVE.equals(survey.getStatus())) {
            throw new RuntimeException("Cannot modify active survey");
        }
        
        survey.addQuestion(question);
        Survey savedSurvey = surveyRepository.save(survey);
        return new SurveyDto(savedSurvey);
    }
    
    /**
     * Remove a question from a survey.
     * 
     * @param surveyId the survey ID
     * @param questionId the question ID
     * @return the updated survey DTO
     */
    public SurveyDto removeQuestionFromSurvey(Long surveyId, Long questionId) {
        Survey survey = surveyRepository.findById(surveyId)
                .orElseThrow(() -> new RuntimeException("Survey not found"));
        
        if (Survey.SurveyStatus.ACTIVE.equals(survey.getStatus())) {
            throw new RuntimeException("Cannot modify active survey");
        }
        
        Question questionToRemove = survey.getQuestions().stream()
                .filter(q -> q.getId().equals(questionId))
                .findFirst()
                .orElseThrow(() -> new RuntimeException("Question not found in survey"));
        
        survey.removeQuestion(questionToRemove);
        Survey savedSurvey = surveyRepository.save(survey);
        return new SurveyDto(savedSurvey);
    }
    
    /**
     * Get surveys ending soon.
     * 
     * @param days number of days to check
     * @return list of surveys ending soon
     */
    public List<Survey> getSurveysEndingSoon(int days) {
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime endDate = now.plusDays(days);
        return surveyRepository.findSurveysEndingSoon(now, endDate);
    }
    
    /**
     * Get surveys with high participation.
     * 
     * @param threshold minimum number of responses
     * @return list of surveys with high participation
     */
    public List<Survey> getSurveysWithHighParticipation(int threshold) {
        return surveyRepository.findSurveysWithHighParticipation(threshold);
    }
    
    /**
     * Search surveys by title.
     * 
     * @param title the search term
     * @return list of matching surveys
     */
    public List<Survey> searchSurveysByTitle(String title) {
        return surveyRepository.findByTitleContainingIgnoreCase(title);
    }
    
    /**
     * Get surveys with pagination and filtering.
     * 
     * @param pageable pagination parameters
     * @param status filter by status (optional)
     * @param title filter by title (optional)
     * @return page of survey DTOs
     */
    public Page<SurveyDto> getSurveysWithPagination(Pageable pageable, String status, String title) {
        List<Survey> surveys;
        
        if (status != null && !status.isEmpty() && title != null && !title.isEmpty()) {
            // Filter by both status and title
            surveys = surveyRepository.findByStatusAndTitleContainingIgnoreCaseWithQuestionsAndResponses(
                Survey.SurveyStatus.valueOf(status.toUpperCase()), title);
        } else if (status != null && !status.isEmpty()) {
            // Filter by status only
            surveys = surveyRepository.findByStatusWithQuestionsAndResponses(
                Survey.SurveyStatus.valueOf(status.toUpperCase()));
        } else if (title != null && !title.isEmpty()) {
            // Filter by title only
            surveys = surveyRepository.findByTitleContainingIgnoreCaseWithQuestionsAndResponses(title);
        } else {
            // No filters
            surveys = surveyRepository.findAllWithQuestionsAndResponses();
        }
        
        // Apply pagination manually since we're using JOIN FETCH
        int start = (int) pageable.getOffset();
        int end = Math.min((start + pageable.getPageSize()), surveys.size());
        
        List<Survey> pagedSurveys = surveys.subList(start, end);
        List<SurveyDto> surveyDtos = pagedSurveys.stream()
                .map(SurveyDto::new)
                .collect(Collectors.toList());
        
        return new PageImpl<>(surveyDtos, pageable, surveys.size());
    }
    
    /**
     * Get survey statistics.
     * 
     * @return survey statistics
     */
    public SurveyStatistics getSurveyStatistics() {
        long totalSurveys = surveyRepository.count();
        long activeSurveys = surveyRepository.countByStatus(Survey.SurveyStatus.ACTIVE);
        long draftSurveys = surveyRepository.countByStatus(Survey.SurveyStatus.DRAFT);
        long closedSurveys = surveyRepository.countByStatus(Survey.SurveyStatus.CLOSED);
        
        return new SurveyStatistics(totalSurveys, activeSurveys, draftSurveys, closedSurveys);
    }
    
    /**
     * Survey statistics class.
     */
    public static class SurveyStatistics {
        private final long totalSurveys;
        private final long activeSurveys;
        private final long draftSurveys;
        private final long closedSurveys;
        
        public SurveyStatistics(long totalSurveys, long activeSurveys, long draftSurveys, long closedSurveys) {
            this.totalSurveys = totalSurveys;
            this.activeSurveys = activeSurveys;
            this.draftSurveys = draftSurveys;
            this.closedSurveys = closedSurveys;
        }
        
        // Getters
        public long getTotalSurveys() { return totalSurveys; }
        public long getActiveSurveys() { return activeSurveys; }
        public long getDraftSurveys() { return draftSurveys; }
        public long getClosedSurveys() { return closedSurveys; }
    }
} 