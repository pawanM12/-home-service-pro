package com.homeservice.serviceprovider.client;

import com.homeservice.serviceprovider.model.Job;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@FeignClient(name = "job-service")
public interface JobClient {

    @GetMapping("/api/jobs/pending/{specialization}")
    List<Job> getPendingJobs(@PathVariable("specialization") String specialization);

    @PostMapping("/api/jobs/{id}/accept")
    void acceptJob(@PathVariable("id") Long id, @RequestParam("providerId") Long providerId);

    @GetMapping("/api/jobs/provider/{providerId}")
    List<Job> getJobsByProvider(@PathVariable("providerId") Long providerId);

    @PostMapping("/api/jobs/{id}/complete")
    void completeJob(@PathVariable("id") Long id, @RequestParam("otp") String otp);
}
