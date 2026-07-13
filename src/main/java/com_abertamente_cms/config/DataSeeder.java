package com_abertamente_cms.config;

import com_abertamente_cms.domain.Role;
import com_abertamente_cms.domain.User;
import com_abertamente_cms.repository.RoleRepository;
import com_abertamente_cms.repository.UserRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Set;

@Configuration
public class DataSeeder {

    @Bean
    CommandLineRunner initDatabase(UserRepository userRepository, RoleRepository roleRepository, PasswordEncoder passwordEncoder) {
        return args -> {
            // Garantir que as roles existem
            Role adminRole = roleRepository.findByName("ADMIN").orElseGet(() -> roleRepository.save(new Role("ADMIN")));
            Role userRole = roleRepository.findByName("USER").orElseGet(() -> roleRepository.save(new Role("USER")));

            // Criar o usuário Admin default se não existir
            if (userRepository.findByEmail("admin@abertamente.com").isEmpty()) {
                User admin = new User();
                admin.setName("Administrador");
                admin.setEmail("admin@abertamente.com");
                admin.setPassword(passwordEncoder.encode("**********")); // Senha padrão
                admin.setRoles(Set.of(adminRole));
                
                userRepository.save(admin);
                System.out.println("✅ Usuário Admin gerado (admin@abertamente.com / **********)");
            }
        };
    }
}
