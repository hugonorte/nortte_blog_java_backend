package com_abertamente_cms.seeder;

import com_abertamente_cms.domain.Role;
import com_abertamente_cms.domain.User;
import com_abertamente_cms.repository.RoleRepository;
import com_abertamente_cms.repository.UserRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.util.Optional;
import java.util.Set;

@Component
public class DatabaseSeeder implements CommandLineRunner {

    private final RoleRepository roleRepository;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public DatabaseSeeder(RoleRepository roleRepository, UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.roleRepository = roleRepository;
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public void run(String... args) throws Exception {
        // 1. Criar Roles padrão (se não existirem)
        Role adminRole = createRoleIfNotFound("ADMIN");
        Role authorRole = createRoleIfNotFound("AUTHOR");
        Role userRole = createRoleIfNotFound("USER");

        // 2. Criar Usuário Admin inicial (se não existir)
        String adminEmail = "admin@abertamente.com";
        Optional<User> adminOpt = userRepository.findByEmail(adminEmail);

        if (adminOpt.isEmpty()) {
            User admin = new User(
                    "Administrador Supremo",
                    adminEmail,
                    passwordEncoder.encode("**********") // Senha provisória
            );
            admin.setRoles(Set.of(adminRole));
            userRepository.save(admin);
            System.out.println("Usuário Admin semeado: " + adminEmail + " / **********");
        }
    }

    private Role createRoleIfNotFound(String name) {
        return roleRepository.findByName(name).orElseGet(() -> {
            Role role = new Role(name);
            return roleRepository.save(role);
        });
    }
}
