package com.survey.repository;

import com.survey.model.Response;
import com.survey.model.Upvote;
import com.survey.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

/**
 * Repository interface for Upvote entity.
 * Provides data access methods for upvote management.
 * 
 * @author Survey Team
 */
@Repository
public interface UpvoteRepository extends JpaRepository<Upvote, Long> {
    
    /**
     * Find upvotes by user.
     * 
     * @param user the user to filter by
     * @return List of upvotes by the user
     */
    List<Upvote> findByUser(User user);
    
    /**
     * Find upvotes by user ID.
     * 
     * @param userId the user ID to filter by
     * @return List of upvotes by the user
     */
    List<Upvote> findByUserId(Long userId);
    
    /**
     * Find upvotes by response.
     * 
     * @param response the response to filter by
     * @return List of upvotes for the response
     */
    List<Upvote> findByResponse(Response response);
    
    /**
     * Find upvotes by response ID.
     * 
     * @param responseId the response ID to filter by
     * @return List of upvotes for the response
     */
    List<Upvote> findByResponseId(Long responseId);
    
    /**
     * Find upvote by user and response.
     * 
     * @param user the user to filter by
     * @param response the response to filter by
     * @return Optional containing the upvote if found
     */
    Optional<Upvote> findByUserAndResponse(User user, Response response);
    
    /**
     * Find upvote by user ID and response ID.
     * 
     * @param userId the user ID to filter by
     * @param responseId the response ID to filter by
     * @return Optional containing the upvote if found
     */
    Optional<Upvote> findByUserIdAndResponseId(Long userId, Long responseId);
    
    /**
     * Check if user has upvoted a response.
     * 
     * @param userId the user ID to check
     * @param responseId the response ID to check
     * @return true if user has upvoted the response, false otherwise
     */
    boolean existsByUserIdAndResponseId(Long userId, Long responseId);
    
    /**
     * Count upvotes by response.
     * 
     * @param response the response to count upvotes for
     * @return number of upvotes for the response
     */
    long countByResponse(Response response);
    
    /**
     * Count upvotes by user.
     * 
     * @param user the user to count upvotes for
     * @return number of upvotes by the user
     */
    long countByUser(User user);
    
    /**
     * Find upvotes created within a date range.
     * 
     * @param startDate start date (inclusive)
     * @param endDate end date (inclusive)
     * @return List of upvotes created within the date range
     */
    List<Upvote> findByCreatedAtBetween(LocalDateTime startDate, LocalDateTime endDate);
    
    /**
     * Find recent upvotes.
     * 
     * @param limit maximum number of upvotes to return
     * @return List of recent upvotes ordered by creation date
     */
    @Query("SELECT u FROM Upvote u ORDER BY u.createdAt DESC")
    List<Upvote> findRecentUpvotes(int limit);
    
    /**
     * Find upvotes by IP address.
     * 
     * @param ipAddress the IP address to filter by
     * @return List of upvotes from the IP address
     */
    List<Upvote> findByIpAddress(String ipAddress);
    
    /**
     * Find upvotes for responses in a specific survey.
     * 
     * @param surveyId the survey ID to filter by
     * @return List of upvotes for responses in the survey
     */
    @Query("SELECT u FROM Upvote u WHERE u.response.question.survey.id = :surveyId")
    List<Upvote> findBySurveyId(@Param("surveyId") Long surveyId);
    
    /**
     * Find upvotes for responses to a specific question.
     * 
     * @param questionId the question ID to filter by
     * @return List of upvotes for responses to the question
     */
    @Query("SELECT u FROM Upvote u WHERE u.response.question.id = :questionId")
    List<Upvote> findByQuestionId(@Param("questionId") Long questionId);
    
    /**
     * Count upvotes for responses in a specific survey.
     * 
     * @param surveyId the survey ID to count upvotes for
     * @return number of upvotes in the survey
     */
    @Query("SELECT COUNT(u) FROM Upvote u WHERE u.response.question.survey.id = :surveyId")
    long countBySurveyId(@Param("surveyId") Long surveyId);
    
    /**
     * Count upvotes for responses to a specific question.
     * 
     * @param questionId the question ID to count upvotes for
     * @return number of upvotes for the question
     */
    @Query("SELECT COUNT(u) FROM Upvote u WHERE u.response.question.id = :questionId")
    long countByQuestionId(@Param("questionId") Long questionId);
    
    /**
     * Find upvotes created this week.
     * 
     * @param weekStart start of the week
     * @param weekEnd end of the week
     * @return List of upvotes created this week
     */
    @Query("SELECT u FROM Upvote u WHERE u.createdAt BETWEEN :weekStart AND :weekEnd")
    List<Upvote> findUpvotesThisWeek(@Param("weekStart") LocalDateTime weekStart, 
                                    @Param("weekEnd") LocalDateTime weekEnd);
    
    /**
     * Find upvotes created this month.
     * 
     * @param monthStart start of the month
     * @param monthEnd end of the month
     * @return List of upvotes created this month
     */
    @Query("SELECT u FROM Upvote u WHERE u.createdAt BETWEEN :monthStart AND :monthEnd")
    List<Upvote> findUpvotesThisMonth(@Param("monthStart") LocalDateTime monthStart, 
                                     @Param("monthEnd") LocalDateTime monthEnd);
} 