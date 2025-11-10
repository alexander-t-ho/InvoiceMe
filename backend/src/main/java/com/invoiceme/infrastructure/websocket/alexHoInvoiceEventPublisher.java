package com.invoiceme.infrastructure.websocket;

import com.amazonaws.auth.DefaultAWSCredentialsProviderChain;
import com.amazonaws.services.sns.AmazonSNS;
import com.amazonaws.services.sns.AmazonSNSClientBuilder;
import com.amazonaws.services.sns.model.PublishRequest;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 * Publisher for invoice events to AWS SNS.
 * All functions prefixed with "alexHo" as requested.
 * 
 * This component publishes invoice events to the alexHoInvoiceEvents SNS topic,
 * which triggers the alexHoSendInvoiceUpdate Lambda function to send real-time
 * updates via WebSocket to connected clients.
 */
@Component
public class alexHoInvoiceEventPublisher {
    
    private static final Logger logger = LoggerFactory.getLogger(alexHoInvoiceEventPublisher.class);
    
    private final AmazonSNS snsClient;
    private final ObjectMapper objectMapper;
    private final String topicArn;
    
    public alexHoInvoiceEventPublisher(
            @Value("${aws.sns.invoice-events-topic-arn:}") String topicArn,
            @Value("${aws.region:us-east-1}") String region) {
        this.topicArn = topicArn;
        this.objectMapper = new ObjectMapper();
        
        // Initialize SNS client
        this.snsClient = AmazonSNSClientBuilder.standard()
                .withRegion(region)
                .withCredentials(DefaultAWSCredentialsProviderChain.getInstance())
                .build();
        
        logger.info("alexHoInvoiceEventPublisher initialized with topic: {}", topicArn);
    }
    
    /**
     * Publish invoice created event.
     */
    public void alexHoPublishInvoiceCreated(UUID invoiceId, UUID customerId, Map<String, Object> invoiceData) {
        alexHoPublishEvent("INVOICE_CREATED", invoiceId, customerId, invoiceData);
    }
    
    /**
     * Publish invoice updated event.
     */
    public void alexHoPublishInvoiceUpdated(UUID invoiceId, UUID customerId, Map<String, Object> invoiceData) {
        alexHoPublishEvent("INVOICE_UPDATED", invoiceId, customerId, invoiceData);
    }
    
    /**
     * Publish invoice sent event.
     */
    public void alexHoPublishInvoiceSent(UUID invoiceId, UUID customerId, Map<String, Object> invoiceData) {
        alexHoPublishEvent("INVOICE_SENT", invoiceId, customerId, invoiceData);
    }
    
    /**
     * Publish invoice paid event.
     */
    public void alexHoPublishInvoicePaid(UUID invoiceId, UUID customerId, Map<String, Object> invoiceData) {
        alexHoPublishEvent("INVOICE_PAID", invoiceId, customerId, invoiceData);
    }
    
    /**
     * Publish payment recorded event.
     */
    public void alexHoPublishPaymentRecorded(UUID invoiceId, UUID customerId, Map<String, Object> invoiceData) {
        alexHoPublishEvent("PAYMENT_RECORDED", invoiceId, customerId, invoiceData);
    }
    
    /**
     * Generic method to publish invoice events.
     */
    private void alexHoPublishEvent(String eventType, UUID invoiceId, UUID customerId, Map<String, Object> invoiceData) {
        if (topicArn == null || topicArn.isEmpty()) {
            logger.debug("alexHoInvoiceEventPublisher - SNS topic ARN not configured, skipping event: {}", eventType);
            return;
        }
        
        try {
            Map<String, Object> message = new HashMap<>();
            message.put("eventType", eventType);
            message.put("invoiceId", invoiceId.toString());
            message.put("customerId", customerId != null ? customerId.toString() : null);
            message.put("invoice", invoiceData);
            message.put("timestamp", System.currentTimeMillis());
            
            String messageBody = objectMapper.writeValueAsString(message);
            
            PublishRequest request = new PublishRequest()
                    .withTopicArn(topicArn)
                    .withMessage(messageBody);
            
            snsClient.publish(request);
            
            logger.info("alexHoInvoiceEventPublisher - Published {} event for invoice: {}", eventType, invoiceId);
        } catch (Exception e) {
            logger.error("alexHoInvoiceEventPublisher - Error publishing event {} for invoice {}: {}", 
                    eventType, invoiceId, e.getMessage(), e);
        }
    }
}

