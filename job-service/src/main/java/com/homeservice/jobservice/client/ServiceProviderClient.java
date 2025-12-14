package com.homeservice.jobservice.client;

import com.homeservice.jobservice.model.ServiceProvider;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import java.util.List;

@FeignClient(name = "serviceprovider-service")
public interface ServiceProviderClient {
    @GetMapping("/api/providers/specialization/{specialization}")
    List<ServiceProvider> getProvidersBySpecialization(@PathVariable("specialization") String specialization);
}
