package yummydelivery.server.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.*;
import org.springframework.mail.javamail.JavaMailSender;
import yummydelivery.server.dto.EmailDetails;


import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

class EmailServiceTestUT {

    @Mock
    private JavaMailSender javaMailSender;
    @Value("${spring.mail.username}")
    private String emailSender;
    @InjectMocks
    private EmailService emailService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    public void sendEmail_Success() {
        EmailDetails emailDetails = EmailDetails.builder()
                .recipient("newUser@gmail.com")
                .messageBody("Success")
                .subject("Registration Success")
                .build();

        SimpleMailMessage mailMessage = new SimpleMailMessage();
        mailMessage.setFrom(emailSender);
        mailMessage.setTo(emailDetails.getRecipient());
        mailMessage.setText(emailDetails.getMessageBody());
        mailMessage.setSubject(emailDetails.getSubject());

        emailService.sendEmail(emailDetails);

        verify(javaMailSender, times(1)).send(mailMessage);
    }
}