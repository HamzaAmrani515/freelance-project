package com.example.freelance.repository;

import com.example.freelance.model.Notification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Repository JPA pour accéder aux notifications en base de données.
 *
 * Cette interface hérite de JpaRepository qui fournit automatiquement :
 * - save(entity) : Sauvegarder une notification
 * - findById(id) : Trouver par ID
 * - findAll() : Récupérer toutes les notifications
 * - delete(entity) : Supprimer une notification
 * - count() : Compter le nombre total
 *
 * Spring Data JPA génère automatiquement l'implémentation des méthodes
 * en analysant le nom de la méthode (convention de nommage).
 *
 * Exemple : findByFreelancerId → SELECT * FROM notifications WHERE freelancer_id = ?
 */
@Repository  // Indique à Spring que c'est un composant d'accès aux données
public interface NotificationRepository extends JpaRepository<Notification, Long> {

    /**
     * Récupère toutes les notifications d'un freelancer, triées par date décroissante.
     *
     * Requête SQL générée automatiquement :
     * SELECT * FROM notifications
     * WHERE freelancer_id = :freelancerId
     * ORDER BY created_at DESC
     *
     * @param freelancerId L'ID du freelancer
     * @return Liste des notifications (les plus récentes en premier)
     */
    List<Notification> findByFreelancerIdOrderByCreatedAtDesc(Long freelancerId);

    /**
     * Récupère uniquement les notifications NON LUES d'un freelancer.
     *
     * Requête SQL générée automatiquement :
     * SELECT * FROM notifications
     * WHERE freelancer_id = :freelancerId AND is_read = false
     * ORDER BY created_at DESC
     *
     * Décomposition du nom de la méthode :
     * - findBy : Début de la requête SELECT
     * - FreelancerId : WHERE freelancer_id = ?
     * - And : Opérateur AND
     * - IsReadFalse : AND is_read = false
     * - OrderByCreatedAtDesc : ORDER BY created_at DESC
     *
     * @param freelancerId L'ID du freelancer
     * @return Liste des notifications non lues
     */
    List<Notification> findByFreelancerIdAndIsReadFalseOrderByCreatedAtDesc(Long freelancerId);

    /**
     * Compte le nombre de notifications non lues pour un freelancer.
     *
     * Requête SQL générée automatiquement :
     * SELECT COUNT(*) FROM notifications
     * WHERE freelancer_id = :freelancerId AND is_read = false
     *
     * Utilisé pour afficher le badge de notifications (ex: "3" sur l'icône cloche).
     *
     * @param freelancerId L'ID du freelancer
     * @return Nombre de notifications non lues
     */
    long countByFreelancerIdAndIsReadFalse(Long freelancerId);
}
