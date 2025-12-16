package com.homeservice.jobservice.service;

import com.homeservice.jobservice.entity.Job;
import com.homeservice.jobservice.repository.JobRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class JobService {
    @Autowired
    private JobRepository repository;

    public Job bookJob(Job job) {
        // Initial status is waiting for a provider to accept
        job.setStatus("WAITING_FOR_PROVIDER");

        // We no longer auto-assign. Providers must accept the job.

        return repository.save(job);
    }

    public List<Job> getPendingJobs(String serviceType) {
        return repository.findByServiceTypeAndStatus(serviceType, "WAITING_FOR_PROVIDER");
    }

    public Job acceptJob(Long jobId, Long providerId) {
        Job job = repository.findById(jobId).orElseThrow(() -> new RuntimeException("Job not found"));

        if (!"WAITING_FOR_PROVIDER".equals(job.getStatus())) {
            throw new RuntimeException("Job is not available for acceptance");
        }

        job.setProviderId(providerId);
        job.setStatus("ASSIGNED");
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
