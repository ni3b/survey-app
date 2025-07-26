package com.survey;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

/**
 * Main Spring Boot application class for the Survey Application.
 * 
 * This application provides a full-stack survey platform with:
 * - User survey participation
 * - Admin survey management
 * - Response and upvote system
 * - Analytics and reporting
 * 
 * @author Survey Team
 * @version 1.0
 */
@SpringBootApplication
@EnableScheduling
public class SurveyApplication {

    private static final Logger logger = LoggerFactory.getLogger(SurveyApplication.class);

    public static void main(String[] args) {
        logger.info("Starting Survey Application...");
        logger.info("Java version: {}", System.getProperty("java.version"));
        logger.info("Spring Boot version: {}", org.springframework.boot.SpringBootVersion.getVersion());
        
        try {
            SpringApplication.run(SurveyApplication.class, args);
            logger.info("Survey Application started successfully");
        } catch (Exception e) {
            logger.error("Failed to start Survey Application", e);
            throw e;
        }
    }
} 