package com.survey.dto;

import com.survey.model.Response;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import java.time.LocalDateTime;

/**
 * Data Transfer Object for Response entities.
 * 
 * @author Survey Team
 */
public class ResponseDto {
    
    private Long id;
    
    @NotBlank(message = "Response text is required")
    @Size(min = 1, max = 2000, message = "Response text must be between 1 and 2000 characters")
    private String text;
    
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private Long questionId;
    private String authorName;

    private int upvoteCount;
    private boolean hasUserUpvoted;
    
    // Constructors
    public ResponseDto() {}
    
    public ResponseDto(Response response) {
        this.id = response.getId();
        this.text = response.getText();
        this.createdAt = response.getCreatedAt();
        this.updatedAt = response.getUpdatedAt();

        this.upvoteCount = response.getUpvoteCount();
        this.authorName = response.getAuthorDisplayName();
        
        try {
            if (response.getQuestion() != null) {
                this.questionId = response.getQuestion().getId();
            }
        } catch (Exception e) {
            // Handle LazyInitializationException for Question entity
            this.questionId = null;
        }
    }
    
    public ResponseDto(Response response, boolean hasUserUpvoted) {
        this(response);
        this.hasUserUpvoted = hasUserUpvoted;
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
    
    public Long getQuestionId() {
        return questionId;
    }
    
    public void setQuestionId(Long questionId) {
        this.questionId = questionId;
    }
    
    public String getAuthorName() {
        return authorName;
    }
    
    public void setAuthorName(String authorName) {
        this.authorName = authorName;
    }
    

    
    public int getUpvoteCount() {
        return upvoteCount;
    }
    
    public void setUpvoteCount(int upvoteCount) {
        this.upvoteCount = upvoteCount;
    }
    
    public boolean isHasUserUpvoted() {
        return hasUserUpvoted;
    }
    
    public void setHasUserUpvoted(boolean hasUserUpvoted) {
        this.hasUserUpvoted = hasUserUpvoted;
    }
    
    @Override
    public String toString() {
        return "ResponseDto{" +
                "id=" + id +
                ", text='" + text + '\'' +
                ", authorName='" + authorName + '\'' +
                ", upvoteCount=" + upvoteCount +
                ", hasUserUpvoted=" + hasUserUpvoted +
                '}';
    }
} 