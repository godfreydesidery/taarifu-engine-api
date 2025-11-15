package com.taarifu_engine_api.modules.notification.sms;

import com.taarifu_engine_api.modules.notification.domain.dto.SmsRequestDto;
import com.taarifu_engine_api.modules.notification.domain.dto.SmsResponseDto;
import com.taarifu_engine_api.modules.notification.domain.enums.SmsStatus;
import com.taarifu_engine_api.modules.notification.service.SmsServiceImpl;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

/**
 * Event listener for processing SMS events asynchronously.
 * This component listens for SmsEvent and processes them without blocking the main thread.
 */
@Component
@RequiredArgsConstructor
@Slf4j
public class SmsEventListener {

    private final SmsServiceImpl smsService;

    /**
     * Handles SmsEvent asynchronously.
     * This method is called when an SmsEvent is published and processes the SMS sending.
     *
     * @param smsEvent the SMS event to process
     */
    @EventListener
    @Async("notificationTaskExecutor")
    public void handleSmsEvent(SmsEvent smsEvent) {
        SmsRequestDto smsRequest = smsEvent.getSmsRequest();
        String smsId = smsEvent.getSmsId();
        String eventId = smsEvent.getEventId();
        
        log.info("Processing SMS event asynchronously: {} (SMS ID: {}) for recipient: {}", 
                eventId, smsId, smsRequest.getTo());
        
        try {
            // Update status to processing
            SmsResponseDto processingResponse = new SmsResponseDto();
            processingResponse.setStatus(SmsStatus.PROCESSING);
            smsService.updateSmsStatus(smsId, processingResponse);
            
            // Send the SMS
            SmsResponseDto providerResponse = smsService.sendSmsInternal(smsRequest);
            
            // Update status in SmsServiceImpl's status map using the original SMS ID
            smsService.updateSmsStatus(smsId, providerResponse);
            
            log.info("SMS event processed successfully: {} (SMS ID: {}) for recipient: {}", 
                    eventId, smsId, smsRequest.getTo());
            
        } catch (Exception e) {
            log.error("Failed to process SMS event: {} (SMS ID: {}) for recipient: {}", 
                    eventId, smsId, smsRequest.getTo(), e);
            
            // Update status to failed
            SmsResponseDto failedResponse = new SmsResponseDto();
            failedResponse.setStatus(SmsStatus.FAILED);
            failedResponse.setErrorMessage(e.getMessage());
            smsService.updateSmsStatus(smsId, failedResponse);
        }
    }
}

