package com.homeservice.jobservice.controller;

import com.homeservice.jobservice.entity.Job;
import com.homeservice.jobservice.service.JobService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/jobs")
public class JobController {

    @Autowired
    private JobService service;

    @PostMapping
    public Job bookJob(@RequestBody Job job) {
        return service.bookJob(job);
    }

    @GetMapping("/customer/{customerId}")
    public List<Job> getJobsByCustomer(@PathVariable Long customerId) {
        return service.getJobsByCustomer(customerId);
    }

    @PutMapping("/{id}/status")
    public Job updateStatus(@PathVariable Long id, @RequestParam String status) {
        return service.updateStatus(id, status);
    }

    @GetMapping("/pending/{serviceType}")
    public List<Job> getPendingJobs(@PathVariable String serviceType) {
        return service.getPendingJobs(serviceType);
    }

    @PostMapping("/{id}/accept")
    public Job acceptJob(@PathVariable Long id, @RequestParam Long providerId) {
        return service.acceptJob(id, providerId);
    }
}
