package com.survey.dto;

import com.survey.model.Question;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import java.time.LocalDateTime;
import java.util.List;

/**
 * Data Transfer Object for Question entities.
 * 
 * @author Survey Team
 */
public class QuestionDto {
    
    private Long id;
    
    @NotBlank(message = "Question text is required")
    @Size(min = 3, max = 500, message = "Question text must be between 3 and 500 characters")
    private String text;
    
    private String type;
    private Integer orderIndex;
    private boolean required;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private List<ResponseDto> responses;
    private Integer maxResponses;
    private boolean allowMultipleAnswers;
    private int totalResponses;
    private List<ResponseDto> topResponses;
    
    // Constructors
    public QuestionDto() {}
    
    public QuestionDto(Question question) {
        this.id = question.getId();
        this.text = question.getText();
        this.type = question.getType().name();
        this.orderIndex = question.getOrderIndex();
        this.required = question.isRequired();
        this.createdAt = question.getCreatedAt();
        this.updatedAt = question.getUpdatedAt();
        this.maxResponses = question.getMaxResponses();
        this.allowMultipleAnswers = question.isAllowMultipleAnswers();
        try {
            this.totalResponses = question.getResponses() != null ? question.getResponses().size() : 0;
            
            if (question.getResponses() != null) {
                this.responses = question.getResponses().stream()
                        .map(ResponseDto::new)
                        .toList();
                
                // Get top 5 responses with most upvotes
                this.topResponses = question.getTopResponses(5).stream()
                        .map(ResponseDto::new)
                        .toList();
            } else {
                this.responses = List.of();
                this.topResponses = List.of();
            }
        } catch (Exception e) {
            // Handle LazyInitializationException
            this.totalResponses = 0;
            this.responses = List.of();
            this.topResponses = List.of();
        }
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
    
    public String getType() {
        return type;
    }
    
    public void setType(String type) {
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
    
    public List<ResponseDto> getResponses() {
        return responses;
    }
    
    public void setResponses(List<ResponseDto> responses) {
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
    
    public int getTotalResponses() {
        return totalResponses;
    }
    
    public void setTotalResponses(int totalResponses) {
        this.totalResponses = totalResponses;
    }
    
    public List<ResponseDto> getTopResponses() {
        return topResponses;
    }
    
    public void setTopResponses(List<ResponseDto> topResponses) {
        this.topResponses = topResponses;
    }
    
    @Override
    public String toString() {
        return "QuestionDto{" +
                "id=" + id +
                ", text='" + text + '\'' +
                ", type='" + type + '\'' +
                ", totalResponses=" + totalResponses +
                '}';
    }
} 