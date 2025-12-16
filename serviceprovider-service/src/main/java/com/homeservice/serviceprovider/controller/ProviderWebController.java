package com.homeservice.serviceprovider.controller;

import com.homeservice.serviceprovider.client.JobClient;
import com.homeservice.serviceprovider.entity.ServiceProvider;
import com.homeservice.serviceprovider.model.Job;
import com.homeservice.serviceprovider.service.ServiceProviderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@Controller
@RequestMapping("/provider")
public class ProviderWebController {

    @Autowired
    private ServiceProviderService providerService;

    @Autowired
    private JobClient jobClient;

    // Hardcoded login for demo/presentation since we don't have full Spring
    // Security setup in this service yet
    // In a real app, we would share session or use SSO. For now, we simulate
    // provider context.
    // We will assume "John Plumber" (id=1) or similar for defaults, or simpler:
    // just use a path param or simple login form.
    // For presentation alignment, let's create a simple login page.

    @GetMapping("/login")
    public String loginPage() {
        return "provider-login";
    }

    @PostMapping("/login")
    public String login(@RequestParam String email, Model model) {
        ServiceProvider provider = providerService.getProviderByEmail(email);
        if (provider != null) {
            return "redirect:/provider/dashboard?email=" + email;
        }
        model.addAttribute("error", "Invalid email");
        return "provider-login";
    }

    @GetMapping("/dashboard")
    public String dashboard(@RequestParam(required = false) String email, Model model) {
        if (email == null) {
            return "redirect:/provider/login";
        }

        ServiceProvider provider = providerService.getProviderByEmail(email);
        if (provider == null) {
            return "redirect:/provider/login";
        }

        model.addAttribute("provider", provider);
        // Pass email to keep session-like state in URL for stateless simplicity in this
        // demo
        model.addAttribute("email", email);

        model.addAttribute("email", email);

        List<Job> pendingJobs = jobClient.getPendingJobs(provider.getSpecialization());
        model.addAttribute("pendingJobs", pendingJobs);

        List<Job> allMyJobs = jobClient.getJobsByProvider(provider.getId());
        List<Job> activeJobs = allMyJobs.stream()
                .filter(j -> "ASSIGNED".equals(j.getStatus()) || "IN_PROGRESS".equals(j.getStatus()))
                .toList();
        List<Job> historyJobs = allMyJobs.stream()
                .filter(j -> "COMPLETED".equals(j.getStatus()) || "CANCELLED".equals(j.getStatus()))
                .toList();

        model.addAttribute("activeJobs", activeJobs);
        model.addAttribute("historyJobs", historyJobs);

        return "provider-dashboard";
    }

    @PostMapping("/job/{id}/accept")
    public String acceptJob(@PathVariable Long id, @RequestParam Long providerId, @RequestParam String email) {
        jobClient.acceptJob(id, providerId);
        return "redirect:/provider/dashboard?email=" + email;
    }

    @PostMapping("/job/{id}/complete")
    public String completeJob(@PathVariable Long id, @RequestParam String otp, @RequestParam String email) {
        jobClient.completeJob(id, otp);
        return "redirect:/provider/dashboard?email=" + email;
    }
}
