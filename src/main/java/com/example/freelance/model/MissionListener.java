//package com.example.freelance.model;
//
//import com.example.freelance.model.enums.MissionStatut;
//import com.example.freelance.service.FreelanceService;
//import com.example.freelance.service.NotificationService;
//import org.springframework.context.annotation.Lazy;
//import org.springframework.stereotype.Component;
//import jakarta.persistence.PostPersist;
//import java.util.List;
//import java.util.Set;
//
//@Component
//public class MissionListener {
//
//    private static FreelanceService freelanceService;
//    private static NotificationService notificationService;
//
//    public MissionListener(FreelanceService freelanceService, NotificationService notificationService) {
//        MissionListener.freelanceService = freelanceService;
//        MissionListener.notificationService = notificationService;
//    }
//
//    @PostPersist
//    public void afterMissionAdded(Mission mission) {
//        if (mission.getStatut() != MissionStatut.EN_ATTENTE) {
//            return;
//        }
//
//        List<Freelancer> freelancers = freelanceService.getAllFreelancers();
//
//        for (Freelancer freelancer : freelancers) {
//            Set<Competence> freelancerCompetences = freelancer.getCompetences();
//
//            if (freelancerCompetences.containsAll(mission.getCompetences())
//                    && freelanceService.isFreelancerAvailable(freelancer)) {
//
//                notificationService.sendNotification(freelancer, mission);
//            }
//        }
//    }
//}
package com.example.freelance.model;

import com.example.freelance.model.enums.MissionStatut;
import com.example.freelance.service.FreelanceService;
import com.example.freelance.service.NotificationService;
import jakarta.persistence.PostPersist;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Set;

@Component
public class MissionListener {

    private static FreelanceService freelanceService;
    private static NotificationService notificationService;

    public MissionListener() {
        // Empty constructor to avoid direct dependency injection in the constructor
    }

    @Autowired
    public void setFreelanceService(@Lazy FreelanceService freelanceService) {
        MissionListener.freelanceService = freelanceService;
    }

    @Autowired
    public void setNotificationService(@Lazy NotificationService notificationService) {
        MissionListener.notificationService = notificationService;
    }

//    @PostPersist
//    public void afterMissionAdded(Mission mission) {
//        if (mission.getStatut() != MissionStatut.EN_ATTENTE) {
//            return;
//        }
//
//        List<Freelancer> freelancers = freelanceService.getAllFreelancers();
//
//        for (Freelancer freelancer : freelancers) {
//            Set<Competence> freelancerCompetences = freelancer.getCompetences();
//
//            if (freelancerCompetences.containsAll(mission.getCompetences())
//                    && freelanceService.isFreelancerAvailable(freelancer)) {
//
//                notificationService.sendNotification(freelancer, mission);
//            }
//        }
//    }
@PostPersist
public void afterMissionAdded(Mission mission) {
    Mission fullMission = freelanceService.getMissionWithCompetences(mission.getId());
    Set<Competence> competences = fullMission.getCompetences();

    if (mission.getStatut() != MissionStatut.EN_ATTENTE) {
        return;
    }

    List<Freelancer> freelancers = freelanceService.getAllFreelancers();

    for (Freelancer freelancer : freelancers) {
        Set<Competence> freelancerCompetences = freelancer.getCompetences();

        if (freelancerCompetences.containsAll(competences)
                && freelanceService.isFreelancerAvailable(freelancer)) {
            notificationService.sendNotification(freelancer, mission);
        }
    }
}

}
