package com.homeservice.jobservice.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.homeservice.jobservice.entity.Job;
import com.homeservice.jobservice.service.JobService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Arrays;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(JobController.class)
public class JobControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private JobService service;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void bookJob_ShouldReturnSavedJob() throws Exception {
        Job job = new Job();
        job.setServiceType("Plumbing");
        job.setStatus("WAITING_FOR_PROVIDER");

        when(service.bookJob(any(Job.class))).thenReturn(job);

        mockMvc.perform(post("/api/jobs")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(job)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.serviceType").value("Plumbing"))
                .andExpect(jsonPath("$.status").value("WAITING_FOR_PROVIDER"));
    }

    @Test
    void getPendingJobs_ShouldReturnList() throws Exception {
        Job job = new Job();
        job.setStatus("WAITING_FOR_PROVIDER");

        when(service.getPendingJobs("Plumbing")).thenReturn(Arrays.asList(job));

        mockMvc.perform(get("/api/jobs/pending/Plumbing"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].status").value("WAITING_FOR_PROVIDER"));
    }

    @Test
    void acceptJob_ShouldReturnAcceptedJob() throws Exception {
        Job job = new Job();
        job.setId(1L);
        job.setStatus("ASSIGNED");
        job.setProviderId(10L);

        when(service.acceptJob(eq(1L), eq(10L))).thenReturn(job);

        mockMvc.perform(post("/api/jobs/1/accept")
                .param("providerId", "10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("ASSIGNED"))
                .andExpect(jsonPath("$.providerId").value(10));
    }
}
