package com.railbit.tcasanalysis.service;

import jakarta.mail.*;
import jakarta.mail.internet.InternetAddress;
import jakarta.mail.internet.MimeMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.Properties;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

public class BulkEmailService {

    private static final Logger log = LoggerFactory.getLogger(BulkEmailService.class);
    private static final int THREAD_POOL_SIZE = 10;
    private static final int BATCH_SIZE = 100;
    private static final int RETRY_COUNT = 3;

    private final ExecutorService executorService = Executors.newFixedThreadPool(THREAD_POOL_SIZE);
    private final String host;
    private final String port;
    private final String username;
    private final String password;

    public BulkEmailService(String host, String port, String username, String password) {
        this.host = host;
        this.port = port;
        this.username = username;
        this.password = password;
    }

    public void sendBulkEmails(String subject, String msg, List<String> toList) {
        for (int i = 0; i < toList.size(); i += BATCH_SIZE) {
            List<String> batch = toList.subList(i, Math.min(i + BATCH_SIZE, toList.size()));
            executorService.execute(() -> sendEmailBatch(subject, msg, batch));
        }
    }

    private void sendEmailBatch(String subject, String msg, List<String> toList) {
        for (String recipient : toList) {
            for (int attempt = 1; attempt <= RETRY_COUNT; attempt++) {
                try {
                    sendEmail(subject, msg, recipient);
                    break;
                } catch (MessagingException e) {
                    log.error("Failed to send email to {} on attempt {}/{}", recipient, attempt, RETRY_COUNT, e);
                    if (attempt == RETRY_COUNT) {
                        log.error("Giving up on sending email to {}", recipient);
                    }
                }
            }
        }
    }

    private void sendEmail(String subject, String msg, String to) throws MessagingException {
        Properties properties = new Properties();
        properties.put("mail.smtp.host", host);
        properties.put("mail.smtp.ssl.trust", host);
        properties.put("mail.smtp.port", port);
        properties.put("mail.smtp.auth", true);
        properties.put("mail.smtp.starttls.enable", true);

        Session session = Session.getInstance(properties, new Authenticator() {
            @Override
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(username, password);
            }
        });

        Message message = new MimeMessage(session);
        message.setFrom(new InternetAddress(username));
        message.setRecipient(Message.RecipientType.TO, new InternetAddress(to));
        message.setSubject(subject);
        message.setText(msg);

        Transport.send(message);
        log.info("Mail sent to: {}", to);
    }

    public void shutdown() {
        try {
            log.info("Shutting down email service...");
            executorService.shutdown();
            if (!executorService.awaitTermination(60, TimeUnit.SECONDS)) {
                executorService.shutdownNow();
            }
        } catch (InterruptedException e) {
            executorService.shutdownNow();
        }
    }
}
