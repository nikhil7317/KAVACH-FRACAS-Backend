package com.railbit.tcasanalysis.service.serviceImpl;

import com.railbit.tcasanalysis.entity.User;
import com.railbit.tcasanalysis.service.EmailService;
import jakarta.mail.*;
import jakarta.mail.internet.InternetAddress;
import jakarta.mail.internet.MimeBodyPart;
import jakarta.mail.internet.MimeMessage;
import jakarta.mail.internet.MimeMultipart;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.io.File;
import java.util.Collections;
import java.util.List;
import java.util.Properties;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Service
@Async
public class EmailServiceImpl implements EmailService {

    @Value("${email.host}")
    private String host;
    @Value("${email.port}")
    private String port;
    @Value("${email.root}")
    private String username;
    @Value("${email.password}")
    private String password;

    private static final Logger log = LoggerFactory.getLogger(EmailServiceImpl.class);
    private static final int THREAD_POOL_SIZE = 10;
    @Override
    public void sendEmail(String subject, String msg, List<String> toList) {
        mailService(subject, msg, toList);
    }

    @Override
    public void sendUsernameAndPassword(User user) {
        String subject="Account Details";
        String msg = "Dear "+user.getName()+", \n";

        msg	+= "Your login credentials to access the Kavach App are:-"
                + "\n"
                + "Email :- "
                 +user.getEmail()+ " "
                + "\n"
                + "Password :- "
                +user.getPassword()+ "\n"
                + "\n"
                + "\n"
                + "Regards,"
                + "\n"
                + "KAVACH Administration"
                + "\n";
        String to=user.getEmail();
        sendEmail(subject, msg, Collections.singletonList(to));
    }


    @Override
    public void mailService(String subject, String msg, List<String> toList) {
        try {
            ExecutorService executorService = Executors.newWorkStealingPool(THREAD_POOL_SIZE);
            executorService.execute(() -> mail(subject, msg, toList));
        } catch (Exception e){
            log.error("Exception",e);
        }
    }

    @Override
    public void sendOTP(String email, String otp) {
        String subject="Kavach Verification Code";
        String msg = "Dear User, \n";

        msg	+= "Your OTP for registration is : \n"
                + otp +
                " .";
        sendEmail(subject, msg, Collections.singletonList(email));
    }

    private void mail(String subject, String msg, List<String> toList) {
        // Get the properties
        Properties properties = new Properties();
        // Set host
        properties.put("mail.smtp.host", host);
        properties.put("mail.smtp.ssl.trust", host);
        properties.put("mail.smtp.port", port);
        properties.put("mail.smtp.auth", true);
        properties.put("mail.smtp.starttls.enable", true);

        // Get session object
        Session session = Session.getInstance(properties, new Authenticator() {
            @Override
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(username, password);
            }
        });

        try {
            Message message = new MimeMessage(session);
            message.setFrom(new InternetAddress(username));

            StringBuilder recipients = new StringBuilder();
            for (String to : toList) {
                try {
                    message.addRecipient(Message.RecipientType.TO, new InternetAddress(to));
                    recipients.append(to).append(", ");
                } catch (MessagingException e) {
                    log.error("Failed to add recipient: {}", to, e);
                }
            }

            message.setSubject(subject);
            message.setText(msg);

            Transport.send(message);
            log.info("Mail sent to: {}", recipients);
        } catch (MessagingException e) {
            log.error("Failed to send email with subject: {}", subject, e);
        }
    }

    public void mailService(String subject, String msg, List<String> toList, List<String> ccList, List<String> bccList, List<File> attachments) {
        try {
            ExecutorService executorService = Executors.newWorkStealingPool(THREAD_POOL_SIZE);
            executorService.execute(() -> mail(subject, msg, toList, ccList, bccList, attachments));
        } catch (Exception e) {
            log.error("Exception", e);
        }
    }

    private void mail(String subject, String msg, List<String> toList, List<String> ccList, List<String> bccList, List<File> attachments) {
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

        try {
            Message message = new MimeMessage(session);
            message.setFrom(new InternetAddress(username));

            for (String to : toList) {
                message.addRecipient(Message.RecipientType.TO, new InternetAddress(to));
            }
            for (String cc : ccList) {
                message.addRecipient(Message.RecipientType.CC, new InternetAddress(cc));
            }
            for (String bcc : bccList) {
                message.addRecipient(Message.RecipientType.BCC, new InternetAddress(bcc));
            }

            message.setSubject(subject);

            MimeBodyPart messageBodyPart = new MimeBodyPart();
            messageBodyPart.setText(msg);

            Multipart multipart = new MimeMultipart();
            multipart.addBodyPart(messageBodyPart);

            for (File file : attachments) {
                MimeBodyPart attachmentPart = new MimeBodyPart();
                attachmentPart.attachFile(file);
                multipart.addBodyPart(attachmentPart);
            }

            message.setContent(multipart);

            Transport.send(message);
            log.info("Mail sent to: {}", toList);
        } catch (Exception e) {
            log.error("Failed to send email with subject: {}", subject, e);
        }
    }

}
