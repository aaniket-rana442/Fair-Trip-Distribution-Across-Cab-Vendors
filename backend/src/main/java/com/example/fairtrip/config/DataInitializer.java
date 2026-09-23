package com.example.fairtrip.config;

import com.example.fairtrip.entity.Role;
import com.example.fairtrip.entity.User;
import com.example.fairtrip.entity.Vendor;
import com.example.fairtrip.entity.enums.RoleName;
import com.example.fairtrip.repository.RoleRepository;
import com.example.fairtrip.repository.UserRepository;
import com.example.fairtrip.repository.VendorRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
public class DataInitializer implements CommandLineRunner {

    private static final Logger log = LoggerFactory.getLogger(DataInitializer.class);

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final VendorRepository vendorRepository;
    private final PasswordEncoder passwordEncoder;

    public DataInitializer(UserRepository userRepository,
                           RoleRepository roleRepository,
                           VendorRepository vendorRepository,
                           PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
        this.vendorRepository = vendorRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    @Transactional
    public void run(String... args) {
        log.info("Verifying and updating seed credentials...");

        ensureUser("admin@example.com", "Admin@123", "System Administrator", RoleName.ADMIN, null);
        ensureUser("user@example.com", "User@123", "Corporate Employee", RoleName.NORMAL_USER, null);
        ensureUser("vendor1@example.com", "Vendor@123", "Vendor 1 Operator", RoleName.VENDOR, "V1");
        ensureUser("vendor2@example.com", "Vendor@123", "Vendor 2 Operator", RoleName.VENDOR, "V2");
        ensureUser("vendor3@example.com", "Vendor@123", "Vendor 3 Operator", RoleName.VENDOR, "V3");

        log.info("Seed credentials verified successfully.");
    }

    private void ensureUser(String email, String rawPassword, String name, RoleName roleName, String vendorCode) {
        Role role = roleRepository.findByName(roleName).orElseGet(() -> {
            Role r = new Role();
            r.setName(roleName);
            return roleRepository.save(r);
        });

        User user = userRepository.findByEmail(email).orElse(null);
        if (user == null) {
            user = new User(name, email, passwordEncoder.encode(rawPassword), role);
            user = userRepository.save(user);
        } else {
            // Ensure password hash matches current encoder configuration
            user.setPasswordHash(passwordEncoder.encode(rawPassword));
            userRepository.save(user);
        }

        if (vendorCode != null) {
            final User finalUser = user;
            vendorRepository.findByVendorCode(vendorCode).ifPresent(v -> {
                v.setUser(finalUser);
                vendorRepository.save(v);
            });
        }
    }
}
