package com.example.freelance.service;

import com.example.freelance.dto.NotificationDTO;
import com.example.freelance.model.Notification;
import com.example.freelance.model.enums.NotificationType;
import com.example.freelance.repository.NotificationRepository;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * Service contenant la logique métier pour les notifications.
 *
 * Ce service est responsable de :
 * 1. Créer et persister les notifications en base de données
 * 2. Envoyer les notifications en temps réel via WebSocket
 * 3. Gérer l'état des notifications (lecture, comptage, etc.)
 *
 * Architecture en couches :
 *
 *   Controller (API REST)
 *        │
 *        ▼
 *   Service (logique métier)  ← NOUS SOMMES ICI
 *        │
 *        ├──► Repository (accès BDD)
 *        │
 *        └──► SimpMessagingTemplate (envoi WebSocket)
 *
 *
 */
@Service
public class NotificationService {

    /**
     * Repository pour accéder aux notifications en base de données.
     */
    private final NotificationRepository notificationRepository;

    /**
     * Template pour envoyer des messages via WebSocket/STOMP.
     *
     * SimpMessagingTemplate est fourni par Spring WebSocket.
     * "Simp" = Simple Messaging Protocol (STOMP)
     *
     * Méthode principale utilisée :
     * - convertAndSend(destination, payload) : Envoie un objet Java converti en JSON
     */
    private final SimpMessagingTemplate messagingTemplate;

    /**
     * Constructeur avec injection de dépendances.
     *
     * Spring injecte automatiquement les instances de :
     * - NotificationRepository : Créé automatiquement car c'est une interface JpaRepository
     * - SimpMessagingTemplate : Créé par la configuration WebSocket (@EnableWebSocketMessageBroker)
     *
     * Pourquoi l'injection par constructeur ?
     * - Les dépendances sont obligatoires (pas null)
     * - Facilite les tests unitaires (on peut passer des mocks)
     * - Les champs peuvent être final (immutables)
     */
    public NotificationService(NotificationRepository notificationRepository,
                               SimpMessagingTemplate messagingTemplate) {
        this.notificationRepository = notificationRepository;
        this.messagingTemplate = messagingTemplate;
    }

    /**
     * Crée une notification, la sauvegarde en BDD et l'envoie via WebSocket.
     *
     * C'est la méthode principale appelée par le scheduler ou d'autres services.
     *
     * Flux :
     * 1. Créer l'objet Notification avec les paramètres
     * 2. Sauvegarder en base de données (INSERT)
     * 3. Envoyer via WebSocket au client connecté
     * 4. Mettre à jour le flag sentViaWebsocket = true
     *
     * @Transactional : Garantit que toutes les opérations BDD sont dans une transaction.
     *                  Si une erreur survient, tout est annulé (rollback).
     *
     * @param freelancerId ID du freelancer destinataire
     * @param missionId ID de la mission concernée
     * @param type Type de notification
     * @param message Texte du message
     * @return La notification créée
     */
    @Transactional
    public Notification createAndSendNotification(Long freelancerId, Long missionId,
                                                   NotificationType type, String message) {
        // Étape 1 : Créer l'objet Notification
        Notification notification = new Notification();
        notification.setFreelancerId(freelancerId);
        notification.setMissionId(missionId);
        notification.setType(type);
        notification.setMessage(message);
        // Note : createdAt est rempli automatiquement par @PrePersist

        // Étape 2 : Sauvegarder en base de données
        // save() retourne l'entité avec l'ID généré
        notification = notificationRepository.save(notification);

        // Étape 3 : Envoyer via WebSocket
        sendNotificationViaWebSocket(notification);

        return notification;
    }

    /**
     * Envoie une notification via WebSocket au freelancer concerné.
     *
     * Le message est envoyé sur le canal : /topic/notifications/{freelancerId}
     *
     * Seuls les clients abonnés à ce canal recevront le message.
     * Si le freelancer a plusieurs onglets ouverts, tous recevront la notification.
     * Si le freelancer n'est pas connecté, le message est perdu (mais reste en BDD).
     *
     * @param notification La notification à envoyer
     */
    private void sendNotificationViaWebSocket(Notification notification) {
        // Convertir l'entité en DTO (ne pas exposer tous les champs)
        NotificationDTO dto = NotificationDTO.fromEntity(notification);

        // Construire la destination : /topic/notifications/1 pour freelancer 1
        String destination = "/topic/notifications/" + notification.getFreelancerId();

        /*
         * Envoyer le message via WebSocket.
         *
         * convertAndSend() :
         * - "convert" : Convertit le DTO en JSON automatiquement (Jackson)
         * - "send" : Envoie le JSON à tous les clients abonnés à la destination
         *
         * Le client JavaScript recevra :
         * {
         *   "id": 1,
         *   "freelancerId": 1,
         *   "missionId": 42,
         *   "type": "RAPPEL_FIN_MISSION",
         *   "message": "...",
         *   "createdAt": "2026-01-16T09:00:00",
         *   "isRead": false
         * }
         */
        messagingTemplate.convertAndSend(destination, dto);

        // Mettre à jour le flag pour indiquer que l'envoi WebSocket a été fait
        notification.setSentViaWebsocket(true);
        notificationRepository.save(notification);
    }

