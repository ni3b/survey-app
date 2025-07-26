package com.survey.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

/**
 * Survey entity representing a survey with questions and responses.
 * 
 * This entity supports:
 * - Survey creation and management
 * - Scheduling with start/end dates
 * - Status tracking (draft, active, closed)
 * - Question association
 * 
 * @author Survey Team
 */
@Entity
@Table(name = "surveys")
public class Survey {
    
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    
    @NotBlank(message = "Survey title is required")
    @Size(min = 3, max = 200, message = "Title must be between 3 and 200 characters")
    @Column(nullable = false)
    private String title;
    
    @Size(max = 1000, message = "Description cannot exceed 1000 characters")
    @Column(columnDefinition = "TEXT")
    private String description;
    
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private SurveyStatus status = SurveyStatus.DRAFT;
    
    @Column(name = "start_date")
    private LocalDateTime startDate;
    
    @Column(name = "end_date")
    private LocalDateTime endDate;
    
    @Column(name = "created_at")
    private LocalDateTime createdAt;
    
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "created_by")
    private User createdBy;
    
    @OneToMany(mappedBy = "survey", cascade = CascadeType.ALL, orphanRemoval = true)
    @OrderBy("orderIndex ASC")
    private Set<Question> questions = new HashSet<>();
    

    
    @Column(name = "allow_multiple_responses")
    private boolean allowMultipleResponses = false;
    
    @Column(name = "require_authentication")
    private boolean requireAuthentication = false;
    
    // Constructors
    public Survey() {
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
    }
    
    public Survey(String title, String description) {
        this();
        this.title = title;
        this.description = description;
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
    
    public SurveyStatus getStatus() {
        return status;
    }
    
    public void setStatus(SurveyStatus status) {
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
    
    public User getCreatedBy() {
        return createdBy;
    }
    
    public void setCreatedBy(User createdBy) {
        this.createdBy = createdBy;
    }
    
    public Set<Question> getQuestions() {
        return questions;
    }
    
    public void setQuestions(Set<Question> questions) {
        this.questions = questions;
    }
    

    
    public boolean isAllowMultipleResponses() {
        return allowMultipleResponses;
    }
    
    public void setAllowMultipleResponses(boolean allowMultipleResponses) {
        this.allowMultipleResponses = allowMultipleResponses;
    }
    
    public boolean isRequireAuthentication() {
        return requireAuthentication;
    }
    
    public void setRequireAuthentication(boolean requireAuthentication) {
        this.requireAuthentication = requireAuthentication;
    }
    
    // Helper methods
    public boolean isActive() {
        return SurveyStatus.ACTIVE.equals(this.status) &&
               (this.startDate == null || LocalDateTime.now().isAfter(this.startDate) || LocalDateTime.now().isEqual(this.startDate)) &&
               (this.endDate == null || LocalDateTime.now().isBefore(this.endDate) || LocalDateTime.now().isEqual(this.endDate));
    }
    
    public boolean isScheduled() {
        return SurveyStatus.SCHEDULED.equals(this.status) &&
               this.startDate != null &&
               LocalDateTime.now().isBefore(this.startDate);
    }
    
    public boolean isClosed() {
        return SurveyStatus.CLOSED.equals(this.status) ||
               (this.endDate != null && LocalDateTime.now().isAfter(this.endDate));
    }
    
    public void addQuestion(Question question) {
        questions.add(question);
        question.setSurvey(this);
        question.setOrderIndex(questions.size() - 1);
    }
    
    public void removeQuestion(Question question) {
        questions.remove(question);
        question.setSurvey(null);
    }
    
    @PreUpdate
    public void preUpdate() {
        this.updatedAt = LocalDateTime.now();
    }
    
    @Override
    public String toString() {
        return "Survey{" +
                "id=" + id +
                ", title='" + title + '\'' +
                ", status=" + status +
                ", startDate=" + startDate +
                ", endDate=" + endDate +
                ", questionsCount=" + (questions != null ? questions.size() : 0) +
                '}';
    }
    
    /**
     * Survey status enumeration.
     */
    public enum SurveyStatus {
        DRAFT,      // Survey is being created/edited
        SCHEDULED,  // Survey is scheduled to start
        ACTIVE,     // Survey is currently active
        CLOSED      // Survey has ended
    }
} 