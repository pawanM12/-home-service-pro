package com.homeservice.customerservice.controller;

import com.homeservice.customerservice.client.JobClient;
import com.homeservice.customerservice.client.ServiceProviderClient;
import com.homeservice.customerservice.model.Job;
import com.homeservice.customerservice.model.ServiceProvider;
import com.homeservice.customerservice.service.CustomerDetailsService;
import com.homeservice.customerservice.entity.Customer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.ModelAttribute;

import java.util.List;

@Controller
public class CustomerController {

    @Autowired
    private JobClient jobClient;

    @Autowired
    private ServiceProviderClient providerClient;

    @Autowired
    private CustomerDetailsService customerDetailsService;

    @GetMapping("/")
    public String home() {
        return "redirect:/dashboard";
    }

    @GetMapping("/dashboard")
    public String dashboard(Model model, Authentication authentication) {
        // Customer specific dashboard
        List<ServiceProvider> providers = providerClient.getAllProviders();
        model.addAttribute("providers", providers);
        model.addAttribute("username", authentication.getName());
        return "dashboard";
    }

    @GetMapping("/book")
    public String bookingForm(@RequestParam(required = false) String serviceType, Model model,
            Authentication authentication) {
        Job job = new Job();
        if (serviceType != null) {
            job.setServiceType(serviceType);
        }
        model.addAttribute("job", job);
        // Add customer address for auto-populate
        Customer customer = customerDetailsService.getCustomerByUsername(authentication.getName());
        model.addAttribute("customer", customer);
        return "booking-form";
    }

    @PostMapping("/book")
    public String bookJob(@ModelAttribute Job job, Authentication authentication) {
        Customer customer = customerDetailsService.getCustomerByUsername(authentication.getName());
        job.setCustomerId(customer.getId());
        // Ensure scheduledTime is parsed or handled. Thymeleaf <input
        // type="datetime-local"> binds to LocalDateTime if configured right.
        jobClient.bookJob(job);
        return "redirect:/my-jobs";
    }

    @GetMapping("/my-jobs")
    public String myJobs(Model model, Authentication authentication) {
        Customer customer = customerDetailsService.getCustomerByUsername(authentication.getName());
        if (customer != null) {
            List<Job> jobs = jobClient.getJobsByCustomer(customer.getId());
            model.addAttribute("jobs", jobs);
        }
        return "job-status";
    }
}
