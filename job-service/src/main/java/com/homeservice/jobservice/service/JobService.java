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
        // Validation: Past Date
        // if (job.getScheduledTime() != null &&
        // job.getScheduledTime().isBefore(java.time.LocalDateTime.now())) {
        // throw new IllegalArgumentException("Cannot book a job in the past.");
        // }

        // Generate 4-digit OTP
        String otp = String.format("%04d", new java.util.Random().nextInt(10000));
        job.setCompletionOtp(otp);

        // Initial status is waiting for a provider to accept
        job.setStatus("WAITING_FOR_PROVIDER");

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

        // TODO: Add conflict check here (future enhancement)

        job.setProviderId(providerId);
        job.setStatus("ASSIGNED");
        return repository.save(job);
    }

    public List<Job> getJobsByCustomer(Long customerId) {
        return repository.findByCustomerId(customerId);
    }

    public List<Job> getJobsByProvider(Long providerId) {
        return repository.findByProviderId(providerId);
    }

    public Job completeJob(Long id, String otp) {
        Job job = repository.findById(id).orElseThrow(() -> new RuntimeException("Job not found"));

        if (!"ASSIGNED".equals(job.getStatus()) && !"IN_PROGRESS".equals(job.getStatus())) {
            throw new RuntimeException("Job is not in a valid state to be completed.");
        }

        if (job.getCompletionOtp() == null || !job.getCompletionOtp().equals(otp)) {
            throw new IllegalArgumentException("Invalid OTP. Please ask customer for the correct code.");
        }

        job.setStatus("COMPLETED");
        return repository.save(job);
    }

    // Deprecated: Use specific lifecycle methods
    public Job updateStatus(Long id, String status) {
        Job job = repository.findById(id).orElseThrow(() -> new RuntimeException("Job not found"));
        job.setStatus(status);
        return repository.save(job);
    }
}
