package com.example.freelance.Entity;

import com.example.freelance.model.Freelancer;  // Import Freelancer class
import com.example.freelance.model.Mission;
import com.example.freelance.model.MissionListener;
import com.example.freelance.model.enums.MissionStatut;
import com.example.freelance.service.FreelanceService;
import com.example.freelance.service.NotificationService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.mockito.Mockito.*;


@ExtendWith(MockitoExtension.class)
public class MissionListenerTest {

    @InjectMocks
    private MissionListener missionListener;  // Make sure this class exists and is imported

    @Mock
    private FreelanceService freelanceService;

    @Mock
    private NotificationService notificationService;

    @Test
    public void testNotificationSentForAvailableFreelancer() {
        // Arrange
        Mission mission = new Mission();
        mission.setStatut(MissionStatut.EN_ATTENTE);
        Freelancer freelancer = new Freelancer();
        freelancer.setId(1L);

        when(freelanceService.isFreelancerAvailable(freelancer)).thenReturn(true);


        missionListener.afterMissionAdded(mission);  // Make sure this method exists in MissionListener


        verify(notificationService, times(1)).sendNotification(freelancer, mission);
    }

    @Test
    public void testNoNotificationSentForUnavailableFreelancer() {

        Mission mission = new Mission();
        mission.setStatut(MissionStatut.EN_ATTENTE); // Ensure this status exists in your Mission class
        Freelancer freelancer = new Freelancer();  // Make sure Freelancer class exists and is properly set up
        freelancer.setId(1L);

        when(freelanceService.isFreelancerAvailable(freelancer)).thenReturn(false);


        missionListener.afterMissionAdded(mission);


        verify(notificationService, times(0)).sendNotification(freelancer, mission);
    }
}
