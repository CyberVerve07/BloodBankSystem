package com.example.bloodbanksystem;

import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
public class DataInitializer implements CommandLineRunner {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final InventoryService inventoryService;

    public DataInitializer(UserRepository userRepository, PasswordEncoder passwordEncoder,
                           InventoryService inventoryService) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.inventoryService = inventoryService;
    }

    @Override
    public void run(String... args) {
        // Initialize blood inventory
        inventoryService.initializeInventory();

        // Create default admin user
        if (!userRepository.existsByUsername("admin")) {
            User admin = new User();
            admin.setUsername("admin");
            admin.setPassword(passwordEncoder.encode("admin123"));
            admin.setFullName("System Administrator");
            admin.setEmail("admin@bloodbank.com");
            admin.setRole(User.Role.ADMIN);
            userRepository.save(admin);
        }

        // Create default staff user
        if (!userRepository.existsByUsername("staff")) {
            User staff = new User();
            staff.setUsername("staff");
            staff.setPassword(passwordEncoder.encode("staff123"));
            staff.setFullName("Staff Member");
            staff.setEmail("staff@bloodbank.com");
            staff.setRole(User.Role.STAFF);
            userRepository.save(staff);
        }

        System.out.println("===========================================");
        System.out.println("  Blood Bank System Started Successfully!");
        System.out.println("  Admin Login: admin / admin123");
        System.out.println("  Staff Login: staff / staff123");
        System.out.println("  API Base URL: http://localhost:8080/api");
        System.out.println("===========================================");
    }
}
