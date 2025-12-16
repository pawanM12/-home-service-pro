package com.homeservice.serviceprovider.config;

import com.homeservice.serviceprovider.entity.ServiceProvider;
import com.homeservice.serviceprovider.repository.ServiceProviderRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class DataInitializer {

    // Seeding some initial data for testing/demo
    @Bean
    CommandLineRunner initDatabase(ServiceProviderRepository repository) {
        return args -> {
            if (repository.count() == 0) {
                repository.save(new ServiceProvider("John Plumber", "Plumbing", "john@example.com", true, "password",
                        "PROVIDER"));
                repository.save(new ServiceProvider("Jane Electrician", "Electrical", "jane@example.com", true,
                        "password", "PROVIDER"));
                repository
                        .save(new ServiceProvider("Mike Cleaner", "Home Cleaning", "mike@example.com", true, "password",
                                "PROVIDER"));
                repository.save(new ServiceProvider("Gary Gardener", "Gardening", "gary@example.com", true, "password",
                        "PROVIDER"));
                repository.save(new ServiceProvider("Peter Pest", "Pest Control", "peter@example.com", true, "password",
                        "PROVIDER"));
            } else {
                // Fix for existing data mismatch if already seeded
                ServiceProvider mike = repository.findByEmail("mike@example.com").orElse(null);
                if (mike != null && "Cleaning".equals(mike.getSpecialization())) {
                    mike.setSpecialization("Home Cleaning");
                    repository.save(mike);
                }
            }
        };
    }
}
