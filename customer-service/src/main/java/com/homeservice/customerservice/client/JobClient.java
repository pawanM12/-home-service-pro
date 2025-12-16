package com.homeservice.customerservice.client;

import com.homeservice.customerservice.model.Job;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import java.util.List;

@FeignClient(name = "job-service")
public interface JobClient {
    @PostMapping("/api/jobs")
    Job bookJob(@RequestBody Job job);

    @GetMapping("/api/jobs/customer/{customerId}")
    List<Job> getJobsByCustomer(@PathVariable("customerId") Long customerId);

    @GetMapping("/api/jobs/pending/{serviceType}")
    List<Job> getPendingJobs(@PathVariable("serviceType") String serviceType);

    @PostMapping("/api/jobs/{id}/accept")
    Job acceptJob(@PathVariable("id") Long id,
            @org.springframework.web.bind.annotation.RequestParam("providerId") Long providerId);
}
