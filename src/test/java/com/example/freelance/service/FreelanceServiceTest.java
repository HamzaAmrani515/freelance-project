package com.example.freelance.service;
import com.example.freelance.model.Freelancer;
import com.example.freelance.model.MissionFreelance;
import com.example.freelance.repository.MissionFreelanceRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Collections;

import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
public class FreelanceServiceTest {

    @InjectMocks
    private FreelanceService freelanceService;

    @Mock
    private MissionFreelanceRepository missionFreelanceRepository;

    @Test
    public void testFreelancerIsAvailable() {
        // Arrange
        Freelancer freelancer = new Freelancer();
        freelancer.setId(1L);  // Setting the freelancer ID

        when(missionFreelanceRepository.findByFreelancerId(freelancer.getId())).thenReturn(Collections.emptyList());

        // Act
        boolean result = freelanceService.isFreelancerAvailable(freelancer);

        // Assert
        assertTrue(result, "Freelancer should be available.");
    }

    @Test
    public void testFreelancerIsNotAvailable() {
        // Arrange
        Freelancer freelancer = new Freelancer();
        freelancer.setId(1L);

        // Simulate that the freelancer is already linked to a mission
        MissionFreelance missionFreelance = new MissionFreelance();
        when(missionFreelanceRepository.findByFreelancerId(freelancer.getId())).thenReturn(Collections.singletonList(missionFreelance));

        // Act
        boolean result = freelanceService.isFreelancerAvailable(freelancer);

        // Assert
        assertFalse(result, "Freelancer should not be available.");
    }
}

