package com.homeservice.jobservice.service;

import com.homeservice.jobservice.entity.Job;
import com.homeservice.jobservice.repository.JobRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class JobServiceTest {

    @Mock
    private JobRepository repository;

    @InjectMocks
    private JobService service;

    @Test
    void bookJob_ShouldSetStatusAndSave() {
        Job job = new Job();
        job.setServiceType("Plumbing");

        when(repository.save(any(Job.class))).thenAnswer(invocation -> invocation.getArgument(0));

        Job savedJob = service.bookJob(job);

        assertEquals("WAITING_FOR_PROVIDER", savedJob.getStatus());
        verify(repository).save(job);
    }

    @Test
    void getPendingJobs_ShouldReturnMatchingJobs() {
        Job job1 = new Job();
        job1.setStatus("WAITING_FOR_PROVIDER");

        when(repository.findByServiceTypeAndStatus("Plumbing", "WAITING_FOR_PROVIDER"))
                .thenReturn(Arrays.asList(job1));

        List<Job> jobs = service.getPendingJobs("Plumbing");

        assertEquals(1, jobs.size());
        verify(repository).findByServiceTypeAndStatus("Plumbing", "WAITING_FOR_PROVIDER");
    }

    @Test
    void acceptJob_ShouldUpdateStatusAndProvider() {
        Job job = new Job();
        job.setId(1L);
        job.setStatus("WAITING_FOR_PROVIDER");

        when(repository.findById(1L)).thenReturn(Optional.of(job));
        when(repository.save(any(Job.class))).thenAnswer(invocation -> invocation.getArgument(0));

        Job acceptedJob = service.acceptJob(1L, 100L);

        assertEquals("ASSIGNED", acceptedJob.getStatus());
        assertEquals(100L, acceptedJob.getProviderId());
    }

    @Test
    void acceptJob_ShouldThrowExample_WhenJobAlreadyAssigned() {
        Job job = new Job();
        job.setId(1L);
        job.setStatus("ASSIGNED");

        when(repository.findById(1L)).thenReturn(Optional.of(job));

        assertThrows(RuntimeException.class, () -> service.acceptJob(1L, 100L));
    }
}
