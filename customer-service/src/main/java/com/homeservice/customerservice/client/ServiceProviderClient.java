package com.homeservice.customerservice.client;

import com.homeservice.customerservice.model.ServiceProvider;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import java.util.List;

@FeignClient(name = "serviceprovider-service")
public interface ServiceProviderClient {
    @GetMapping("/api/providers")
    List<ServiceProvider> getAllProviders();

    @org.springframework.web.bind.annotation.PostMapping("/api/providers/login")
    ServiceProvider login(Object loginRequest);

    @GetMapping("/api/providers/email/{email}")
    ServiceProvider getProviderByEmail(@org.springframework.web.bind.annotation.PathVariable("email") String email);
}
