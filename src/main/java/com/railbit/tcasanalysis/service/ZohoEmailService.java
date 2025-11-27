package com.railbit.tcasanalysis.service;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.MailException;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.stereotype.Service;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;

@Service
public class ZohoEmailService {

    private static final Logger logger = LoggerFactory.getLogger(ZohoEmailService.class);

    @Autowired
    private JavaMailSender mailSender;

    @Autowired
    private ThreadPoolTaskExecutor taskExecutor; // For parallel processing

    private static final int BATCH_SIZE = 5; // Send 5 emails at a time
    private static final long DELAY_BETWEEN_BATCHES_MS = 60000; // 60 seconds delay between batches

    /**
     * Send an email to a single recipient
     */
    public void sendEmail(String to, String subject, String body) {
        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true);

            helper.setFrom("scr.fracas@zohomail.in");
            helper.setTo(to);
            helper.setSubject(subject);
            helper.setText(body, true);

            mailSender.send(message);
            logger.info("✅ Email sent successfully to {}", to);
        } catch (MailException | MessagingException e) {
            logger.error("❌ Failed to send email to {}: {}", to, e.getMessage());
        }
    }

    /**
     * Send bulk emails with batch processing and delays
     */
    public void sendBulkEmails(List<String> recipients, String subject, String body) {
        logger.info("📨 Starting bulk email sending... Total recipients: {}", recipients.size());

        for (int i = 0; i < recipients.size(); i += BATCH_SIZE) {
            int end = Math.min(i + BATCH_SIZE, recipients.size());
            List<String> batch = recipients.subList(i, end);

            CompletableFuture.runAsync(() -> batch.forEach(email -> sendEmail(email, subject, body)), taskExecutor);

            if (end < recipients.size()) {
                try {
                    logger.info("⏳ Waiting {} seconds before sending next batch...", DELAY_BETWEEN_BATCHES_MS / 1000);
                    TimeUnit.MILLISECONDS.sleep(DELAY_BETWEEN_BATCHES_MS);
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                }
            }
        }

        logger.info("✅ Bulk email process completed.");
    }
}

