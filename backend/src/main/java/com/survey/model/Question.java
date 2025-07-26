package com.survey.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * Question entity representing a question within a survey.
 * 
 * This entity supports:
 * - Question text and type
 * - Ordering within surveys
 * - Response association
 * - Required/optional questions
 * 
 * @author Survey Team
 */
@Entity
@Table(name = "questions")
public class Question {
    
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    
    @NotBlank(message = "Question text is required")
    @Size(min = 3, max = 500, message = "Question text must be between 3 and 500 characters")
    @Column(columnDefinition = "TEXT", nullable = false)
    private String text;
    
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private QuestionType type = QuestionType.TEXT;
    
    @Column(name = "order_index")
    private Integer orderIndex = 0;
    
    @Column(name = "is_required")
    private boolean required = true;
    
    @Column(name = "created_at")
    private LocalDateTime createdAt;
    
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "survey_id", nullable = false)
    private Survey survey;
    
    @OneToMany(mappedBy = "question", cascade = CascadeType.ALL, orphanRemoval = true)
    @OrderBy("createdAt DESC")
    private List<Response> responses = new ArrayList<>();
    
    @Column(name = "max_responses")
    private Integer maxResponses;
    
    @Column(name = "allow_multiple_answers")
    private boolean allowMultipleAnswers = false;
    
    // Constructors
    public Question() {
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
    }
    
    public Question(String text, QuestionType type) {
        this();
        this.text = text;
        this.type = type;
    }
    
    // Getters and Setters
    public Long getId() {
        return id;
    }
    
    public void setId(Long id) {
        this.id = id;
    }
    
    public String getText() {
        return text;
    }
    
    public void setText(String text) {
        this.text = text;
    }
    
    public QuestionType getType() {
        return type;
    }
    
    public void setType(QuestionType type) {
        this.type = type;
    }
    
    public Integer getOrderIndex() {
        return orderIndex;
    }
    
    public void setOrderIndex(Integer orderIndex) {
        this.orderIndex = orderIndex;
    }
    
    public boolean isRequired() {
        return required;
    }
    
    public void setRequired(boolean required) {
        this.required = required;
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
    
    public Survey getSurvey() {
        return survey;
    }
    
    public void setSurvey(Survey survey) {
        this.survey = survey;
    }
    
    public List<Response> getResponses() {
        return responses;
    }
    
    public void setResponses(List<Response> responses) {
        this.responses = responses;
    }
    
    public Integer getMaxResponses() {
        return maxResponses;
    }
    
    public void setMaxResponses(Integer maxResponses) {
        this.maxResponses = maxResponses;
    }
    
    public boolean isAllowMultipleAnswers() {
        return allowMultipleAnswers;
    }
    
    public void setAllowMultipleAnswers(boolean allowMultipleAnswers) {
        this.allowMultipleAnswers = allowMultipleAnswers;
    }
    
    // Helper methods
    public void addResponse(Response response) {
        responses.add(response);
        response.setQuestion(this);
    }
    
    public void removeResponse(Response response) {
        responses.remove(response);
        response.setQuestion(null);
    }
    
    public List<Response> getTopResponses(int limit) {
        return responses.stream()
                .sorted((r1, r2) -> Integer.compare(r2.getUpvotes().size(), r1.getUpvotes().size()))
                .limit(limit)
                .toList();
    }
    
    public int getTotalUpvotes() {
        return responses.stream()
                .mapToInt(response -> response.getUpvotes().size())
                .sum();
    }
    
    @PreUpdate
    public void preUpdate() {
        this.updatedAt = LocalDateTime.now();
    }
    
    @Override
    public String toString() {
        return "Question{" +
                "id=" + id +
                ", text='" + text + '\'' +
                ", type=" + type +
                ", orderIndex=" + orderIndex +
                ", required=" + required +
                ", responsesCount=" + (responses != null ? responses.size() : 0) +
                '}';
    }
    
    /**
     * Question type enumeration.
     */
    public enum QuestionType {
        TEXT,           // Free text response
        MULTIPLE_CHOICE, // Multiple choice with predefined options
        RATING,         // Rating scale (1-5, 1-10, etc.)
        YES_NO,         // Yes/No question
        LIKERT_SCALE    // Likert scale (Strongly Disagree to Strongly Agree)
    }
} 