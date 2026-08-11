package com.example.freelance.service;

import com.example.freelance.model.Freelancer;
import com.example.freelance.model.Mission;
import com.example.freelance.model.Notification;
import com.example.freelance.model.enums.MissionStatut;
import com.example.freelance.repository.NotificationRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.mockito.Mockito.*;


@ExtendWith(MockitoExtension.class)
public class NotificationServiceTest {

    @InjectMocks
    private NotificationService notificationService;

    @Mock
    private NotificationRepository notificationRepository;

    @Test
    public void testSendNotificationForMissionInEnAttente() {

        Freelancer freelancer = new Freelancer();
        freelancer.setId(1L);

        Mission mission = new Mission();
        mission.setStatut(MissionStatut.EN_ATTENTE);
        mission.setTitre("Mission 1");


        notificationService.sendNotification(freelancer, mission);


        verify(notificationRepository, times(1)).save(any(Notification.class));  // Verify that a notification is saved
    }

    @Test
    public void testDoNotSendNotificationForMissionNotInEnAttente() {

        Freelancer freelancer = new Freelancer();
        freelancer.setId(1L);

        Mission mission = new Mission();
        mission.setStatut(MissionStatut.ACCEPTEE);
        mission.setTitre("Mission 2");


        notificationService.sendNotification(freelancer, mission);


        verify(notificationRepository, times(0)).save(any(Notification.class));  // Ensure no notification is saved
    }
}
