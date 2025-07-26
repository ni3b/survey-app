package com.survey.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * Response entity representing a user's response to a question.
 * 
 * This entity supports:
 * - Text responses to questions
 * - Anonymous responses
 * - Upvote system
 * - User association (optional for anonymous surveys)
 * 
 * @author Survey Team
 */
@Entity
@Table(name = "responses")
public class Response {
    
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    
    @NotBlank(message = "Response text is required")
    @Size(min = 1, max = 2000, message = "Response text must be between 1 and 2000 characters")
    @Column(columnDefinition = "TEXT", nullable = false)
    private String text;
    
    @Column(name = "created_at")
    private LocalDateTime createdAt;
    
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "question_id", nullable = false)
    private Question question;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;
    
    @Column(name = "is_anonymous")
    private boolean anonymous = false;
    
    @OneToMany(mappedBy = "response", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Upvote> upvotes = new ArrayList<>();
    
    @Column(name = "ip_address")
    private String ipAddress;
    
    @Column(name = "user_agent")
    private String userAgent;
    
    // Constructors
    public Response() {
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
    }
    
    public Response(String text) {
        this();
        this.text = text;
    }
    
    public Response(String text, boolean anonymous) {
        this(text);
        this.anonymous = anonymous;
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
    
    public Question getQuestion() {
        return question;
    }
    
    public void setQuestion(Question question) {
        this.question = question;
    }
    
    public User getUser() {
        return user;
    }
    
    public void setUser(User user) {
        this.user = user;
    }
    
    public boolean isAnonymous() {
        return anonymous;
    }
    
    public void setAnonymous(boolean anonymous) {
        this.anonymous = anonymous;
    }
    
    public List<Upvote> getUpvotes() {
        return upvotes;
    }
    
    public void setUpvotes(List<Upvote> upvotes) {
        this.upvotes = upvotes;
    }
    
    public String getIpAddress() {
        return ipAddress;
    }
    
    public void setIpAddress(String ipAddress) {
        this.ipAddress = ipAddress;
    }
    
    public String getUserAgent() {
        return userAgent;
    }
    
    public void setUserAgent(String userAgent) {
        this.userAgent = userAgent;
    }
    
    // Helper methods
    public void addUpvote(Upvote upvote) {
        upvotes.add(upvote);
        upvote.setResponse(this);
    }
    
    public void removeUpvote(Upvote upvote) {
        upvotes.remove(upvote);
        upvote.setResponse(null);
    }
    
    public int getUpvoteCount() {
        return upvotes.size();
    }
    
    public boolean hasUserUpvoted(User user) {
        if (user == null) return false;
        return upvotes.stream()
                .anyMatch(upvote -> user.getId().equals(upvote.getUser().getId()));
    }
    
    public String getAuthorDisplayName() {
        if (anonymous) {
            return "Anonymous";
        }
        return user != null ? user.getUsername() : "Unknown";
    }
    
    @PreUpdate
    public void preUpdate() {
        this.updatedAt = LocalDateTime.now();
    }
    
    @Override
    public String toString() {
        return "Response{" +
                "id=" + id +
                ", text='" + text + '\'' +
                ", anonymous=" + anonymous +
                ", upvotesCount=" + (upvotes != null ? upvotes.size() : 0) +
                ", createdAt=" + createdAt +
                '}';
    }
} 