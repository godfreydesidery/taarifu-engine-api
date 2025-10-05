package com.taarifu_engine_api.modules.notification.email;

import com.taarifu_engine_api.modules.notification.domain.dto.EmailRequestDto;
import lombok.Getter;
import org.springframework.context.ApplicationEvent;

import java.time.LocalDateTime;

/**
 * Spring Application Event for email sending.
 * This event is published when an email needs to be sent, allowing for asynchronous processing.
 */
@Getter
public class EmailEvent extends ApplicationEvent {

    private final EmailRequestDto emailRequest;
    private final String eventId;
    private final LocalDateTime eventTime;
    private final String source;

    /**
     * Creates a new EmailEvent.
     *
     * @param source the object on which the event initially occurred
     * @param emailRequest the email request data
     */
    public EmailEvent(Object source, EmailRequestDto emailRequest) {
        super(source);
        this.emailRequest = emailRequest;
        this.eventId = generateEventId();
        this.eventTime = LocalDateTime.now();
        this.source = source.getClass().getSimpleName();
    }

    /**
     * Creates a new EmailEvent with custom source identifier.
     *
     * @param source the object on which the event initially occurred
     * @param emailRequest the email request data
     * @param sourceIdentifier custom source identifier
     */
    public EmailEvent(Object source, EmailRequestDto emailRequest, String sourceIdentifier) {
        super(source);
        this.emailRequest = emailRequest;
        this.eventId = generateEventId();
        this.eventTime = LocalDateTime.now();
        this.source = sourceIdentifier;
    }

    /**
     * Generates a unique event ID.
     *
     * @return unique event identifier
     */
    private String generateEventId() {
        return "EMAIL_EVENT_" + System.currentTimeMillis() + "_" + 
               emailRequest.getEmailType().getCode().toUpperCase();
    }

    @Override
    public String toString() {
        return String.format("EmailEvent{eventId='%s', emailType='%s', to='%s', source='%s', eventTime=%s}",
                eventId, emailRequest.getEmailType(), emailRequest.getTo(), source, eventTime);
    }
}
