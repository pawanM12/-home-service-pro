package com.homeservice.jobservice.service;

import com.homeservice.jobservice.client.ServiceProviderClient;
import com.homeservice.jobservice.entity.Job;
import com.homeservice.jobservice.model.ServiceProvider;
import com.homeservice.jobservice.repository.JobRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class JobService {
    @Autowired
    private JobRepository repository;

    @Autowired
    private ServiceProviderClient providerClient;

    public Job bookJob(Job job) {
        job.setStatus("PENDING");

        // Find available providers
        List<ServiceProvider> providers = providerClient.getProvidersBySpecialization(job.getServiceType());

        // Simple assignment logic: assign to first available provider
        Optional<ServiceProvider> assignedProvider = providers.stream()
                .filter(ServiceProvider::isAvailable)
                .findFirst();

        if (assignedProvider.isPresent()) {
            job.setProviderId(assignedProvider.get().getId());
            job.setStatus("ASSIGNED");
        } else {
            job.setStatus("WAITING_FOR_PROVIDER");
        }

        return repository.save(job);
    }

    public List<Job> getJobsByCustomer(Long customerId) {
        return repository.findByCustomerId(customerId);
    }

    public Job updateStatus(Long id, String status) {
        Job job = repository.findById(id).orElseThrow(() -> new RuntimeException("Job not found"));
        job.setStatus(status);
        return repository.save(job);
    }
}
