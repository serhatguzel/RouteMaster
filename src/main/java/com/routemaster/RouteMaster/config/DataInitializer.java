package com.routemaster.RouteMaster.config;

import com.routemaster.RouteMaster.entity.Role;
import com.routemaster.RouteMaster.entity.User;
import com.routemaster.RouteMaster.repository.RoleRepository;
import com.routemaster.RouteMaster.repository.UserRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import jakarta.transaction.Transactional;
import java.util.HashSet;
import java.util.Set;

@Component
public class DataInitializer implements CommandLineRunner {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;

    public DataInitializer(UserRepository userRepository,
                           RoleRepository roleRepository,
                           PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    @Transactional
    public void run(String... args) {

        if (roleRepository.count() > 0) {
            return;
        }

        Role adminRole = new Role();
        adminRole.setName("ADMIN");
        roleRepository.save(adminRole);

        Role agencyRole = new Role();
        agencyRole.setName("AGENCY");
        roleRepository.save(agencyRole);

        User admin = new User();
        admin.setUsername("admin");
        admin.setPassword(passwordEncoder.encode("admin123"));
        admin.setRoles(new HashSet<>(Set.of(adminRole)));
        userRepository.save(admin);

        User agency = new User();
        agency.setUsername("agency");
        agency.setPassword(passwordEncoder.encode("agency123"));
        agency.setRoles(new HashSet<>(Set.of(agencyRole)));
        userRepository.save(agency);

        System.out.println("🚀 ROUTEMASTER: BAŞLANGIÇ VERİLERİ (ROLES & USERS) YÜKLENDİ!");

    }
}
