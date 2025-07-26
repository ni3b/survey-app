package com.survey.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;

/**
 * Upvote entity representing a user's upvote on a response.
 * 
 * This entity enforces:
 * - One upvote per user per response
 * - Tracking of upvote timestamps
 * - User and response associations
 * 
 * @author Survey Team
 */
@Entity
@Table(name = "upvotes")
public class Upvote {
    
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    
    @Column(name = "created_at")
    private LocalDateTime createdAt;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "response_id", nullable = false)
    private Response response;
    
    @Column(name = "ip_address")
    private String ipAddress;
    
    // Constructors
    public Upvote() {
        this.createdAt = LocalDateTime.now();
    }
    
    public Upvote(User user, Response response) {
        this();
        this.user = user;
        this.response = response;
    }
    
    // Getters and Setters
    public Long getId() {
        return id;
    }
    
    public void setId(Long id) {
        this.id = id;
    }
    
    public LocalDateTime getCreatedAt() {
        return createdAt;
    }
    
    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
    
    public User getUser() {
        return user;
    }
    
    public void setUser(User user) {
        this.user = user;
    }
    
    public Response getResponse() {
        return response;
    }
    
    public void setResponse(Response response) {
        this.response = response;
    }
    
    public String getIpAddress() {
        return ipAddress;
    }
    
    public void setIpAddress(String ipAddress) {
        this.ipAddress = ipAddress;
    }
    
    @Override
    public String toString() {
        return "Upvote{" +
                "id=" + id +
                ", userId=" + (user != null ? user.getId() : null) +
                ", responseId=" + (response != null ? response.getId() : null) +
                ", createdAt=" + createdAt +
                '}';
    }
    
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        
        Upvote upvote = (Upvote) o;
        
        if (user != null ? !user.getId().equals(upvote.user.getId()) : upvote.user != null) return false;
        return response != null ? response.getId().equals(upvote.response.getId()) : upvote.response == null;
    }
    
    @Override
    public int hashCode() {
        int result = user != null ? user.getId().hashCode() : 0;
        result = 31 * result + (response != null ? response.getId().hashCode() : 0);
        return result;
    }
} 