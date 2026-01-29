package com.example.freelance.model;

import com.example.freelance.model.enums.NotificationType;
import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * Entité JPA représentant une notification en base de données.
 *
 * Cette classe est mappée à la table "notifications" dans PostgreSQL.
 * Chaque notification est associée à un freelancer et optionnellement à une mission.
 *
 * Table générée en base :
 * CREATE TABLE notifications (
 *     id BIGSERIAL PRIMARY KEY,
 *     freelancer_id BIGINT NOT NULL,
 *     mission_id BIGINT,
 *     type VARCHAR(50) NOT NULL,
 *     message TEXT NOT NULL,
 *     created_at TIMESTAMP NOT NULL,
 *     is_read BOOLEAN DEFAULT false,
 *     sent_via_websocket BOOLEAN DEFAULT false
 * );
 *
 * Annotations Lombok utilisées :
 * - @Data : Génère automatiquement getters, setters, equals, hashCode, toString
 * - @NoArgsConstructor : Génère un constructeur sans arguments (requis par JPA)
 */
@Entity
@Table(name = "notifications")  // Nom de la table en base de données
@Data
@NoArgsConstructor
public class Notification {

    /**
     * Identifiant unique de la notification.
     * Généré automatiquement par PostgreSQL (BIGSERIAL = auto-increment).
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * ID du freelancer qui reçoit la notification.
     * Obligatoire (nullable = false).
     * Utilisé pour envoyer la notification sur le bon canal WebSocket.
     */
    @Column(name = "freelancer_id", nullable = false)
    private Long freelancerId;

    /**
     * ID de la mission concernée par la notification.
     * Optionnel car certaines notifications peuvent ne pas être liées à une mission.
     */
    @Column(name = "mission_id")
    private Long missionId;

    /**
     * Type de notification (ex: RAPPEL_FIN_MISSION, MISSION_ASSIGNEE).
     *
     * @Enumerated(EnumType.STRING) : Stocke le nom de l'enum en texte
     * plutôt qu'un index numérique. Plus lisible en base de données.
     * Ex: "RAPPEL_FIN_MISSION" au lieu de "0"
     */
    @Enumerated(EnumType.STRING)
    @Column(name = "type", nullable = false)
    private NotificationType type;

    /**
     * Message textuel de la notification affiché à l'utilisateur.
     * Ex: "Rappel : Votre mission se termine dans 3 jours..."
     */
    @Column(name = "message", nullable = false)
    private String message;

    /**
     * Date et heure de création de la notification.
     * Rempli automatiquement par la méthode onCreate() grâce à @PrePersist.
     */
    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    /**
     * Indique si le freelancer a lu la notification.
     * Valeur par défaut : false (non lue).
     * Passe à true quand l'utilisateur clique sur la notification.
     */
    @Column(name = "is_read")
    private Boolean isRead = false;

    /**
     * Indique si la notification a été envoyée via WebSocket.
     * Valeur par défaut : false.
     * Passe à true après l'envoi réussi via WebSocket.
     * Utile pour le debugging et pour renvoyer les notifications échouées.
     */
    @Column(name = "sent_via_websocket")
    private Boolean sentViaWebsocket = false;

    /**
     * Méthode appelée automatiquement AVANT l'insertion en base de données.
     *
     * @PrePersist : Callback JPA exécuté avant le premier INSERT.
     * Permet de remplir automatiquement la date de création.
     */
    @PrePersist
    protected void onCreate() {
        this.createdAt = LocalDateTime.now();
    }
}
