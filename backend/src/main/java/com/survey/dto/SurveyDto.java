package com.survey.dto;

import com.survey.model.Survey;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import java.time.LocalDateTime;
import java.util.List;

/**
 * Data Transfer Object for Survey entities.
 * Used for API communication between frontend and backend.
 * 
 * @author Survey Team
 */
public class SurveyDto {
    
    private Long id;
    
    @NotBlank(message = "Survey title is required")
    @Size(min = 3, max = 200, message = "Title must be between 3 and 200 characters")
    private String title;
    
    @Size(max = 1000, message = "Description cannot exceed 1000 characters")
    private String description;
    
    private String status;
    private LocalDateTime startDate;
    private LocalDateTime endDate;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private String createdBy;
    private List<QuestionDto> questions;
    private boolean anonymous;
    private boolean allowMultipleResponses;
    private int totalResponses;
    private int totalQuestions;
    
    // Constructors
    public SurveyDto() {}
    
    public SurveyDto(Survey survey) {
        this.id = survey.getId();
        this.title = survey.getTitle();
        this.description = survey.getDescription();
        this.status = survey.getStatus().name();
        this.startDate = survey.getStartDate();
        this.endDate = survey.getEndDate();
        this.createdAt = survey.getCreatedAt();
        this.updatedAt = survey.getUpdatedAt();
        this.anonymous = survey.isAnonymous();
        this.allowMultipleResponses = survey.isAllowMultipleResponses();
        this.totalQuestions = survey.getQuestions().size();
        
        // Calculate total responses across all questions
        this.totalResponses = survey.getQuestions().stream()
                .mapToInt(question -> question.getResponses().size())
                .sum();
        
        if (survey.getCreatedBy() != null) {
            this.createdBy = survey.getCreatedBy().getUsername();
        }
        
        if (survey.getQuestions() != null) {
            this.questions = survey.getQuestions().stream()
                    .map(QuestionDto::new)
                    .toList();
        }
    }
    
    // Getters and Setters
    public Long getId() {
        return id;
    }
    
    public void setId(Long id) {
        this.id = id;
    }
    
    public String getTitle() {
        return title;
    }
    
    public void setTitle(String title) {
        this.title = title;
    }
    
    public String getDescription() {
        return description;
    }
    
    public void setDescription(String description) {
        this.description = description;
    }
    
    public String getStatus() {
        return status;
    }
    
    public void setStatus(String status) {
        this.status = status;
    }
    
    public LocalDateTime getStartDate() {
        return startDate;
    }
    
    public void setStartDate(LocalDateTime startDate) {
        this.startDate = startDate;
    }
    
    public LocalDateTime getEndDate() {
        return endDate;
    }
    
    public void setEndDate(LocalDateTime endDate) {
        this.endDate = endDate;
    }
    
    public LocalDateTime getCreatedAt() {
        return createdAt;
    }
    
    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
    
    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }
    
    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }
    
    public String getCreatedBy() {
        return createdBy;
    }
    
    public void setCreatedBy(String createdBy) {
        this.createdBy = createdBy;
    }
    
    public List<QuestionDto> getQuestions() {
        return questions;
    }
    
    public void setQuestions(List<QuestionDto> questions) {
        this.questions = questions;
    }
    
    public boolean isAnonymous() {
        return anonymous;
    }
    
    public void setAnonymous(boolean anonymous) {
        this.anonymous = anonymous;
    }
    
    public boolean isAllowMultipleResponses() {
        return allowMultipleResponses;
    }
    
    public void setAllowMultipleResponses(boolean allowMultipleResponses) {
        this.allowMultipleResponses = allowMultipleResponses;
    }
    
    public int getTotalResponses() {
        return totalResponses;
    }
    
    public void setTotalResponses(int totalResponses) {
        this.totalResponses = totalResponses;
    }
    
    public int getTotalQuestions() {
        return totalQuestions;
    }
    
    public void setTotalQuestions(int totalQuestions) {
        this.totalQuestions = totalQuestions;
    }
    
    @Override
    public String toString() {
        return "SurveyDto{" +
                "id=" + id +
                ", title='" + title + '\'' +
                ", status='" + status + '\'' +
                ", totalQuestions=" + totalQuestions +
                ", totalResponses=" + totalResponses +
                '}';
    }
} 