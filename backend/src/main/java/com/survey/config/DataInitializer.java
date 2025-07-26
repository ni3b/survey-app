package com.survey.config;

import com.survey.model.Question;
import com.survey.model.Survey;
import com.survey.model.User;
import com.survey.repository.SurveyRepository;
import com.survey.repository.UserRepository;
import com.survey.service.UserService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

/**
 * Data initializer to seed the database with sample data.
 * 
 * @author Survey Team
 */
@Component
public class DataInitializer implements CommandLineRunner {
    
    private static final Logger logger = LoggerFactory.getLogger(DataInitializer.class);
    
    @Autowired
    private UserService userService;
    
    @Autowired
    private SurveyRepository surveyRepository;
    
    @Autowired
    private UserRepository userRepository;
    
    @Autowired
    private PasswordEncoder passwordEncoder;
    
    @Override
    public void run(String... args) throws Exception {
        logger.info("Initializing sample data...");
        
        try {
            // Create admin user if not exists
            createAdminUser();
            
            // Create sample surveys if none exist
            if (surveyRepository.count() == 0) {
                logger.info("No surveys found, creating sample surveys...");
                createSampleSurveys();
            } else {
                logger.info("Found {} existing surveys, skipping sample survey creation", surveyRepository.count());
            }
            
            logger.info("Sample data initialization completed successfully");
        } catch (Exception e) {
            logger.error("Error during sample data initialization", e);
            throw e;
        }
    }
    
    /**
     * Create admin user if it doesn't exist.
     */
    private void createAdminUser() {
        if (!userRepository.existsByUsername("admin")) {
            try {
                User adminUser = new User();
                adminUser.setUsername("admin");
                adminUser.setPassword("admin123");
                adminUser.setEmail("admin@survey.com");
                adminUser.setRole(User.UserRole.ADMIN);
                adminUser.setActive(true);
                
                userService.createUser(adminUser);
                logger.info("Admin user created: admin/admin123");
            } catch (Exception e) {
                logger.warn("Could not create admin user using service, trying direct repository save: " + e.getMessage());
                try {
                    // Try direct repository save as fallback
                    User adminUser = new User();
                    adminUser.setUsername("admin");
                    adminUser.setPassword(passwordEncoder.encode("admin123"));
                    adminUser.setEmail("admin@survey.com");
                    adminUser.setRole(User.UserRole.ADMIN);
                    adminUser.setActive(true);
                    
                    userRepository.save(adminUser);
                    logger.info("Admin user created via repository: admin/admin123");
                } catch (Exception e2) {
                    logger.error("Failed to create admin user: " + e2.getMessage(), e2);
                    // Continue without admin user - application can still run
                }
            }
        } else {
            logger.info("Admin user already exists");
        }
    }
    
