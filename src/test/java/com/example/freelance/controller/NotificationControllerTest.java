package com.example.freelance.controller;

import com.example.freelance.model.Freelancer;
import com.example.freelance.model.Mission;
import com.example.freelance.model.enums.MissionStatut;
import com.example.freelance.repository.FreelancerRepository;
import com.example.freelance.repository.MissionRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;



@SpringBootTest
public class NotificationControllerTest {

    @Autowired
    private MissionRepository missionRepository;

    @Autowired
    private FreelancerRepository freelancerRepository;

    @Test
    public void testAddMissionAndSendNotification() {

        Freelancer freelancer = new Freelancer();
        freelancer.setNom("John Doe");
        freelancerRepository.save(freelancer);


        Mission mission = new Mission();
        mission.setTitre("New Mission");
        mission.setStatut(MissionStatut.EN_ATTENTE);
        missionRepository.save(mission);


    }
}
