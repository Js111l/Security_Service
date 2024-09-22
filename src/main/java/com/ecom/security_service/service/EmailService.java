package com.ecom.security_service.service;

import com.ecom.security_service.dao.entity.VerificationToken;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

//@Service
//@Transactional
public class EmailService {

    @Autowired
    private UserService userService;
    @Autowired
    private RegistrationService verificationTokenService;
    @Autowired
    private JavaMailSender mailSender;

    public void sendRegistrationConfirmationMail(VerificationToken token) {
        var content = "<a href=http://localhost:8080/register/activation-token?token="
                + token.getUuid() + ">Link aktywacyjny</a>";

        this.send("jakubswiercz5@gmail.com",
                token.getUser().getEmail(),
                "Link aktywacyjny do rejestracji konta",
                content
        );
    }

    private void send(String from, String to, String subject, String content) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setFrom(from);
        message.setTo(to);
        message.setSubject(subject);
        message.setText(content);
        mailSender.send(message);
    }
}
