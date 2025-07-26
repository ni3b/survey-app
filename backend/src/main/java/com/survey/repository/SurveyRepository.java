package com.survey.repository;

import com.survey.model.Survey;
import com.survey.model.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

/**
 * Repository interface for Survey entity.
 * Provides data access methods for survey management.
 * 
 * @author Survey Team
 */
@Repository
public interface SurveyRepository extends JpaRepository<Survey, Long> {
    
    /**
     * Find all surveys with questions and responses eagerly fetched.
     * 
     * @return List of all surveys with questions and responses
     */
    @Query("SELECT DISTINCT s FROM Survey s " +
           "LEFT JOIN FETCH s.questions q")
    List<Survey> findAllWithQuestions();
    
    /**
     * Find all surveys with questions and responses eagerly fetched.
     * Note: This method fetches questions and responses separately to avoid MultipleBagFetchException.
     * 
     * @return List of all surveys with questions and responses
     */
    @Query("SELECT DISTINCT s FROM Survey s " +
           "LEFT JOIN FETCH s.questions q " +
           "LEFT JOIN FETCH q.responses r")
    List<Survey> findAllWithQuestionsAndResponses();
    
    /**
     * Find survey by ID with questions eagerly fetched.
     * 
     * @param id the survey ID
     * @return Optional containing the survey with questions if found
     */
    @Query("SELECT DISTINCT s FROM Survey s " +
           "LEFT JOIN FETCH s.questions q " +
           "WHERE s.id = :id")
    Optional<Survey> findByIdWithQuestions(@Param("id") Long id);
    
    /**
     * Find survey by ID with questions and responses eagerly fetched.
     * 
     * @param id the survey ID
     * @return Optional containing the survey with questions and responses if found
     */
    @Query("SELECT DISTINCT s FROM Survey s " +
           "LEFT JOIN FETCH s.questions q " +
           "LEFT JOIN FETCH q.responses r " +
           "WHERE s.id = :id")
    Optional<Survey> findByIdWithQuestionsAndResponses(@Param("id") Long id);
    
    /**
     * Find surveys by status.
     * 
     * @param status the status to filter by
     * @return List of surveys with the specified status
     */
    List<Survey> findByStatus(Survey.SurveyStatus status);
    
    /**
     * Find surveys by status with questions eagerly fetched.
     * 
     * @param status the status to filter by
     * @return List of surveys with the specified status and questions
     */
    @Query("SELECT DISTINCT s FROM Survey s " +
           "LEFT JOIN FETCH s.questions q " +
           "WHERE s.status = :status")
    List<Survey> findByStatusWithQuestions(@Param("status") Survey.SurveyStatus status);
    
    /**
     * Find surveys by status with questions and responses eagerly fetched.
     * 
     * @param status the status to filter by
     * @return List of surveys with the specified status and questions/responses
     */
    @Query("SELECT DISTINCT s FROM Survey s " +
           "LEFT JOIN FETCH s.questions q " +
           "LEFT JOIN FETCH q.responses r " +
           "WHERE s.status = :status")
    List<Survey> findByStatusWithQuestionsAndResponses(@Param("status") Survey.SurveyStatus status);
    
    /**
     * Find active surveys (currently running).
     * 
     * @return List of active surveys
     */
    @Query("SELECT s FROM Survey s WHERE s.status = 'ACTIVE' " +
           "AND (s.startDate IS NULL OR s.startDate <= :now) " +
           "AND (s.endDate IS NULL OR s.endDate >= :now)")
    List<Survey> findActiveSurveys(@Param("now") LocalDateTime now);
    
    /**
     * Find active surveys with questions and responses eagerly fetched.
     * 
     * @return List of active surveys with questions and responses
     */
    @Query("SELECT DISTINCT s FROM Survey s " +
           "LEFT JOIN FETCH s.questions q " +
           "LEFT JOIN FETCH q.responses r " +
           "WHERE s.status = 'ACTIVE' " +
           "AND (s.startDate IS NULL OR s.startDate <= :now) " +
           "AND (s.endDate IS NULL OR s.endDate >= :now)")
    List<Survey> findActiveSurveysWithQuestionsAndResponses(@Param("now") LocalDateTime now);
    
    /**
     * Find surveys created by a specific user.
     * 
     * @param createdBy the user who created the surveys
     * @return List of surveys created by the user
     */
    List<Survey> findByCreatedBy(User createdBy);
    
    /**
     * Find surveys by status and creator.
     * 
     * @param status the status to filter by
     * @param createdBy the user who created the surveys
     * @return List of surveys matching the criteria
     */
    List<Survey> findByStatusAndCreatedBy(Survey.SurveyStatus status, User createdBy);
    
