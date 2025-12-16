package com.homeservice.serviceprovider.repository;

import com.homeservice.serviceprovider.entity.ServiceProvider;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface ServiceProviderRepository extends JpaRepository<ServiceProvider, Long> {
    List<ServiceProvider> findBySpecialization(String specialization);

    java.util.Optional<ServiceProvider> findByEmail(String email);
}
