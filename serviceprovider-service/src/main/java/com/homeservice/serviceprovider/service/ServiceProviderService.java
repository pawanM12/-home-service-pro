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
}
