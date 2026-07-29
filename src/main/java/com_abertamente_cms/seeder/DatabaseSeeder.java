package com_abertamente_cms.seeder;

import com_abertamente_cms.domain.User;
import com_abertamente_cms.domain.UserRole;
import com_abertamente_cms.repository.UserRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import org.springframework.dao.DataIntegrityViolationException;

import org.springframework.beans.factory.annotation.Value;

import java.util.Optional;

@Component
public class DatabaseSeeder implements CommandLineRunner {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Value("${app.seed.admin-email}")
    private String adminEmail;

    @Value("${app.seed.admin-password}")
    private String adminPassword;

    public DatabaseSeeder(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public void run(String... args) throws Exception {
        // 1. Criar Usuário Admin inicial (se não existir)
        Optional<User> adminOpt = userRepository.findByEmail(adminEmail);

        if (adminOpt.isEmpty()) {
            try {
                User admin = new User(
                        "Admin", "User",
                        adminEmail,
                        passwordEncoder.encode(adminPassword) // Senha via env var
                );
                admin.setRole(UserRole.ADMIN);
                userRepository.save(admin);
                System.out.println("Usuário Admin semeado: " + adminEmail);
            } catch (DataIntegrityViolationException e) {
                System.out.println("Usuário Admin já existe no banco (possivelmente deletado logicamente). Ignorando seed para não quebrar a inicialização.");
            } catch (Exception e) {
                System.out.println("Erro inesperado ao semear o usuário admin: " + e.getMessage());
            }
        }
    }
}
