package com.homeservice.customerservice.controller;

import com.homeservice.customerservice.client.JobClient;
import com.homeservice.customerservice.client.ServiceProviderClient;
import com.homeservice.customerservice.entity.Customer;
import com.homeservice.customerservice.model.Job;
import com.homeservice.customerservice.service.CustomerDetailsService;
import org.junit.jupiter.api.Test;
import org.springframework.context.annotation.Import;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Collections;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;

@WebMvcTest(CustomerController.class)
@Import(com.homeservice.customerservice.config.SecurityConfig.class)
public class CustomerControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private JobClient jobClient;

    @MockBean
    private ServiceProviderClient providerClient;

    @MockBean
    private CustomerDetailsService customerDetailsService;

    @Test
    @WithMockUser(username = "testuser")
    void dashboard_ShouldLoad_WhenAuthenticated() throws Exception {
        when(providerClient.getAllProviders()).thenReturn(Collections.emptyList());

        mockMvc.perform(get("/dashboard"))
                .andExpect(status().isOk())
                .andExpect(view().name("dashboard"))
                .andExpect(model().attributeExists("providers"))
                .andExpect(model().attribute("username", "testuser"));
    }

    @Test
    void dashboard_ShouldRedirect_WhenUnauthenticated() throws Exception {
        mockMvc.perform(get("/dashboard"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("http://localhost/login"));
    }

    @Test
    @WithMockUser(username = "testuser")
    void bookJob_ShouldCallClientAndRedirect() throws Exception {
        Customer customer = new Customer();
        customer.setId(1L);
        when(customerDetailsService.getCustomerByUsername("testuser")).thenReturn(customer);

        mockMvc.perform(post("/book")
                .param("serviceType", "Plumbing")
                .param("description", "Leak")
                .with(csrf()))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/my-jobs"));

        verify(jobClient).bookJob(any(Job.class));
    }
}
