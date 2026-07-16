package com_abertamente_cms.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
public class EmailService {

    private final JavaMailSender mailSender;

    @Value("${spring.mail.username:noreply@abertamente.com}")
    private String fromEmail;

    public EmailService(JavaMailSender mailSender) {
        this.mailSender = mailSender;
    }

    public void sendPasswordResetEmail(String to, String resetLink) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setFrom(fromEmail);
        message.setTo(to);
        message.setSubject("Recuperação de Senha");
        message.setText("Olá,\n\nVocê solicitou a recuperação de senha. "
                + "Clique no link abaixo para redefinir sua senha:\n\n"
                + resetLink
                + "\n\nSe você não solicitou, por favor, ignore este e-mail.");

        mailSender.send(message);
    }
}
