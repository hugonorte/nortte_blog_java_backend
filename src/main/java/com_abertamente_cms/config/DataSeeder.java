package com_abertamente_cms.config;

import com_abertamente_cms.domain.User;
import com_abertamente_cms.domain.UserRole;
import com_abertamente_cms.repository.UserRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.dao.DataIntegrityViolationException;


@Configuration
public class DataSeeder {

    @Bean
    CommandLineRunner initDatabase(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        return args -> {

            // Criar o usuário Admin default se não existir
            if (userRepository.findByEmail("admin@abertamente.com").isEmpty()) {
                try {
                    User admin = new User();
                    admin.setFirstName("Administrador");
                    admin.setLastName("");
                    admin.setEmail("admin@abertamente.com");
                    admin.setPassword(passwordEncoder.encode("**********")); // Senha padrão
                    admin.setRole(UserRole.ADMIN);
                    
                    userRepository.save(admin);
                    System.out.println("✅ Usuário Admin gerado (admin@abertamente.com / **********)");
                } catch (DataIntegrityViolationException e) {
                    System.out.println("⚠️ Usuário Admin já existe no banco (possivelmente deletado logicamente). Ignorando seed no DataSeeder.");
                } catch (Exception e) {
                    System.out.println("❌ Erro inesperado ao semear admin no DataSeeder: " + e.getMessage());
                }
            }
        };
    }
}
