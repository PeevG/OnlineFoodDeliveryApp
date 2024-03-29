package yummydelivery.server.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.MailException;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import yummydelivery.server.dto.EmailDetails;

@Service
@Slf4j
public class EmailService {

    private final JavaMailSender javaMailSender;
    @Value("${spring.mail.username}")
    private String emailSender;

    public EmailService(JavaMailSender javaMailSender) {
        this.javaMailSender = javaMailSender;
    }

    @Async
    public void sendEmail(EmailDetails emailDetails) {
        try {
            SimpleMailMessage mailMsg = new SimpleMailMessage();
            mailMsg.setFrom(emailSender);
            mailMsg.setTo(emailDetails.getRecipient());
            mailMsg.setText(emailDetails.getMessageBody());
            mailMsg.setSubject(emailDetails.getSubject());
            javaMailSender.send(mailMsg);
            log.info("Mail sent successfully");
        } catch (MailException exception) {
            log.error("Failure occurred while sending email: " + exception.getMessage());
            log.error("Root cause: " + exception.getRootCause());
        }
    }
}
