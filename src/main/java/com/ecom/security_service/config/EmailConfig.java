package com.ecom.security_service.config;

import com.ecom.security_service.dao.ApplicationSettingsRepository;
import com.ecom.security_service.dao.entity.ApplicationSetting;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.JavaMailSenderImpl;

import java.util.List;
import java.util.Properties;
import java.util.stream.Collectors;

import static com.ecom.security_service.enums.SettingType.*;

@Configuration
public class EmailConfig {
    @Autowired
    private ApplicationSettingsRepository applicationSettingsRepository;

    @Bean
    public JavaMailSender getJavaMailSender() {
        final var applicationSettings = this.applicationSettingsRepository.getMailSettings(
                List.of(
                        MAIL_HOST,
                        MAIL_USERNAME,
                        MAIL_PASSWORD,
                        MAIL_HOST,
                        MAIL_SMTP_STARTTLS_ENABLE,
                        MAIL_SMTP_AUTH,
                        MAIL_TRANSPORT_PROTOCOL,
                        MAIL_PORT,
                        MAIL_DEBUG
                )
        ).stream().collect(Collectors.toMap(
                ApplicationSetting::getSettingType,
                ApplicationSetting::getSettingValue
        ));

        JavaMailSenderImpl mailSender = new JavaMailSenderImpl();
        mailSender.setHost(applicationSettings.get(MAIL_HOST));
        mailSender.setPort(applicationSettings.get(MAIL_PORT).isEmpty() ? 587 : Integer.parseInt(applicationSettings.get(MAIL_PORT)));

        mailSender.setUsername(applicationSettings.get(MAIL_USERNAME));
        mailSender.setPassword(applicationSettings.get(MAIL_PASSWORD));

        Properties props = mailSender.getJavaMailProperties();
        props.put("mail.transport.protocol", applicationSettings.get(MAIL_TRANSPORT_PROTOCOL));
        props.put("mail.smtp.auth", applicationSettings.get(MAIL_SMTP_AUTH));
        props.put("mail.smtp.starttls.enable", applicationSettings.get(MAIL_SMTP_STARTTLS_ENABLE));
        props.put("mail.debug", applicationSettings.get(MAIL_DEBUG));

        return mailSender;
    }
}
