package com.example.freelance.scheduler;

import com.example.freelance.model.MissionFreelance;
import com.example.freelance.model.enums.NotificationType;
import com.example.freelance.repository.MissionFreelanceRepository;
import com.example.freelance.service.NotificationService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;

/**
 * Tâche planifiée (Scheduler) qui envoie des rappels de fin de mission.
 *
 * Cette classe s'exécute automatiquement à intervalles réguliers grâce à Spring Scheduling.
 * Elle recherche les missions qui se terminent dans 3 jours et envoie une notification
 * aux freelancers concernés pour leur rappeler de rédiger leur rapport.
 *
 * Fonctionnement :
 * 1. Le scheduler s'exécute selon l'expression cron configurée
 * 2. Il cherche les missions avec dateFin = aujourd'hui + 3 jours
 * 3. Pour chaque mission trouvée (non encore notifiée) :
 *    - Crée une notification en BDD
 *    - Envoie via WebSocket
 *    - Marque la mission comme notifiée (évite les doublons)
 *
 * Prérequis :
 * - L'annotation @EnableScheduling doit être présente sur la classe principale (FreelanceApplication)
 * - L'application doit être en cours d'exécution
 *
 * @Component : Indique à Spring que c'est un composant à gérer (crée une instance automatiquement)
 */
@Component
public class MissionEndReminderScheduler {

    /**
     * Logger pour tracer l'exécution du scheduler.
     *
     * SLF4J (Simple Logging Facade for Java) est une façade de logging.
     * Les messages seront affichés dans la console et/ou les fichiers de log.
     *
     * Niveaux de log : TRACE < DEBUG < INFO < WARN < ERROR
     */
    private static final Logger logger = LoggerFactory.getLogger(MissionEndReminderScheduler.class);

    /**
     * Repository pour accéder aux missions freelance en base de données.
     */
    private final MissionFreelanceRepository missionFreelanceRepository;

    /**
     * Service pour créer et envoyer les notifications.
     */
    private final NotificationService notificationService;

    /**
     * Constructeur avec injection de dépendances.
     *
     * Spring injecte automatiquement les beans nécessaires.
     *
     * @param missionFreelanceRepository Repository des missions
     * @param notificationService Service de notification
     */
    public MissionEndReminderScheduler(MissionFreelanceRepository missionFreelanceRepository,
                                        NotificationService notificationService) {
        this.missionFreelanceRepository = missionFreelanceRepository;
        this.notificationService = notificationService;
    }

    /**
     * Méthode principale du scheduler qui envoie les rappels de fin de mission.
     *
     * @Scheduled : Indique que cette méthode doit être exécutée automatiquement.
     *
     * Expression Cron : "0 *\/2 * * * *"
     * ┌───────────── seconde (0)
     * │ ┌─────────── minute (*\/2 = toutes les 2 minutes) - pour les tests
     * │ │ ┌───────── heure (*)
     * │ │ │ ┌─────── jour du mois (*)
     * │ │ │ │ ┌───── mois (*)
     * │ │ │ │ │ ┌─── jour de la semaine (*)
     * │ │ │ │ │ │
     * 0 *\/2 * * * *
     *
     * En production, utiliser : "0 0 9 * * *" (tous les jours à 9h00)
     *
     * @Transactional : Toutes les opérations BDD sont dans une transaction.
     *                  Si une erreur survient, les modifications sont annulées.
     */
    @Scheduled(cron = "0 */2 * * * *")  // Toutes les 2 minutes (pour les tests)
    @Transactional
    public void sendMissionEndReminders() {
        // Log de début d'exécution
        logger.info("Starting mission end reminder scheduler...");

        /*
         * Calculer la date cible : aujourd'hui + 3 jours.
         *
         * Exemple :
         * - Aujourd'hui = 16 janvier 2026
         * - Date cible = 19 janvier 2026
         *
         * On cherche les missions qui se terminent le 19 janvier
         * pour envoyer le rappel 3 jours avant.
         */
        LocalDate targetDate = LocalDate.now().plusDays(3);

        /*
         * Rechercher les missions qui se terminent à la date cible
         * ET qui n'ont pas encore reçu de notification (notificationEnvoyee = false).
         *
         * La requête JPQL dans le repository :
         * SELECT mf FROM MissionFreelance mf
         * WHERE mf.dateFin = :targetDate AND mf.notificationEnvoyee = false
         */
        List<MissionFreelance> missionsEndingSoon = missionFreelanceRepository.findMissionsEndingOn(targetDate);

        // Log du nombre de missions trouvées
        logger.info("Found {} missions ending on {}", missionsEndingSoon.size(), targetDate);

        /*
         * Parcourir chaque mission et envoyer une notification.
         *
         * On utilise une boucle for classique au lieu de forEach
         * pour pouvoir gérer les exceptions individuellement.
         */
        for (MissionFreelance missionFreelance : missionsEndingSoon) {
            try {
                // Construire le message de notification
                String message = String.format(
                        "Rappel : Votre mission (ID: %d) se termine dans 3 jours. " +
                        "N'oubliez pas de rédiger votre rapport de fin de mission.",
                        missionFreelance.getMissionId()
                );

                /*
                 * Créer et envoyer la notification.
                 *
                 * Cette méthode :
                 * 1. Crée l'entité Notification
                 * 2. La sauvegarde en BDD
                 * 3. L'envoie via WebSocket au freelancer
                 */
                notificationService.createAndSendNotification(
                        missionFreelance.getFreelancerId(),  // Destinataire
                        missionFreelance.getMissionId(),     // Mission concernée
                        NotificationType.RAPPEL_FIN_MISSION, // Type de notification
                        message                              // Contenu du message
                );

                /*
                 * Marquer la mission comme notifiée.
                 *
                 * IMPORTANT : Ce flag évite d'envoyer plusieurs notifications
                 * pour la même mission si le scheduler s'exécute plusieurs fois.
                 */
                missionFreelance.setNotificationEnvoyee(true);
                missionFreelanceRepository.save(missionFreelance);

                // Log de succès
                logger.info("Notification sent for mission {} to freelancer {}",
                        missionFreelance.getMissionId(), missionFreelance.getFreelancerId());

            } catch (Exception e) {
                /*
                 * En cas d'erreur, on log l'erreur mais on continue avec les autres missions.
                 *
                 * On ne veut pas qu'une erreur sur une mission bloque toutes les autres.
                 * L'erreur sera visible dans les logs pour investigation.
                 */
                logger.error("Failed to send notification for mission {}: {}",
                        missionFreelance.getMissionId(), e.getMessage());
            }
        }

        // Log de fin d'exécution
        logger.info("Mission end reminder scheduler completed.");
    }
}