    /**
     * Récupère toutes les notifications d'un freelancer.
     *
     * Utilisé par l'API REST : GET /api/notifications/freelancer/{id}
     *
     * @param freelancerId ID du freelancer
     * @return Liste des notifications converties en DTO
     */
    public List<NotificationDTO> getNotificationsByFreelancerId(Long freelancerId) {
        // Récupérer les entités depuis la BDD
        // Les convertir en DTO avec la méthode fromEntity
        // .toList() crée une liste immuable (Java 16+)
        return notificationRepository.findByFreelancerIdOrderByCreatedAtDesc(freelancerId)
                .stream()                           // Convertir la liste en Stream
                .map(NotificationDTO::fromEntity)   // Transformer chaque entité en DTO
                .toList();                          // Collecter en liste
    }

    /**
     * Récupère uniquement les notifications non lues d'un freelancer.
     *
     * Utilisé par l'API REST : GET /api/notifications/freelancer/{id}/unread
     *
     * @param freelancerId ID du freelancer
     * @return Liste des notifications non lues
     */
    public List<NotificationDTO> getUnreadNotificationsByFreelancerId(Long freelancerId) {
        return notificationRepository.findByFreelancerIdAndIsReadFalseOrderByCreatedAtDesc(freelancerId)
                .stream()
                .map(NotificationDTO::fromEntity)
                .toList();
    }

    /**
     * Compte le nombre de notifications non lues.
     *
     * Utilisé pour afficher le badge de notifications (ex: "3" sur l'icône cloche).
     * API REST : GET /api/notifications/freelancer/{id}/count
     *
     * @param freelancerId ID du freelancer
     * @return Nombre de notifications non lues
     */
    public long countUnreadNotifications(Long freelancerId) {
        return notificationRepository.countByFreelancerIdAndIsReadFalse(freelancerId);
    }

    /**
     * Marque une notification comme lue.
     *
     * Appelé quand l'utilisateur clique sur une notification.
     * API REST : PUT /api/notifications/{id}/read
     *
     * @Transactional : Nécessaire car on modifie une entité
     *
     * @param notificationId ID de la notification
     * @return La notification mise à jour
     * @throws RuntimeException Si la notification n'existe pas
     */
    @Transactional
    public NotificationDTO markAsRead(Long notificationId) {
        // Chercher la notification en BDD
        // orElseThrow() : Lance une exception si non trouvée
        Notification notification = notificationRepository.findById(notificationId)
                .orElseThrow(() -> new RuntimeException("Notification not found: " + notificationId));

        // Mettre à jour le flag isRead
        notification.setIsRead(true);

        // Sauvegarder les modifications (UPDATE en BDD)
        notification = notificationRepository.save(notification);

        // Retourner le DTO
        return NotificationDTO.fromEntity(notification);
    }

    /**
     * Marque toutes les notifications d'un freelancer comme lues.
     *
     * Appelé quand l'utilisateur clique sur "Tout marquer comme lu".
     * API REST : PUT /api/notifications/freelancer/{id}/read-all
     *
     * @Transactional : Nécessaire car on modifie plusieurs entités
     *
     * @param freelancerId ID du freelancer
     */
    @Transactional
    public void markAllAsRead(Long freelancerId) {
        // Récupérer toutes les notifications non lues
        List<Notification> unreadNotifications = notificationRepository
                .findByFreelancerIdAndIsReadFalseOrderByCreatedAtDesc(freelancerId);

        // Marquer chacune comme lue
        // forEach avec lambda : pour chaque notification, appeler setIsRead(true)
        unreadNotifications.forEach(notification -> notification.setIsRead(true));

        // Sauvegarder toutes les modifications en une seule opération (batch)
        notificationRepository.saveAll(unreadNotifications);
    }
}
