package com.example.bloodbanksystem;

import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import java.time.LocalDate;

@Component
public class DataInitializer implements CommandLineRunner {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final InventoryService inventoryService;
    private final DonorRepository donorRepository;
    private final BloodCampRepository campRepository;

    public DataInitializer(UserRepository userRepository, PasswordEncoder passwordEncoder,
                           InventoryService inventoryService, DonorRepository donorRepository,
                           BloodCampRepository campRepository) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.inventoryService = inventoryService;
        this.donorRepository = donorRepository;
        this.campRepository = campRepository;
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

        // Seed Mock Donors if none exist
        if (donorRepository.count() == 0) {
            donorRepository.save(new Donor("Rahul Sharma", "Suresh Sharma", "rahul@gmail.com", "O+", "9876543210"));
            donorRepository.save(new Donor("Aman Verma", "Rajesh Verma", "aman@gmail.com", "A+", "9876543211"));
            donorRepository.save(new Donor("Vikram Singh", "Manjit Singh", "vikram@gmail.com", "B-", "9876543212"));
            donorRepository.save(new Donor("Neha Gupta", "Alok Gupta", "neha@gmail.com", "AB+", "9876543213"));
            System.out.println("Mock Donors Seeded.");
        }

        // Seed Mock Camps if none exist
        if (campRepository.count() == 0) {
            BloodCamp camp1 = new BloodCamp("City Metro Blood Drive", "Metro Station Hall, Block B", LocalDate.now().plusDays(7), "09:00 AM", "05:00 PM", 30);
            camp1.setOrganizer("Rotary Club & DMRC");
            camp1.setContactNumber("9999888877");
            camp1.setNotes("Annual metro drive. Free refreshments and certificate for all donors.");
            campRepository.save(camp1);

            BloodCamp camp2 = new BloodCamp("Red Cross Community Camp", "Community Center Sector 15", LocalDate.now().plusDays(14), "10:00 AM", "04:00 PM", 50);
            camp2.setOrganizer("Indian Red Cross Society");
            camp2.setContactNumber("9876509876");
            camp2.setNotes("Mega donation drive. Special focus on O negative blood group.");
            campRepository.save(camp2);

            BloodCamp camp3 = new BloodCamp("Youth College Drive", "National Tech Institute (Main Auditorium)", LocalDate.now().plusDays(3), "09:30 AM", "03:30 PM", 80);
            camp3.setOrganizer("NSS Technical Unit");
            camp3.setContactNumber("9555112233");
            camp3.setNotes("College student community drive.");
            campRepository.save(camp3);

            System.out.println("Mock Blood Camps Seeded.");
        }

        System.out.println("===========================================");
        System.out.println("  Blood Bank System Started Successfully!");
        System.out.println("  Admin Login: admin / admin123");
        System.out.println("  Staff Login: staff / staff123");
        System.out.println("  API Base URL: http://localhost:8080/api");
        System.out.println("===========================================");
    }
}
