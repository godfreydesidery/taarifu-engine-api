package com.taarifu_engine_api.modules.notification.sms;

import com.taarifu_engine_api.modules.notification.domain.dto.SmsRequestDto;
import lombok.Getter;
import org.springframework.context.ApplicationEvent;

import java.time.LocalDateTime;

/**
 * Spring Application Event for SMS sending.
 * This event is published when an SMS needs to be sent, allowing for asynchronous processing.
 */
@Getter
public class SmsEvent extends ApplicationEvent {

    private final SmsRequestDto smsRequest;
    private final String smsId;
    private final String eventId;
    private final LocalDateTime eventTime;
    private final String source;

    /**
     * Creates a new SmsEvent.
     *
     * @param source the object on which the event initially occurred
     * @param smsRequest the SMS request data
     * @param smsId the SMS ID for tracking
     */
    public SmsEvent(Object source, SmsRequestDto smsRequest, String smsId) {
        super(source);
        this.smsRequest = smsRequest;
        this.smsId = smsId;
        this.eventId = generateEventId();
        this.eventTime = LocalDateTime.now();
        this.source = source.getClass().getSimpleName();
    }

    /**
     * Creates a new SmsEvent with custom source identifier.
     *
     * @param source the object on which the event initially occurred
     * @param smsRequest the SMS request data
     * @param smsId the SMS ID for tracking
     * @param sourceIdentifier custom source identifier
     */
    public SmsEvent(Object source, SmsRequestDto smsRequest, String smsId, String sourceIdentifier) {
        super(source);
        this.smsRequest = smsRequest;
        this.smsId = smsId;
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
        return "SMS_EVENT_" + System.currentTimeMillis() + "_" + 
               smsRequest.getSmsType().getCode().toUpperCase();
    }

    @Override
    public String toString() {
        return String.format("SmsEvent{eventId='%s', smsType='%s', to='%s', source='%s', eventTime=%s}",
                eventId, smsRequest.getSmsType(), smsRequest.getTo(), source, eventTime);
    }
}

