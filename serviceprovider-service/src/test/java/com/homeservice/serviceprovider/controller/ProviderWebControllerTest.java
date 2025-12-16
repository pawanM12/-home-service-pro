package com.homeservice.serviceprovider.controller;

import com.homeservice.serviceprovider.client.JobClient;
import com.homeservice.serviceprovider.entity.ServiceProvider;
import com.homeservice.serviceprovider.model.Job;
import com.homeservice.serviceprovider.service.ServiceProviderService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Arrays;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(ProviderWebController.class)
public class ProviderWebControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private ServiceProviderService providerService;

    @MockBean
    private JobClient jobClient;

    @Test
    void login_ShouldRedirect_WhenProviderExists() throws Exception {
        ServiceProvider provider = new ServiceProvider();
        provider.setEmail("mike@example.com");

        when(providerService.getProviderByEmail("mike@example.com")).thenReturn(provider);

        mockMvc.perform(post("/provider/login")
                .param("email", "mike@example.com"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/provider/dashboard?email=mike@example.com"));
    }

    @Test
    void login_ShouldFail_WhenProviderNotFound() throws Exception {
        when(providerService.getProviderByEmail("unknown")).thenReturn(null);

        mockMvc.perform(post("/provider/login")
                .param("email", "unknown"))
                .andExpect(status().isOk())
                .andExpect(view().name("provider-login"))
                .andExpect(model().attributeExists("error"));
    }

    @Test
    void dashboard_ShouldShowJobs() throws Exception {
        ServiceProvider provider = new ServiceProvider();
        provider.setEmail("mike@example.com");
        provider.setSpecialization("Cleaning");

        Job job = new Job();
        job.setId(1L);
        job.setServiceType("Cleaning");

        when(providerService.getProviderByEmail("mike@example.com")).thenReturn(provider);
        when(jobClient.getPendingJobs("Cleaning")).thenReturn(Arrays.asList(job));

        mockMvc.perform(get("/provider/dashboard")
                .param("email", "mike@example.com"))
                .andExpect(status().isOk())
                .andExpect(view().name("provider-dashboard"))
                .andExpect(model().attributeExists("pendingJobs"))
                .andExpect(model().attribute("email", "mike@example.com"));
    }

    @Test
    void acceptJob_ShouldCallClientAndRedirect() throws Exception {
        mockMvc.perform(post("/provider/job/1/accept")
                .param("providerId", "10")
                .param("email", "mike@example.com"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/provider/dashboard?email=mike@example.com"));

        verify(jobClient).acceptJob(1L, 10L);
    }
}
