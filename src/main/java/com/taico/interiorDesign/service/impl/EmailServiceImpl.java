package com.taico.interiorDesign.service.impl;



import com.taico.interiorDesign.service.EmailService;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

@Service
public class EmailServiceImpl implements EmailService {

    private final JavaMailSender mailSender;

    public EmailServiceImpl(JavaMailSender mailSender) {
        this.mailSender = mailSender;
    }

    @Override
    public void sendContactMessage(
            String name,
            String email,
            String subject,
            String message
    ) {

        try {

            MimeMessage mail =
                    mailSender.createMimeMessage();

            MimeMessageHelper helper =
                    new MimeMessageHelper(mail, false, "UTF-8");

            helper.setFrom("mail_000@abv.bg");
            helper.setTo("mail_000@abv.bg");
            helper.setReplyTo(email);

            helper.setSubject(
                    "Ново съобщение от сайта: " + subject
            );

            helper.setText(
                    "Име: " + name +
                            "\nИмейл: " + email +
                            "\n\nСъобщение:\n" +
                            message
            );

            mailSender.send(mail);

        } catch (MessagingException e) {

            throw new RuntimeException(
                    "Грешка при изпращането на имейла.",
                    e
            );
        }
    }


    @Override
    public void sendRegistrationEmail(
            String email,
            String name
    ) {
//        System.out.println(
//                ">>> Изпращам регистрационен имейл към: " + email
//        );
      try {

            MimeMessage mail =
                    mailSender.createMimeMessage();

            MimeMessageHelper helper =
                    new MimeMessageHelper(mail, false, "UTF-8");

            helper.setFrom("mail_000@abv.bg");
            helper.setTo(email);

            helper.setSubject(
                    "Добре дошли в TA&CO!"
            );

            helper.setText(
                    "Здравейте, " + name + "!\n\n" +
                            "Благодарим Ви, че се регистрирахте в TA&CO.\n\n" +
                            "Вашият профил беше създаден успешно.\n\n" +
                            "Очакваме с удоволствие да работим заедно " +
                            "по Вашия интериорен проект.\n\n" +
                            "Поздрави,\n" +
                            "Екипът на TA&CO"
            );

            mailSender.send(mail);

        } catch (MessagingException e) {

            throw new RuntimeException(
                    "Грешка при изпращането на регистрационния имейл.",
                    e
            );
        }
    }
}
