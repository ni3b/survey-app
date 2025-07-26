package com.survey.repository;

import com.survey.model.Question;
import com.survey.model.Survey;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Repository interface for Question entity.
 * Provides data access methods for question management.
 * 
 * @author Survey Team
 */
@Repository
public interface QuestionRepository extends JpaRepository<Question, Long> {
    
    /**
     * Find questions by survey.
     * 
     * @param survey the survey to filter by
     * @return List of questions in the survey, ordered by orderIndex
     */
    List<Question> findBySurveyOrderByOrderIndex(Survey survey);
    
    /**
     * Find questions by survey ID.
     * 
     * @param surveyId the survey ID to filter by
     * @return List of questions in the survey, ordered by orderIndex
     */
    List<Question> findBySurveyIdOrderByOrderIndex(Long surveyId);
    
    /**
     * Find questions by type.
     * 
     * @param type the question type to filter by
     * @return List of questions with the specified type
     */
    List<Question> findByType(Question.QuestionType type);
    
    /**
     * Find required questions by survey.
     * 
     * @param survey the survey to filter by
     * @return List of required questions in the survey
     */
    List<Question> findBySurveyAndRequiredTrue(Survey survey);
    
    /**
     * Find optional questions by survey.
     * 
     * @param survey the survey to filter by
     * @return List of optional questions in the survey
     */
    List<Question> findBySurveyAndRequiredFalse(Survey survey);
    
    /**
     * Count questions by survey.
     * 
     * @param survey the survey to count questions for
     * @return number of questions in the survey
     */
    long countBySurvey(Survey survey);
    
    /**
     * Count required questions by survey.
     * 
     * @param survey the survey to count required questions for
     * @return number of required questions in the survey
     */
    long countBySurveyAndRequiredTrue(Survey survey);
    
    /**
     * Find questions with responses.
     * 
     * @param survey the survey to filter by
     * @return List of questions that have responses
     */
    @Query("SELECT q FROM Question q WHERE q.survey = :survey AND SIZE(q.responses) > 0")
    List<Question> findQuestionsWithResponses(@Param("survey") Survey survey);
    
    /**
     * Find questions without responses.
     * 
     * @param survey the survey to filter by
     * @return List of questions that have no responses
     */
    @Query("SELECT q FROM Question q WHERE q.survey = :survey AND SIZE(q.responses) = 0")
    List<Question> findQuestionsWithoutResponses(@Param("survey") Survey survey);
    
    /**
     * Find questions by text containing the search term.
     * 
     * @param text the search term
     * @return List of questions with matching text
     */
    List<Question> findByTextContainingIgnoreCase(String text);
    
    /**
     * Find questions with most responses.
     * 
     * @param limit maximum number of questions to return
     * @return List of questions ordered by response count
     */
    @Query("SELECT q FROM Question q ORDER BY SIZE(q.responses) DESC")
    List<Question> findQuestionsWithMostResponses(int limit);
    
    /**
     * Find questions with most upvotes.
     * 
     * @param limit maximum number of questions to return
     * @return List of questions ordered by total upvotes
     */
    @Query("SELECT q FROM Question q ORDER BY " +
           "(SELECT COUNT(u) FROM Upvote u WHERE u.response IN (SELECT r FROM q.responses r)) DESC")
    List<Question> findQuestionsWithMostUpvotes(int limit);
    
    /**
     * Find questions by order index range.
     * 
     * @param survey the survey to filter by
     * @param startIndex starting order index (inclusive)
     * @param endIndex ending order index (inclusive)
     * @return List of questions in the specified range
     */
    List<Question> findBySurveyAndOrderIndexBetweenOrderByOrderIndex(
            Survey survey, Integer startIndex, Integer endIndex);
    
    /**
     * Find the next order index for a survey.
     * 
     * @param surveyId the survey ID
     * @return the next available order index
     */
    @Query("SELECT COALESCE(MAX(q.orderIndex), -1) + 1 FROM Question q WHERE q.survey.id = :surveyId")
    Integer findNextOrderIndex(@Param("surveyId") Long surveyId);
} 