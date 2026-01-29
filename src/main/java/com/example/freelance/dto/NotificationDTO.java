package com.example.freelance.dto;

import com.example.freelance.model.Notification;
import com.example.freelance.model.enums.NotificationType;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * Data Transfer Object (DTO) pour les notifications.
 *
 * Un DTO est un objet utilisé pour transférer des données entre les couches
 * de l'application (Controller ↔ Service ↔ Client).
 *
 * Pourquoi utiliser un DTO au lieu de l'entité directement ?
 * 1. Sécurité : On ne veut pas exposer tous les champs de l'entité (ex: sentViaWebsocket)
 * 2. Flexibilité : Le DTO peut avoir une structure différente de l'entité
 * 3. Performance : On peut choisir quels champs envoyer au client
 * 4. Découplage : L'API reste stable même si l'entité change
 *
 * Ce DTO est envoyé :
 * - Via l'API REST (réponses JSON)
 * - Via WebSocket (notifications temps réel)
 *
 * Exemple de JSON généré :
 * {
 *   "id": 1,
 *   "freelancerId": 1,
 *   "missionId": 42,
 *   "type": "RAPPEL_FIN_MISSION",
 *   "message": "Rappel : Votre mission se termine dans 3 jours...",
 *   "createdAt": "2026-01-16T09:00:00",
 *   "isRead": false
 * }
 */
@Data               // Génère getters, setters, equals, hashCode, toString
@AllArgsConstructor // Génère un constructeur avec tous les arguments
@NoArgsConstructor  // Génère un constructeur vide (nécessaire pour la désérialisation JSON)
public class NotificationDTO {

    /** Identifiant unique de la notification */
    private Long id;

    /** ID du freelancer destinataire */
    private Long freelancerId;

    /** ID de la mission concernée */
    private Long missionId;

    /** Type de notification (RAPPEL_FIN_MISSION, MISSION_ASSIGNEE, etc.) */
    private NotificationType type;

    /** Message textuel affiché à l'utilisateur */
    private String message;

    /** Date et heure de création */
    private LocalDateTime createdAt;

    /** Indique si la notification a été lue */
    private Boolean isRead;

    /**
     * Méthode statique "factory" pour convertir une entité Notification en DTO.
     *
     * Pattern Factory : Permet de créer un DTO à partir d'une entité
     * sans exposer la logique de conversion partout dans le code.
     *
     * Utilisation :
     *   Notification entity = notificationRepository.findById(1);
     *   NotificationDTO dto = NotificationDTO.fromEntity(entity);
     *
     * Note : Le champ 'sentViaWebsocket' de l'entité n'est PAS inclus dans le DTO
     * car c'est une information interne qui ne concerne pas le client.
     *
     * @param notification L'entité JPA à convertir
     * @return Le DTO correspondant
     */
    public static NotificationDTO fromEntity(Notification notification) {
        return new NotificationDTO(
                notification.getId(),
                notification.getFreelancerId(),
                notification.getMissionId(),
                notification.getType(),
                notification.getMessage(),
                notification.getCreatedAt(),
                notification.getIsRead()
        );
    }
}
