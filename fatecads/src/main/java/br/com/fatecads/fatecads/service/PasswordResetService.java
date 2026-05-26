package br.com.fatecads.fatecads.service;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.Base64;
import java.util.Locale;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.util.UriComponentsBuilder;

import br.com.fatecads.fatecads.entity.PasswordResetToken;
import br.com.fatecads.fatecads.entity.Usuario;
import br.com.fatecads.fatecads.exception.InvalidPasswordResetTokenException;
import br.com.fatecads.fatecads.repository.PasswordResetTokenRepository;
import br.com.fatecads.fatecads.repository.UsuarioRepository;

@Service
public class PasswordResetService {
    private static final int TOKEN_BYTES = 32;
    private static final int MIN_PASSWORD_LENGTH = 8;

    private final SecureRandom secureRandom = new SecureRandom();
    private final UsuarioRepository usuarioRepository;
    private final PasswordResetTokenRepository passwordResetTokenRepository;
    private final PasswordResetEmailService passwordResetEmailService;
    private final PasswordEncoder passwordEncoder;
    private final Duration tokenTtl;

    public PasswordResetService(
            UsuarioRepository usuarioRepository,
            PasswordResetTokenRepository passwordResetTokenRepository,
            PasswordResetEmailService passwordResetEmailService,
            PasswordEncoder passwordEncoder,
            @Value("${fatecads.password-reset.token-minutes:30}") long tokenMinutes) {
        this.usuarioRepository = usuarioRepository;
        this.passwordResetTokenRepository = passwordResetTokenRepository;
        this.passwordResetEmailService = passwordResetEmailService;
        this.passwordEncoder = passwordEncoder;
        this.tokenTtl = Duration.ofMinutes(tokenMinutes);
    }

    @Transactional
    public void requestPasswordReset(String email, String baseUrl) {
        String normalizedEmail = normalizeEmail(email);
        if (normalizedEmail.isBlank()) {
            return;
        }

        usuarioRepository.findFirstByEmailUsuarioIgnoreCaseOrderByIdUsuarioAsc(normalizedEmail)
                .ifPresent(usuario -> createResetTokenAndSendEmail(usuario, baseUrl));
    }

    @Transactional(readOnly = true)
    public boolean isTokenValid(String token) {
        return findValidToken(token, LocalDateTime.now()).isPresent();
    }

    @Transactional
    public void resetPassword(String token, String newPassword) {
        validatePassword(newPassword);

        LocalDateTime now = LocalDateTime.now();
        PasswordResetToken passwordResetToken = findValidToken(token, now)
                .orElseThrow(InvalidPasswordResetTokenException::new);

        Usuario usuario = passwordResetToken.getUsuario();
        usuario.setSenhaUsuario(passwordEncoder.encode(newPassword));
        passwordResetToken.setUsedAt(now);

        usuarioRepository.save(usuario);
        passwordResetTokenRepository.save(passwordResetToken);
    }

    private void createResetTokenAndSendEmail(Usuario usuario, String baseUrl) {
        LocalDateTime now = LocalDateTime.now();
        passwordResetTokenRepository.invalidateActiveTokens(usuario, now);

        String rawToken = generateToken();
        PasswordResetToken passwordResetToken = new PasswordResetToken(
                null,
                usuario,
                hashToken(rawToken),
                now.plus(tokenTtl),
                now,
                null);

        passwordResetTokenRepository.save(passwordResetToken);

        String resetLink = UriComponentsBuilder.fromUriString(baseUrl)
                .path("/redefinir-senha")
                .queryParam("token", rawToken)
                .build()
                .toUriString();

        passwordResetEmailService.sendPasswordResetEmail(usuario.getEmailUsuario(), resetLink);
    }

    private Optional<PasswordResetToken> findValidToken(String token, LocalDateTime now) {
        if (token == null || token.isBlank()) {
            return Optional.empty();
        }

        return passwordResetTokenRepository.findByTokenHash(hashToken(token))
                .filter(resetToken -> resetToken.getUsedAt() == null)
                .filter(resetToken -> resetToken.getExpiresAt().isAfter(now));
    }

    private void validatePassword(String password) {
        if (password == null || password.length() < MIN_PASSWORD_LENGTH) {
            throw new IllegalArgumentException("A senha deve ter pelo menos 8 caracteres.");
        }
    }

    private String generateToken() {
        byte[] tokenBytes = new byte[TOKEN_BYTES];
        secureRandom.nextBytes(tokenBytes);
        return Base64.getUrlEncoder().withoutPadding().encodeToString(tokenBytes);
    }

    private String hashToken(String token) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] tokenHash = digest.digest(token.getBytes(StandardCharsets.UTF_8));
            return Base64.getUrlEncoder().withoutPadding().encodeToString(tokenHash);
        } catch (NoSuchAlgorithmException ex) {
            throw new IllegalStateException("SHA-256 is not available.", ex);
        }
    }

    private String normalizeEmail(String email) {
        if (email == null) {
            return "";
        }

        return email.trim().toLowerCase(Locale.ROOT);
    }
}
