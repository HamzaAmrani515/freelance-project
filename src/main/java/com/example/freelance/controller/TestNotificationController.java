package com.example.freelance.controller;

import com.example.freelance.dto.NotificationDTO;
import com.example.freelance.model.Notification;
import com.example.freelance.model.enums.NotificationType;
import com.example.freelance.scheduler.MissionEndReminderScheduler;
import com.example.freelance.service.NotificationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * Contrôleur de TEST pour les notifications.
 *
 * ⚠️ CE CONTRÔLEUR EST UNIQUEMENT POUR LE DÉVELOPPEMENT ET LES TESTS !
 * À SUPPRIMER OU DÉSACTIVER EN PRODUCTION !
 *
 * Il permet de :
 * 1. Envoyer manuellement une notification de test via l'API
 * 2. Déclencher manuellement le scheduler sans attendre l'heure prévue
 *
 * Utile pour :
 * - Tester le WebSocket sans attendre le scheduler
 * - Démontrer le fonctionnement au professeur
 * - Déboguer les problèmes de notification
 *
 * URL de base : /api/test
 */
@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/test")
public class TestNotificationController {

    /**
     * Service pour créer et envoyer des notifications.
     */
    private final NotificationService notificationService;

    /**
     * Scheduler pour pouvoir le déclencher manuellement.
     */
    private final MissionEndReminderScheduler scheduler;

    /**
     * Envoie une notification de test à un freelancer.
     *
     * Endpoint : POST /api/test/notification/send?freelancerId=1&missionId=42
     *
     * Cette méthode crée immédiatement une notification et l'envoie via WebSocket.
     * Utile pour tester le WebSocket sans avoir à créer de vraies données en base.
     *
     * @RequestParam : Extrait les paramètres de l'URL (query string)
     *                 Ex: ?freelancerId=1&missionId=42
     *
     * defaultValue : Valeur par défaut si le paramètre n'est pas fourni
     *
     * Exemple d'utilisation avec curl :
     * curl -X POST "http://localhost:9091/api/test/notification/send?freelancerId=1&missionId=42"
     *
     * Exemple d'utilisation avec Postman :
     * POST http://localhost:9091/api/test/notification/send
     * Params: freelancerId=1, missionId=42
     *
     * @param freelancerId ID du freelancer qui recevra la notification
     * @param missionId ID de la mission (pour le message)
     * @return La notification créée au format JSON
     */
    @PostMapping("/notification/send")
    public ResponseEntity<NotificationDTO> sendTestNotification(
            @RequestParam Long freelancerId,
            @RequestParam(defaultValue = "1") Long missionId) {

        log.info("Envoi d'une notification de test au freelancer {}", freelancerId);

        // Construire le message de test
        // String.format() remplace %d par la valeur de missionId
        String message = String.format(
                "TEST - Rappel : Votre mission (ID: %d) se termine dans 3 jours. " +
                "N'oubliez pas de rédiger votre rapport de fin de mission.",
                missionId
        );

        /*
         * Appeler le service pour créer et envoyer la notification.
         *
         * Ce qui se passe en interne :
         * 1. Création de l'entité Notification
         * 2. Sauvegarde en base de données (INSERT)
         * 3. Envoi via WebSocket sur /topic/notifications/{freelancerId}
         * 4. Mise à jour du flag sentViaWebsocket = true
         */
        Notification notification = notificationService.createAndSendNotification(
                freelancerId,                        // Destinataire
                missionId,                           // Mission concernée
                NotificationType.RAPPEL_FIN_MISSION, // Type de notification
                message                              // Contenu du message
        );

        // Convertir en DTO et retourner avec HTTP 200 OK
        return ResponseEntity.ok(NotificationDTO.fromEntity(notification));
    }

    /**
     * Déclenche manuellement le scheduler de rappel de fin de mission.
     *
     * Endpoint : POST /api/test/scheduler/trigger
     *
     * Normalement, le scheduler s'exécute automatiquement selon l'expression cron.
     * Cet endpoint permet de le déclencher à la demande pour les tests.
     *
     * Le scheduler va :
     * 1. Chercher les missions qui se terminent dans 3 jours
     * 2. Envoyer une notification à chaque freelancer concerné
     * 3. Marquer les missions comme notifiées
     *
     * Exemple d'utilisation avec curl :
     * curl -X POST "http://localhost:9091/api/test/scheduler/trigger"
     *
     * @return Message de confirmation
     */
    @PostMapping("/scheduler/trigger")
    public ResponseEntity<String> triggerScheduler() {
        log.info("Déclenchement manuel du scheduler");

        /*
         * Appeler directement la méthode du scheduler.
         *
         * C'est possible car le scheduler est un @Component Spring,
         * donc on peut l'injecter et appeler ses méthodes publiques.
         */
        scheduler.sendMissionEndReminders();

        return ResponseEntity.ok("Scheduler exécuté avec succès");
    }
}
