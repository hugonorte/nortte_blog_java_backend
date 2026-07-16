package com_abertamente_cms.service;

import com_abertamente_cms.domain.PasswordResetToken;
import com_abertamente_cms.domain.User;
import com_abertamente_cms.repository.PasswordResetTokenRepository;
import com_abertamente_cms.repository.UserRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

@Service
public class PasswordResetService {

    private final UserRepository userRepository;
    private final PasswordResetTokenRepository tokenRepository;
    private final EmailService emailService;
    private final PasswordEncoder passwordEncoder;

    @Value("${frontend.url:http://localhost:3000}")
    private String frontendUrl;

    public PasswordResetService(UserRepository userRepository,
                                PasswordResetTokenRepository tokenRepository,
                                EmailService emailService,
                                PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.tokenRepository = tokenRepository;
        this.emailService = emailService;
        this.passwordEncoder = passwordEncoder;
    }

    @Transactional
    public void requestPasswordReset(String email) {
        Optional<User> userOpt = userRepository.findByEmail(email);
        
        // Em um sistema seguro não damos erro se o email não existir para não vazar info.
        if (userOpt.isEmpty()) {
            return;
        }

        User user = userOpt.get();
        String token = UUID.randomUUID().toString();
        
        // Token expira em 2 horas
        PasswordResetToken resetToken = new PasswordResetToken(token, user, LocalDateTime.now().plusHours(2));
        tokenRepository.save(resetToken);

        String resetLink = String.format("%s/redefinir-senha?token=%s&email=%s", frontendUrl, token, user.getEmail());
        
        // Executamos envio de forma síncrona ou assíncrona dependendo da config,
        // mas idealmente usar @Async em um projeto em produção.
        emailService.sendPasswordResetEmail(user.getEmail(), resetLink);
    }

    @Transactional
    public void resetPassword(String email, String token, String newPassword) {
        PasswordResetToken resetToken = tokenRepository.findByToken(token)
                .orElseThrow(() -> new IllegalArgumentException("Token inválido."));

        if (resetToken.isExpired()) {
            throw new IllegalArgumentException("Token expirado.");
        }

        if (resetToken.isUsed()) {
            throw new IllegalArgumentException("Token já utilizado.");
        }

        User user = resetToken.getUser();
        if (!user.getEmail().equalsIgnoreCase(email)) {
            throw new IllegalArgumentException("Token inválido para este e-mail.");
        }

        user.setPassword(passwordEncoder.encode(newPassword));
        userRepository.save(user);

        resetToken.setUsed(true);
        tokenRepository.save(resetToken);
    }
}
