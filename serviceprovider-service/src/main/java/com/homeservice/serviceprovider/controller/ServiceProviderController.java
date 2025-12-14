package com.homeservice.serviceprovider.controller;

import com.homeservice.serviceprovider.entity.ServiceProvider;
import com.homeservice.serviceprovider.service.ServiceProviderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/providers")
public class ServiceProviderController {

    @Autowired
    private ServiceProviderService service;

    @GetMapping
    public List<ServiceProvider> getAllProviders() {
        return service.getAllProviders();
    }

    @GetMapping("/{id}")
    public ServiceProvider getProviderById(@PathVariable Long id) {
        return service.getProviderById(id);
    }

    @PostMapping
    public ServiceProvider createProvider(@RequestBody ServiceProvider provider) {
        return service.createProvider(provider);
    }

    @GetMapping("/specialization/{specialization}")
    public List<ServiceProvider> getProvidersBySpecialization(@PathVariable String specialization) {
        return service.getProvidersBySpecialization(specialization);
    }
}
