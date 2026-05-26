package br.com.fatecads.fatecads.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.MailException;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
public class PasswordResetEmailService {
    private static final Logger LOGGER = LoggerFactory.getLogger(PasswordResetEmailService.class);

    private final ObjectProvider<JavaMailSender> mailSenderProvider;
    private final boolean emailEnabled;
    private final String fromAddress;

    public PasswordResetEmailService(
            ObjectProvider<JavaMailSender> mailSenderProvider,
            @Value("${fatecads.mail.enabled:false}") boolean emailEnabled,
            @Value("${fatecads.mail.from:no-reply@fatecads.local}") String fromAddress) {
        this.mailSenderProvider = mailSenderProvider;
        this.emailEnabled = emailEnabled;
        this.fromAddress = fromAddress;
    }

    public void sendPasswordResetEmail(String to, String resetLink) {
        if (!emailEnabled) {
            LOGGER.warn("Email desativado. Link de recuperacao para {}: {}", to, resetLink);
            return;
        }

        JavaMailSender mailSender = mailSenderProvider.getIfAvailable();
        if (mailSender == null) {
            throw new IllegalStateException("Email sender is not configured.");
        }

        SimpleMailMessage message = new SimpleMailMessage();
        message.setFrom(fromAddress);
        message.setTo(to);
        message.setSubject("Recuperacao de senha - Sistema Fatec ADS");
        message.setText("""
                Recebemos uma solicitacao para redefinir sua senha.

                Acesse o link abaixo para criar uma nova senha:
                %s

                Este link expira em 30 minutos. Se voce nao solicitou a recuperacao, ignore esta mensagem.
                """.formatted(resetLink));

        try {
            mailSender.send(message);
        } catch (MailException ex) {
            LOGGER.error("Falha ao enviar email de recuperacao para {}. Link de fallback: {}", to, resetLink, ex);
        }
    }
}
