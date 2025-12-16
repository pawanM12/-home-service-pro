package com.homeservice.serviceprovider.service;

import com.homeservice.serviceprovider.entity.ServiceProvider;
import com.homeservice.serviceprovider.repository.ServiceProviderRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ServiceProviderService {
    @Autowired
    private ServiceProviderRepository repository;

    public List<ServiceProvider> getAllProviders() {
        return repository.findAll();
    }

    public ServiceProvider getProviderById(Long id) {
        return repository.findById(id).orElse(null);
    }

    public ServiceProvider createProvider(ServiceProvider provider) {
        return repository.save(provider);
    }

    public List<ServiceProvider> getProvidersBySpecialization(String specialization) {
        return repository.findBySpecialization(specialization);
    }

    public ServiceProvider authenticate(String email, String password) {
        return repository.findByEmail(email)
                .filter(provider -> provider.getPassword() != null && provider.getPassword().equals(password))
                .orElse(null);
    }

    public ServiceProvider getProviderByEmail(String email) {
        return repository.findByEmail(email).orElse(null);
    }
}
