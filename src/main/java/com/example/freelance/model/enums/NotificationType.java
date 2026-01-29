package com.example.freelance.model.enums;

/**
 * Énumération des types de notifications possibles dans l'application.
 *
 * Cette enum permet de catégoriser les notifications et de les filtrer
 * par type si nécessaire (ex: afficher seulement les rappels de fin de mission).
 *
 * Utilisation dans l'entité Notification :
 *   @Enumerated(EnumType.STRING)
 *   private NotificationType type;
 *
 * En base de données, le type sera stocké en tant que texte (STRING)
 * plutôt qu'en tant que nombre (ORDINAL), ce qui est plus lisible.
 */
public enum NotificationType {

    /**
     * Notification de rappel envoyée 3 jours avant la fin d'une mission.
     * Rappelle au freelancer de rédiger son rapport de fin de mission.
     */
    RAPPEL_FIN_MISSION,

    /**
     * Notification envoyée quand une nouvelle mission est assignée au freelancer.
     * (Extensible pour de futures fonctionnalités)
     */
    MISSION_ASSIGNEE
}
