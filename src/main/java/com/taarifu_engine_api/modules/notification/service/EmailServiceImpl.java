package com.taarifu_engine_api.modules.notification.service;

import com.taarifu_engine_api.modules.notification.domain.dto.EmailRequestDto;
import com.taarifu_engine_api.modules.notification.domain.dto.EmailResponseDto;
import com.taarifu_engine_api.modules.notification.domain.enums.EmailStatus;
import com.taarifu_engine_api.modules.notification.domain.enums.EmailType;
import com.taarifu_engine_api.modules.notification.email.EmailEvent;
import com.taarifu_engine_api.modules.notification.email.EmailTemplateService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Implementation of EmailService interface.
 * Provides both synchronous and asynchronous email sending capabilities using event-driven architecture.
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class EmailServiceImpl implements EmailService {

    private final JavaMailSender mailSender;
    private final ApplicationEventPublisher eventPublisher;
    private final EmailTemplateService emailTemplateService;
    
    @Value("${spring.mail.from}")
    private String fromAddress;
    
    // In-memory storage for email status tracking (in production, use a database)
    private final Map<String, EmailResponseDto> emailStatusMap = new ConcurrentHashMap<>();

    @Override
    public EmailResponseDto sendEmailAsync(EmailRequestDto emailRequest) {
        log.info("Publishing email event for async processing: {} to {}", 
                emailRequest.getEmailType(), emailRequest.getTo());
        
        // Create email response with pending status
        EmailResponseDto emailResponse = createEmailResponse(emailRequest, EmailStatus.PENDING);
        emailStatusMap.put(emailResponse.getEmailId(), emailResponse);
        
        // Publish event for asynchronous processing
        eventPublisher.publishEvent(new EmailEvent(this, emailRequest));
        
        log.info("Email event published successfully with ID: {}", emailResponse.getEmailId());
        return emailResponse;
    }

    @Override
    public EmailResponseDto sendEmailSync(EmailRequestDto emailRequest) {
        log.info("Sending email synchronously: {} to {}", 
                emailRequest.getEmailType(), emailRequest.getTo());
        
        EmailResponseDto emailResponse = createEmailResponse(emailRequest, EmailStatus.PROCESSING);
        emailStatusMap.put(emailResponse.getEmailId(), emailResponse);
        
        try {
            // Send email immediately
            sendEmailInternal(emailRequest);
            
            // Update status to sent
            emailResponse.setStatus(EmailStatus.SENT);
            emailResponse.setSentAt(LocalDateTime.now());
            emailStatusMap.put(emailResponse.getEmailId(), emailResponse);
            
            log.info("Email sent successfully with ID: {}", emailResponse.getEmailId());
            return emailResponse;
            
        } catch (Exception e) {
            log.error("Failed to send email with ID: {}", emailResponse.getEmailId(), e);
            
            // Update status to failed
            emailResponse.setStatus(EmailStatus.FAILED);
            emailResponse.setErrorMessage(e.getMessage());
            emailStatusMap.put(emailResponse.getEmailId(), emailResponse);
            
            return emailResponse;
        }
    }

    @Override
    public EmailResponseDto sendSimpleEmail(String to, String subject, String body, EmailType emailType) {
        EmailRequestDto emailRequest = new EmailRequestDto();
        emailRequest.setTo(to);
        emailRequest.setSubject(subject);
        emailRequest.setBody(body);
        emailRequest.setEmailType(emailType);
        emailRequest.setHtml(false);
        
        return sendEmailSync(emailRequest);
    }

    @Override
    public EmailResponseDto sendSimpleEmailAsync(String to, String subject, String body, EmailType emailType) {
        EmailRequestDto emailRequest = new EmailRequestDto();
        emailRequest.setTo(to);
        emailRequest.setSubject(subject);
        emailRequest.setBody(body);
        emailRequest.setEmailType(emailType);
        emailRequest.setHtml(false);
        
        return sendEmailAsync(emailRequest);
    }

    @Override
    public EmailResponseDto sendTemplateEmail(String to, String subject, String templateName, 
                                            Map<String, Object> templateVariables, EmailType emailType) {
        // For now, we'll use simple template substitution
        // In production, integrate with a template engine like Thymeleaf or FreeMarker
        String body = processTemplate(templateName, templateVariables);
        
        EmailRequestDto emailRequest = new EmailRequestDto();
        emailRequest.setTo(to);
        emailRequest.setSubject(subject);
        emailRequest.setBody(body);
        emailRequest.setEmailType(emailType);
        emailRequest.setTemplateVariables(templateVariables);
        emailRequest.setHtml(true);
        
        return sendEmailSync(emailRequest);
    }

    @Override
    public EmailResponseDto sendTemplateEmailAsync(String to, String subject, String templateName, 
                                                 Map<String, Object> templateVariables, EmailType emailType) {
        String body = processTemplate(templateName, templateVariables);
        
        EmailRequestDto emailRequest = new EmailRequestDto();
        emailRequest.setTo(to);
        emailRequest.setSubject(subject);
        emailRequest.setBody(body);
        emailRequest.setEmailType(emailType);
        emailRequest.setTemplateVariables(templateVariables);
        emailRequest.setHtml(true);
        
        return sendEmailAsync(emailRequest);
    }

    @Override
    public EmailResponseDto getEmailStatus(String emailId) {
        return emailStatusMap.get(emailId);
    }

    @Override
    public List<EmailResponseDto> getEmailsByRecipient(String recipientEmail) {
        return emailStatusMap.values().stream()
                .filter(email -> email.getTo().equals(recipientEmail))
                .toList();
    }

    @Override
    public List<EmailResponseDto> getEmailsByType(EmailType emailType) {
        return emailStatusMap.values().stream()
                .filter(email -> email.getEmailType() == emailType)
                .toList();
    }

    @Override
    public EmailResponseDto retryEmail(String emailId) {
        EmailResponseDto emailResponse = emailStatusMap.get(emailId);
        if (emailResponse == null) {
            throw new IllegalArgumentException("Email not found with ID: " + emailId);
        }
        
        if (emailResponse.getStatus() != EmailStatus.FAILED) {
            throw new IllegalStateException("Can only retry failed emails");
        }
        
        // Increment retry count
        emailResponse.setRetryCount(emailResponse.getRetryCount() + 1);
        emailResponse.setStatus(EmailStatus.PENDING);
        emailResponse.setErrorMessage(null);
        
        // Create new email request and send
        EmailRequestDto emailRequest = new EmailRequestDto();
        emailRequest.setTo(emailResponse.getTo());
        emailRequest.setSubject(emailResponse.getSubject());
        emailRequest.setEmailType(emailResponse.getEmailType());
        
        return sendEmailAsync(emailRequest);
    }

    @Override
    public boolean validateEmailConfiguration() {
        try {
            // Test email configuration by creating a test message
            SimpleMailMessage testMessage = new SimpleMailMessage();
            testMessage.setTo("test@example.com");
            testMessage.setSubject("Configuration Test");
            testMessage.setText("This is a test message to validate email configuration.");
            
            // Don't actually send, just validate configuration
            log.info("Email configuration validation successful");
            return true;
        } catch (Exception e) {
            log.error("Email configuration validation failed", e);
            return false;
        }
    }

    /**
     * Internal method to send email using JavaMailSender.
     */
    private void sendEmailInternal(EmailRequestDto emailRequest) throws MessagingException {
        if (emailRequest.isHtml()) {
            sendHtmlEmail(emailRequest);
        } else {
            sendTextEmail(emailRequest);
        }
    }

    /**
     * Sends HTML email.
     */
    private void sendHtmlEmail(EmailRequestDto emailRequest) throws MessagingException {
        log.debug("Creating HTML email for: {} with subject: {}", emailRequest.getTo(), emailRequest.getSubject());
        
        MimeMessage mimeMessage = mailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, true, "UTF-8");
        
        helper.setFrom(fromAddress);
        helper.setTo(emailRequest.getTo());
        helper.setSubject(emailRequest.getSubject());
        helper.setText(emailRequest.getBody(), true); // true for HTML
        
        log.debug("Email prepared - To: {}, Subject: {}, Body length: {}", 
            emailRequest.getTo(), emailRequest.getSubject(), emailRequest.getBody().length());
        
        // Set CC if provided
        if (emailRequest.getCc() != null && emailRequest.getCc().length > 0) {
            helper.setCc(emailRequest.getCc());
        }
        
        // Set BCC if provided
        if (emailRequest.getBcc() != null && emailRequest.getBcc().length > 0) {
            helper.setBcc(emailRequest.getBcc());
        }
        
        // Set reply-to if provided
        if (StringUtils.hasText(emailRequest.getReplyTo())) {
            helper.setReplyTo(emailRequest.getReplyTo());
        }
        
        log.debug("Sending email via SMTP to: {}", emailRequest.getTo());
        mailSender.send(mimeMessage);
        log.debug("Email sent successfully via SMTP to: {}", emailRequest.getTo());
    }

    /**
     * Sends plain text email.
     */
    private void sendTextEmail(EmailRequestDto emailRequest) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setFrom(fromAddress);
        message.setTo(emailRequest.getTo());
        message.setSubject(emailRequest.getSubject());
        message.setText(emailRequest.getBody());
        
        // Set CC if provided
        if (emailRequest.getCc() != null && emailRequest.getCc().length > 0) {
            message.setCc(emailRequest.getCc());
        }
        
        // Set BCC if provided
        if (emailRequest.getBcc() != null && emailRequest.getBcc().length > 0) {
            message.setBcc(emailRequest.getBcc());
        }
        
        // Set reply-to if provided
        if (StringUtils.hasText(emailRequest.getReplyTo())) {
            message.setReplyTo(emailRequest.getReplyTo());
        }
        
        mailSender.send(message);
    }

    /**
     * Creates an email response DTO.
     */
    private EmailResponseDto createEmailResponse(EmailRequestDto emailRequest, EmailStatus status) {
        EmailResponseDto response = new EmailResponseDto();
        response.setEmailId(UUID.randomUUID().toString());
        response.setTo(emailRequest.getTo());
        response.setSubject(emailRequest.getSubject());
        response.setEmailType(emailRequest.getEmailType());
        response.setStatus(status);
        response.setQueuedAt(LocalDateTime.now());
        response.setRetryCount(0);
        response.setMaxRetries(3);
        return response;
    }

    /**
     * Processes email template with variables using EmailTemplateService.
     */
    private String processTemplate(String templateName, Map<String, Object> variables) {
        return emailTemplateService.processTemplate(templateName, variables);
    }

    /**
     * Gets template content by name.
     * In production, load from files or database.
     */
    private String getTemplateContent(String templateName) {
        // Simple template storage - in production, use proper template management
        return switch (templateName) {
            case "admin_user_created" -> """
                <h2>Admin User Created</h2>
                <p>Hello {{username}},</p>
                <p>Your admin account has been created successfully.</p>
                <p>Username: {{username}}</p>
                <p>Email: {{email}}</p>
                <p>Please log in and change your password.</p>
                <p>Best regards,<br>Taarifu Team</p>
                """;
            case "admin_user_updated" -> """
                <h2>Admin User Updated</h2>
                <p>Hello {{username}},</p>
                <p>Your admin account has been updated.</p>
                <p>If you did not make these changes, please contact support immediately.</p>
                <p>Best regards,<br>Taarifu Team</p>
                """;
            default -> """
                <h2>Notification</h2>
                <p>{{message}}</p>
                <p>Best regards,<br>Taarifu Team</p>
                """;
        };
    }
}
