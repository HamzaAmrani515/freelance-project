package com.example.freelance.controller;

import com.example.freelance.dto.NotificationDTO;
import com.example.freelance.service.NotificationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

/**
 * Contrôleur REST pour gérer les notifications.
 *
 * Ce contrôleur expose une API REST permettant de :
 * - Récupérer les notifications d'un freelancer
 * - Compter les notifications non lues
 * - Marquer les notifications comme lues
 *
 * URL de base : /api/notifications
 *
 * Annotations utilisées :
 * - @Slf4j : Génère automatiquement un logger (Lombok)
 *            Équivalent à : private static final Logger log = LoggerFactory.getLogger(...)
 *
 * - @RestController : Combinaison de @Controller + @ResponseBody
 *                     Indique que toutes les méthodes retournent du JSON (pas de vues HTML)
 *
 * - @RequiredArgsConstructor : Génère un constructeur avec tous les champs 'final' (Lombok)
 *                              Équivalent à : public NotificationController(NotificationService s) { ... }
 *
 * - @RequestMapping : Définit le préfixe URL pour toutes les méthodes du contrôleur
 */
@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/notifications")
public class NotificationController {

    /**
     * Service de notification injecté par Spring.
     * 'final' + @RequiredArgsConstructor = injection par constructeur obligatoire.
     */
    private final NotificationService notificationService;

    /**
     * Récupère toutes les notifications d'un freelancer.
     *
     * Endpoint : GET /api/notifications/freelancer/{id}
     *
     * @PathVariable : Extrait la valeur {id} de l'URL
     *                 Ex: GET /api/notifications/freelancer/1 → id = 1
     *
     * ResponseEntity : Wrapper Spring qui permet de contrôler :
     * - Le code HTTP de la réponse (200, 404, 500, etc.)
     * - Les headers de la réponse
     * - Le corps de la réponse (body)
     *
     * ResponseEntity.ok() = HTTP 200 OK
     *
     * Exemple de réponse JSON :
     * [
     *   {
     *     "id": 1,
     *     "freelancerId": 1,
     *     "missionId": 42,
     *     "type": "RAPPEL_FIN_MISSION",
     *     "message": "Rappel : Votre mission...",
     *     "createdAt": "2026-01-16T09:00:00",
     *     "isRead": false
     *   }
     * ]
     *
     * @param id ID du freelancer
     * @return Liste des notifications
     */
    @GetMapping("/freelancer/{id}")
    public ResponseEntity<List<NotificationDTO>> getNotificationsByFreelancerId(@PathVariable Long id) {
        log.info("Récupérer toutes les notifications pour le freelancer {}", id);
        List<NotificationDTO> notifications = notificationService.getNotificationsByFreelancerId(id);
        return ResponseEntity.ok(notifications);
    }

    /**
     * Récupère uniquement les notifications NON LUES d'un freelancer.
     *
     * Endpoint : GET /api/notifications/freelancer/{id}/unread
     *
     * Utile pour afficher la liste des nouvelles notifications
     * dans une pop-up ou un panneau de notifications.
     *
     * @param id ID du freelancer
     * @return Liste des notifications non lues
     */
    @GetMapping("/freelancer/{id}/unread")
    public ResponseEntity<List<NotificationDTO>> getUnreadNotificationsByFreelancerId(@PathVariable Long id) {
        log.info("Récupérer les notifications non lues pour le freelancer {}", id);
        List<NotificationDTO> notifications = notificationService.getUnreadNotificationsByFreelancerId(id);
        return ResponseEntity.ok(notifications);
    }

    /**
     * Compte le nombre de notifications non lues pour un freelancer.
     *
     * Endpoint : GET /api/notifications/freelancer/{id}/count
     *
     * Utile pour afficher un badge sur l'icône de notification.
     * Ex: L'icône cloche avec un "3" indiquant 3 nouvelles notifications.
     *
     * Exemple de réponse JSON :
     * {
     *   "unreadCount": 3
     * }
     *
     * Map.of() : Crée une Map immuable (Java 9+)
     *
     * @param id ID du freelancer
     * @return Compteur sous forme de JSON
     */
    @GetMapping("/freelancer/{id}/count")
    public ResponseEntity<Map<String, Long>> countUnreadNotifications(@PathVariable Long id) {
        log.info("Compter les notifications non lues pour le freelancer {}", id);
        long count = notificationService.countUnreadNotifications(id);
        return ResponseEntity.ok(Map.of("unreadCount", count));
    }

    /**
     * Marque une notification spécifique comme lue.
     *
     * Endpoint : PUT /api/notifications/{id}/read
     *
     * @PutMapping : Méthode HTTP PUT (modification d'une ressource existante)
     *
     * Appelé quand l'utilisateur clique sur une notification
     * ou ouvre le détail d'une notification.
     *
     * @param id ID de la notification (pas du freelancer !)
     * @return La notification mise à jour
     */
    @PutMapping("/{id}/read")
    public ResponseEntity<NotificationDTO> markAsRead(@PathVariable Long id) {
        log.info("Marquer la notification {} comme lue", id);
        NotificationDTO notification = notificationService.markAsRead(id);
        return ResponseEntity.ok(notification);
    }

    /**
     * Marque TOUTES les notifications d'un freelancer comme lues.
     *
     * Endpoint : PUT /api/notifications/freelancer/{id}/read-all
     *
     * Appelé quand l'utilisateur clique sur "Tout marquer comme lu"
     * dans le panneau de notifications.
     *
     * ResponseEntity.ok().build() : Retourne HTTP 200 OK sans corps de réponse
     *
     * @param id ID du freelancer
     * @return HTTP 200 OK (sans contenu)
     */
    @PutMapping("/freelancer/{id}/read-all")
    public ResponseEntity<Void> markAllAsRead(@PathVariable Long id) {
        log.info("Marquer toutes les notifications du freelancer {} comme lues", id);
        notificationService.markAllAsRead(id);
        return ResponseEntity.ok().build();
    }
}
