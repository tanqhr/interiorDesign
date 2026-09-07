package com.taico.interiorDesign.service.impl;


import jakarta.mail.MessagingException;
import jakarta.mail.Session;
import jakarta.mail.internet.MimeMessage;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mail.javamail.JavaMailSender;

import java.util.Properties;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class EmailServiceImplTest {

    @Mock
    private JavaMailSender mailSender;

    private EmailServiceImpl emailService;

    private MimeMessage mimeMessage;

    @BeforeEach
    void setUp() {

        emailService = new EmailServiceImpl(mailSender);

        mimeMessage = new MimeMessage(
                Session.getInstance(new Properties())
        );
    }


    // =========================================================
    // sendContactMessage()
    // =========================================================

    @Test
    void sendContactMessage_shouldSendEmailSuccessfully()
            throws Exception {

        // Arrange
        when(mailSender.createMimeMessage())
                .thenReturn(mimeMessage);

        String name = "Ivan Ivanov";
        String email = "ivan@gmail.com";
        String subject = "Въпрос";
        String message = "Искам да получа повече информация.";


        // Act
        emailService.sendContactMessage(
                name,
                email,
                subject,
                message
        );


        // Assert
        verify(mailSender).createMimeMessage();
        verify(mailSender).send(mimeMessage);

        assertEquals(
                "mail_000@abv.bg",
                mimeMessage.getFrom()[0].toString()
        );

        assertEquals(
                "mail_000@abv.bg",
                mimeMessage.getRecipients(
                        jakarta.mail.Message.RecipientType.TO
                )[0].toString()
        );

        assertEquals(
                email,
                mimeMessage.getReplyTo()[0].toString()
        );

        assertEquals(
                "Ново съобщение от сайта: " + subject,
                mimeMessage.getSubject()
        );

        String content =
                mimeMessage.getContent().toString();

        assertTrue(content.contains(
                "Име: " + name
        ));

        assertTrue(content.contains(
                "Имейл: " + email
        ));

        assertTrue(content.contains(
                "Съобщение:"
        ));

        assertTrue(content.contains(
                message
        ));
    }


    @Test
    void sendContactMessage_shouldThrowRuntimeException_whenMessagingExceptionOccurs() {

        // Arrange
        when(mailSender.createMimeMessage())
                .thenThrow(new RuntimeException("Mail error"));


        // Act & Assert
        RuntimeException exception =
                assertThrows(
                        RuntimeException.class,
                        () -> emailService.sendContactMessage(
                                "Ivan",
                                "ivan@gmail.com",
                                "Question",
                                "Hello"
                        )
                );

        assertEquals(
                "Mail error",
                exception.getMessage()
        );

        verify(mailSender)
                .createMimeMessage();

        verify(mailSender, never())
                .send(any(MimeMessage.class));
    }


    // =========================================================
    // sendRegistrationEmail()
    // =========================================================

    @Test
    void sendRegistrationEmail_shouldSendEmailSuccessfully()
            throws Exception {

        // Arrange
        when(mailSender.createMimeMessage())
                .thenReturn(mimeMessage);

        String email = "ivan@gmail.com";
        String name = "Ivan";


        // Act
        emailService.sendRegistrationEmail(
                email,
                name
        );


        // Assert
        verify(mailSender).createMimeMessage();
        verify(mailSender).send(mimeMessage);

        assertEquals(
                "mail_000@abv.bg",
                mimeMessage.getFrom()[0].toString()
        );

        assertEquals(
                email,
                mimeMessage.getRecipients(
                        jakarta.mail.Message.RecipientType.TO
                )[0].toString()
        );

        assertEquals(
                "Добре дошли в TA&CO!",
                mimeMessage.getSubject()
        );

        String content =
                mimeMessage.getContent().toString();

        assertTrue(content.contains(
                "Здравейте, " + name + "!"
        ));

        assertTrue(content.contains(
                "Благодарим Ви, че се регистрирахте в TA&CO."
        ));

        assertTrue(content.contains(
                "Вашият профил беше създаден успешно."
        ));

        assertTrue(content.contains(
                "Очакваме с удоволствие да работим заедно"
        ));

        assertTrue(content.contains(
                "Екипът на TA&CO"
        ));
    }


    @Test
    void sendRegistrationEmail_shouldThrowRuntimeException_whenMessagingExceptionOccurs() {

        // Arrange
        when(mailSender.createMimeMessage())
                .thenThrow(new RuntimeException("Mail error"));


        // Act & Assert
        RuntimeException exception =
                assertThrows(
                        RuntimeException.class,
                        () -> emailService.sendRegistrationEmail(
                                "ivan@gmail.com",
                                "Ivan"
                        )
                );

        assertEquals(
                "Mail error",
                exception.getMessage()
        );

        verify(mailSender)
                .createMimeMessage();

        verify(mailSender, never())
                .send(any(MimeMessage.class));
    }
}