    /**
     * Find surveys that are scheduled to start.
     * 
     * @param now current time
     * @return List of scheduled surveys
     */
    @Query("SELECT s FROM Survey s WHERE s.status = 'SCHEDULED' " +
           "AND s.startDate IS NOT NULL AND s.startDate > :now")
    List<Survey> findScheduledSurveys(@Param("now") LocalDateTime now);
    
    /**
     * Find surveys that have ended.
     * 
     * @param now current time
     * @return List of closed surveys
     */
    @Query("SELECT s FROM Survey s WHERE s.status = 'CLOSED' " +
           "OR (s.endDate IS NOT NULL AND s.endDate < :now)")
    List<Survey> findClosedSurveys(@Param("now") LocalDateTime now);
    
    /**
     * Count surveys by status.
     * 
     * @param status the status to count
     * @return number of surveys with the specified status
     */
    long countByStatus(Survey.SurveyStatus status);
    
    /**
     * Find surveys with high participation (more than threshold responses).
     * 
     * @param threshold minimum number of responses
     * @return List of surveys with high participation
     */
    @Query("SELECT s FROM Survey s WHERE SIZE(s.questions) > 0 " +
           "AND (SELECT COUNT(r) FROM Response r WHERE r.question IN (SELECT q FROM s.questions q)) >= :threshold")
    List<Survey> findSurveysWithHighParticipation(@Param("threshold") int threshold);
    
    /**
     * Find surveys ending soon (within specified days).
     * 
     * @param now current time
     * @param days number of days to check
     * @return List of surveys ending soon
     */
    @Query("SELECT s FROM Survey s WHERE s.endDate IS NOT NULL " +
           "AND s.endDate BETWEEN :now AND :endDate " +
           "AND s.status = 'ACTIVE'")
    List<Survey> findSurveysEndingSoon(@Param("now") LocalDateTime now, 
                                      @Param("endDate") LocalDateTime endDate);
    
    /**
     * Find surveys by title containing the search term.
     * 
     * @param title the search term
     * @return List of surveys with matching titles
     */
    List<Survey> findByTitleContainingIgnoreCase(String title);
    
    /**
     * Find surveys by title containing the search term with questions and responses eagerly fetched.
     * 
     * @param title the search term
     * @return List of surveys with matching titles and questions/responses
     */
    @Query("SELECT DISTINCT s FROM Survey s " +
           "LEFT JOIN FETCH s.questions q " +
           "LEFT JOIN FETCH q.responses r " +
           "WHERE LOWER(s.title) LIKE LOWER(CONCAT('%', :title, '%'))")
    List<Survey> findByTitleContainingIgnoreCaseWithQuestionsAndResponses(@Param("title") String title);
    
    /**
     * Find surveys by description containing the search term.
     * 
     * @param description the search term
     * @return List of surveys with matching descriptions
     */
    List<Survey> findByDescriptionContainingIgnoreCase(String description);
    

    
    /**
     * Find surveys that allow multiple responses.
     * 
     * @return List of surveys allowing multiple responses
     */
    List<Survey> findByAllowMultipleResponsesTrue();
    
    /**
     * Find surveys by status with pagination.
     * 
     * @param status the status to filter by
     * @param pageable pagination parameters
     * @return Page of surveys with the specified status
     */
    Page<Survey> findByStatus(Survey.SurveyStatus status, Pageable pageable);
    
    /**
     * Find surveys by title containing the search term with pagination.
     * 
     * @param title the search term
     * @param pageable pagination parameters
     * @return Page of surveys with matching titles
     */
    Page<Survey> findByTitleContainingIgnoreCase(String title, Pageable pageable);
    
    /**
     * Find surveys by status and title containing the search term with pagination.
     * 
     * @param status the status to filter by
     * @param title the search term
     * @param pageable pagination parameters
     * @return Page of surveys matching the criteria
     */
    Page<Survey> findByStatusAndTitleContainingIgnoreCase(Survey.SurveyStatus status, String title, Pageable pageable);
    
    /**
     * Find surveys by status and title containing the search term with questions and responses eagerly fetched.
     * 
     * @param status the status to filter by
     * @param title the search term
     * @return List of surveys matching the criteria with questions/responses
     */
    @Query("SELECT DISTINCT s FROM Survey s " +
           "LEFT JOIN FETCH s.questions q " +
           "LEFT JOIN FETCH q.responses r " +
           "WHERE s.status = :status " +
           "AND LOWER(s.title) LIKE LOWER(CONCAT('%', :title, '%'))")
    List<Survey> findByStatusAndTitleContainingIgnoreCaseWithQuestionsAndResponses(
        @Param("status") Survey.SurveyStatus status, @Param("title") String title);
} 