package com.example.freelance.repository;

import com.example.freelance.model.MissionFreelance;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

/**
 * Repository JPA pour accéder aux associations mission-freelancer en base de données.
 *
 * Cette interface hérite de JpaRepository qui fournit automatiquement les méthodes CRUD :
 * - save(entity) : Insérer ou mettre à jour
 * - findById(id) : Trouver par ID
 * - findAll() : Récupérer tout
 * - delete(entity) : Supprimer
 *
 * JpaRepository<MissionFreelance, Long> :
 * - MissionFreelance : Type de l'entité gérée
 * - Long : Type de la clé primaire (id)
 *
 * @Repository : Indique à Spring que c'est un composant d'accès aux données.
 *               Permet aussi la traduction automatique des exceptions SQL en exceptions Spring.
 */
@Repository
public interface MissionFreelanceRepository extends JpaRepository<MissionFreelance, Long> {

    /**
     * Recherche les missions qui se terminent à une date donnée et non encore notifiées.
     *
     * Cette requête est utilisée par le scheduler pour trouver les missions
     * qui nécessitent un rappel de fin de mission.
     *
     * @Query : Permet d'écrire une requête JPQL personnalisée.
     *
     * JPQL (Java Persistence Query Language) :
     * - Similaire à SQL mais travaille avec les entités Java
     * - "MissionFreelance mf" au lieu de "mission_freelance"
     * - "mf.dateFin" au lieu de "date_fin"
     *
     * Requête équivalente en SQL :
     * SELECT * FROM mission_freelance
     * WHERE date_fin = :targetDate AND notification_envoyee = false
     *
     * :targetDate : Paramètre nommé, lié par @Param("targetDate")
     *
     * Conditions :
     * 1. dateFin = targetDate : La mission se termine à la date cible
     * 2. notificationEnvoyee = false : Pas encore de notification envoyée
     *
     * La condition sur notificationEnvoyee évite d'envoyer plusieurs fois
     * la même notification si le scheduler s'exécute plusieurs fois.
     *
     * @param targetDate La date de fin recherchée (généralement aujourd'hui + 3 jours)
     * @return Liste des missions correspondantes
     */
    @Query("SELECT mf FROM MissionFreelance mf WHERE mf.dateFin = :targetDate AND mf.notificationEnvoyee = false")
    List<MissionFreelance> findMissionsEndingOn(@Param("targetDate") LocalDate targetDate);
}
