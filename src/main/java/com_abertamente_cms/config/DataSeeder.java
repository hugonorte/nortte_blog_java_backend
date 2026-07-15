package com_abertamente_cms.config;

import com_abertamente_cms.domain.User;
import com_abertamente_cms.domain.UserRole;
import com_abertamente_cms.repository.UserRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;


@Configuration
public class DataSeeder {

    @Bean
    CommandLineRunner initDatabase(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        return args -> {

            // Criar o usuário Admin default se não existir
            if (userRepository.findByEmail("admin@abertamente.com").isEmpty()) {
                User admin = new User();
                admin.setFirstName("Administrador");
                admin.setLastName("");
                admin.setEmail("admin@abertamente.com");
                admin.setPassword(passwordEncoder.encode("**********")); // Senha padrão
                admin.setRole(UserRole.ADMIN);
                
                userRepository.save(admin);
                System.out.println("✅ Usuário Admin gerado (admin@abertamente.com / **********)");
            }
        };
    }
}
