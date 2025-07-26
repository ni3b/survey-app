package com.survey.repository;

import com.survey.model.Question;
import com.survey.model.Response;
import com.survey.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Repository interface for Response entity.
 * Provides data access methods for response management.
 * 
 * @author Survey Team
 */
@Repository
public interface ResponseRepository extends JpaRepository<Response, Long> {
    
    /**
     * Find responses by question.
     * 
     * @param question the question to filter by
     * @return List of responses for the question
     */
    List<Response> findByQuestion(Question question);
    
    /**
     * Find responses by question ID.
     * 
     * @param questionId the question ID to filter by
     * @return List of responses for the question
     */
    List<Response> findByQuestionId(Long questionId);
    
    /**
     * Find responses by user.
     * 
     * @param user the user to filter by
     * @return List of responses by the user
     */
    List<Response> findByUser(User user);
    
    /**
     * Find responses by user ID.
     * 
     * @param userId the user ID to filter by
     * @return List of responses by the user
     */
    List<Response> findByUserId(Long userId);
    

    
    /**
     * Find responses by question and user.
     * 
     * @param question the question to filter by
     * @param user the user to filter by
     * @return List of responses by the user for the question
     */
    List<Response> findByQuestionAndUser(Question question, User user);
    
    /**
     * Find responses by question ID and user ID.
     * 
     * @param questionId the question ID to filter by
     * @param userId the user ID to filter by
     * @return List of responses by the user for the question
     */
    List<Response> findByQuestionIdAndUserId(Long questionId, Long userId);
    
    /**
     * Count responses by question.
     * 
     * @param question the question to count responses for
     * @return number of responses for the question
     */
    long countByQuestion(Question question);
    
    /**
     * Count responses by user.
     * 
     * @param user the user to count responses for
     * @return number of responses by the user
     */
    long countByUser(User user);
    
    /**
     * Find top responses by upvote count.
     * 
     * @param question the question to filter by
     * @param limit maximum number of responses to return
     * @return List of top responses ordered by upvote count
     */
    @Query("SELECT r FROM Response r WHERE r.question = ?1 " +
           "ORDER BY SIZE(r.upvotes) DESC")
    List<Response> findTopResponsesByUpvotes(Question question, int limit);
    
    /**
     * Find responses created within a date range.
     * 
     * @param startDate start date (inclusive)
     * @param endDate end date (inclusive)
     * @return List of responses created within the date range
     */
    List<Response> findByCreatedAtBetween(LocalDateTime startDate, LocalDateTime endDate);
    
    /**
     * Find responses by text containing the search term.
     * 
     * @param text the search term
     * @return List of responses with matching text
     */
    List<Response> findByTextContainingIgnoreCase(String text);
    
    /**
     * Find responses with most upvotes.
     * 
     * @param limit maximum number of responses to return
     * @return List of responses ordered by upvote count
     */
    @Query("SELECT r FROM Response r ORDER BY SIZE(r.upvotes) DESC")
    List<Response> findResponsesWithMostUpvotes(int limit);
    
    /**
     * Find recent responses.
     * 
     * @param limit maximum number of responses to return
     * @return List of recent responses ordered by creation date
     */
    @Query("SELECT r FROM Response r ORDER BY r.createdAt DESC")
    List<Response> findRecentResponses(int limit);
    
    /**
     * Find responses by IP address.
     * 
     * @param ipAddress the IP address to filter by
     * @return List of responses from the IP address
     */
    List<Response> findByIpAddress(String ipAddress);
    

    
    /**
     * Find responses that have been upvoted by a specific user.
     * 
     * @param user the user who upvoted
     * @return List of responses upvoted by the user
     */
    @Query("SELECT r FROM Response r WHERE r IN " +
           "(SELECT u.response FROM Upvote u WHERE u.user = :user)")
    List<Response> findResponsesUpvotedByUser(@Param("user") User user);
    
    /**
     * Find responses that haven't been upvoted by a specific user.
     * 
     * @param user the user to check
     * @return List of responses not upvoted by the user
     */
    @Query("SELECT r FROM Response r WHERE r NOT IN " +
           "(SELECT u.response FROM Upvote u WHERE u.user = :user)")
    List<Response> findResponsesNotUpvotedByUser(@Param("user") User user);
    
    /**
     * Find responses for questions in a survey.
     * 
     * @param surveyId the survey ID
     * @return List of responses for the survey's questions
     */
    @Query("SELECT r FROM Response r WHERE r.question.survey.id = :surveyId")
    List<Response> findResponsesBySurveyId(@Param("surveyId") Long surveyId);
} 