    /**
     * Create sample surveys with questions.
     */
    private void createSampleSurveys() {
        // Sample Survey 1: Employee Satisfaction
        Survey employeeSurvey = new Survey();
        employeeSurvey.setTitle("Employee Satisfaction Survey 2024");
        employeeSurvey.setDescription("Help us understand how we can improve the workplace environment and employee experience.");
        employeeSurvey.setStatus(Survey.SurveyStatus.ACTIVE);
        employeeSurvey.setStartDate(LocalDateTime.now().minusDays(7));
        employeeSurvey.setEndDate(LocalDateTime.now().plusDays(30));
        employeeSurvey.setAnonymous(true);
        employeeSurvey.setAllowMultipleResponses(false);
        employeeSurvey.setCreatedBy(userRepository.findByUsername("admin").orElse(null));
        
        // Add questions to employee survey
        Question q1 = new Question("How satisfied are you with your current work environment?", Question.QuestionType.RATING);
        q1.setRequired(true);
        q1.setOrderIndex(0);
        
        Question q2 = new Question("What would you like to see improved in the workplace?", Question.QuestionType.TEXT);
        q2.setRequired(false);
        q2.setOrderIndex(1);
        
        Question q3 = new Question("How likely are you to recommend this company as a great place to work?", Question.QuestionType.RATING);
        q3.setRequired(true);
        q3.setOrderIndex(2);
        
        Question q4 = new Question("What is your favorite aspect of working here?", Question.QuestionType.TEXT);
        q4.setRequired(false);
        q4.setOrderIndex(3);
        
        Question q5 = new Question("Do you feel valued and recognized for your contributions?", Question.QuestionType.YES_NO);
        q5.setRequired(true);
        q5.setOrderIndex(4);
        
        employeeSurvey.addQuestion(q1);
        employeeSurvey.addQuestion(q2);
        employeeSurvey.addQuestion(q3);
        employeeSurvey.addQuestion(q4);
        employeeSurvey.addQuestion(q5);
        
        surveyRepository.save(employeeSurvey);
        logger.info("Created Employee Satisfaction Survey");
        
        // Sample Survey 2: Product Feedback
        Survey productSurvey = new Survey();
        productSurvey.setTitle("Product Feedback Survey");
        productSurvey.setDescription("Share your thoughts about our latest product features and help us improve.");
        productSurvey.setStatus(Survey.SurveyStatus.ACTIVE);
        productSurvey.setStartDate(LocalDateTime.now().minusDays(3));
        productSurvey.setEndDate(LocalDateTime.now().plusDays(21));
        productSurvey.setAnonymous(false);
        productSurvey.setAllowMultipleResponses(true);
        productSurvey.setCreatedBy(userRepository.findByUsername("admin").orElse(null));
        
        // Add questions to product survey
        Question pq1 = new Question("How would you rate the overall user experience of our product?", Question.QuestionType.RATING);
        pq1.setRequired(true);
        pq1.setOrderIndex(0);
        
        Question pq2 = new Question("Which features do you use most frequently?", Question.QuestionType.TEXT);
        pq2.setRequired(false);
        pq2.setOrderIndex(1);
        
        Question pq3 = new Question("What additional features would you like to see in future updates?", Question.QuestionType.TEXT);
        pq3.setRequired(false);
        pq3.setOrderIndex(2);
        
        Question pq4 = new Question("How likely are you to recommend our product to others?", Question.QuestionType.RATING);
        pq4.setRequired(true);
        pq4.setOrderIndex(3);
        
        Question pq5 = new Question("Have you encountered any bugs or issues recently?", Question.QuestionType.YES_NO);
        pq5.setRequired(false);
        pq5.setOrderIndex(4);
        
        productSurvey.addQuestion(pq1);
        productSurvey.addQuestion(pq2);
        productSurvey.addQuestion(pq3);
        productSurvey.addQuestion(pq4);
        productSurvey.addQuestion(pq5);
        
        surveyRepository.save(productSurvey);
        logger.info("Created Product Feedback Survey");
        
        // Sample Survey 3: Customer Service
        Survey customerSurvey = new Survey();
        customerSurvey.setTitle("Customer Service Experience");
        customerSurvey.setDescription("Help us improve our customer service by sharing your recent experience.");
        customerSurvey.setStatus(Survey.SurveyStatus.SCHEDULED);
        customerSurvey.setStartDate(LocalDateTime.now().plusDays(7));
        customerSurvey.setEndDate(LocalDateTime.now().plusDays(45));
        customerSurvey.setAnonymous(true);
        customerSurvey.setAllowMultipleResponses(false);
        customerSurvey.setCreatedBy(userRepository.findByUsername("admin").orElse(null));
        
        // Add questions to customer survey
        Question cq1 = new Question("How satisfied were you with your recent customer service interaction?", Question.QuestionType.RATING);
        cq1.setRequired(true);
        cq1.setOrderIndex(0);
        
        Question cq2 = new Question("How quickly was your issue resolved?", Question.QuestionType.RATING);
        cq2.setRequired(true);
        cq2.setOrderIndex(1);
        
        Question cq3 = new Question("What could we have done better to improve your experience?", Question.QuestionType.TEXT);
        cq3.setRequired(false);
        cq3.setOrderIndex(2);
        
        Question cq4 = new Question("Would you use our customer service again?", Question.QuestionType.YES_NO);
        cq4.setRequired(true);
        cq4.setOrderIndex(3);
        
        Question cq5 = new Question("How would you describe the professionalism of our support team?", Question.QuestionType.LIKERT_SCALE);
        cq5.setRequired(true);
        cq5.setOrderIndex(4);
        
        customerSurvey.addQuestion(cq1);
        customerSurvey.addQuestion(cq2);
        customerSurvey.addQuestion(cq3);
        customerSurvey.addQuestion(cq4);
        customerSurvey.addQuestion(cq5);
        
        surveyRepository.save(customerSurvey);
        logger.info("Created Customer Service Survey");
        
        // Sample Survey 4: Event Feedback (Draft)
        Survey eventSurvey = new Survey();
        eventSurvey.setTitle("Annual Conference Feedback");
        eventSurvey.setDescription("Share your feedback about the recent annual conference to help us plan next year's event.");
        eventSurvey.setStatus(Survey.SurveyStatus.DRAFT);
        eventSurvey.setAnonymous(false);
        eventSurvey.setAllowMultipleResponses(false);
        eventSurvey.setCreatedBy(userRepository.findByUsername("admin").orElse(null));
        
        // Add questions to event survey
        Question eq1 = new Question("How would you rate the overall conference experience?", Question.QuestionType.RATING);
        eq1.setRequired(true);
        eq1.setOrderIndex(0);
        
        Question eq2 = new Question("Which sessions were most valuable to you?", Question.QuestionType.TEXT);
        eq2.setRequired(false);
        eq2.setOrderIndex(1);
        
        Question eq3 = new Question("What topics would you like to see covered in next year's conference?", Question.QuestionType.TEXT);
        eq3.setRequired(false);
        eq3.setOrderIndex(2);
        
        Question eq4 = new Question("How would you rate the networking opportunities?", Question.QuestionType.RATING);
        eq4.setRequired(true);
        eq4.setOrderIndex(3);
        
        Question eq5 = new Question("Would you recommend this conference to colleagues?", Question.QuestionType.YES_NO);
        eq5.setRequired(true);
        eq5.setOrderIndex(4);
        
        eventSurvey.addQuestion(eq1);
        eventSurvey.addQuestion(eq2);
        eventSurvey.addQuestion(eq3);
        eventSurvey.addQuestion(eq4);
        eventSurvey.addQuestion(eq5);
        
        surveyRepository.save(eventSurvey);
        logger.info("Created Event Feedback Survey (Draft)");
    }
} 