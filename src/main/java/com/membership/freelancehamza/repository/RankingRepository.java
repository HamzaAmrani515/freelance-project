package com.membership.freelancehamza.repository;

import com.membership.freelancehamza.entity.Freelancer;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.Repository;
import org.springframework.data.repository.query.Param;

import java.util.List;

/**
 * Repository responsable du calcul du ranking global des freelancers.
 *
 * Le score final n'est pas stocké directement dans la table freelancers.
 * Il est recalculé dynamiquement à partir :
 * - des évaluations client,
 * - des événements métier,
 * - de la tendance,
 * - de l'expérience,
 * - de la disponibilité.
 */
public interface RankingRepository extends Repository<Freelancer, Long> {

    @Query(value = """
        WITH evaluation_base AS (
            SELECT
                e.freelancer_id,
                e.created_at,

                -- Age de l'évaluation en jours.
                -- Exemple : une évaluation faite aujourd'hui = 0 jour.
                -- GREATEST(0, ...) évite les valeurs négatives si une date future existe.
                GREATEST(
                    0,
                    EXTRACT(EPOCH FROM (now() - e.created_at)) / 86400.0
                ) AS age_days,

                -- Poids de récence.
                -- Plus l'évaluation est récente, plus son poids est fort.
                -- Plus elle est ancienne, plus son poids diminue.
                EXP(
                    -0.03 * GREATEST(
                        0,
                        EXTRACT(EPOCH FROM (now() - e.created_at)) / 86400.0
                    )
                ) AS recency_weight,

                -- Score d'une évaluation sur 5.
                -- On combine plusieurs critères métier :
                -- 30% qualité technique
                -- 18% communication
                -- 18% respect des délais
                -- 14% autonomie
                -- 10% qualité des tests
                -- 10% récence
                --
                -- COALESCE permet d'utiliser la note globale si un critère est null.
                (
                    0.30 * COALESCE(e.technical_quality, e.note)
                    + 0.18 * COALESCE(e.communication, e.note)
                    + 0.18 * COALESCE(e.deadline_respect, e.note)
                    + 0.14 * COALESCE(e.autonomy, e.note)
                    + 0.10 * COALESCE(e.test_quality, e.note)
                    + 0.10 * (
                        CASE
                            -- Evaluation très récente : bonus fort.
                            WHEN GREATEST(0, EXTRACT(EPOCH FROM (now() - e.created_at)) / 86400.0) <= 7 THEN 5.0

                            -- Evaluation récente : bonus correct.
                            WHEN GREATEST(0, EXTRACT(EPOCH FROM (now() - e.created_at)) / 86400.0) <= 30 THEN 4.0

                            -- Evaluation moins récente.
                            WHEN GREATEST(0, EXTRACT(EPOCH FROM (now() - e.created_at)) / 86400.0) <= 90 THEN 3.0

                            -- Evaluation ancienne.
                            WHEN GREATEST(0, EXTRACT(EPOCH FROM (now() - e.created_at)) / 86400.0) <= 180 THEN 2.0

                            -- Evaluation très ancienne.
                            ELSE 1.0
                        END
                    )
                ) AS evaluation_score
            FROM evaluations e
        ),

        recent_quality AS (
            SELECT
                freelancer_id,

                -- Qualité récente du freelancer sur 100.
                --
                -- On fait une moyenne pondérée :
                -- somme(score_evaluation * poids_recence) / somme(poids_recence)
                --
                -- Le *20 sert à convertir un score sur 5 en score sur 100.
                CASE
                    WHEN SUM(recency_weight) = 0 THEN 0
                    ELSE (
                        SUM(evaluation_score * recency_weight)
                        / SUM(recency_weight)
                    ) * 20
                END AS recent_evaluation_score,

                -- Nombre total d'évaluations du freelancer.
                COUNT(*) AS evaluations_count
            FROM evaluation_base
            GROUP BY freelancer_id
        ),

        event_scores AS (
            SELECT
                me.freelancer_id,

                -- Score brut de comportement basé sur les événements métier.
                -- SUCCESS augmente la fiabilité.
                -- LATE_DELIVERY pénalise.
                -- CANCELLED pénalise fortement.
                SUM(
                    CASE me.type
                        WHEN 'SUCCESS' THEN 5
                        WHEN 'LATE_DELIVERY' THEN -10
                        WHEN 'CANCELLED' THEN -20
                        ELSE 0
                    END
                ) AS raw_behavior_score,

                -- Nombre de retards sur les 30 derniers jours.
                -- Sert à appliquer une pénalité supplémentaire si le freelancer
                -- accumule beaucoup de retards récents.
                SUM(
                    CASE
                        WHEN me.type = 'LATE_DELIVERY'
                         AND me.created_at >= now() - interval '30 days'
                        THEN 1 ELSE 0
                    END
                ) AS late_30d
            FROM mission_events me
            GROUP BY me.freelancer_id
        ),

        reliability AS (
            SELECT
                freelancer_id,

                -- Score de fiabilité sur 100.
                --
                -- Base : 70 points.
                -- On ajoute ou retire les points liés aux événements métier.
                -- Si le freelancer a au moins 3 retards récents, on applique -15.
                --
                -- GREATEST / LEAST bloquent le score entre 0 et 100.
                GREATEST(
                    0,
                    LEAST(
                        100,
                        70
                        + COALESCE(raw_behavior_score, 0)
                        + CASE
                            WHEN COALESCE(late_30d, 0) >= 3 THEN -15
                            ELSE 0
                          END
                    )
                ) AS reliability_score
            FROM event_scores
        ),

        eval_ranked AS (
            SELECT
                freelancer_id,
                evaluation_score,

                -- Classement des évaluations par freelancer.
                -- rn = 1 : évaluation la plus récente.
                -- rn = 2 : deuxième plus récente.
                -- etc.
                ROW_NUMBER() OVER (
                    PARTITION BY freelancer_id
                    ORDER BY created_at DESC
                ) AS rn
            FROM evaluation_base
        ),

        trend AS (
            SELECT
                freelancer_id,

                -- Moyenne des 3 évaluations les plus récentes.
                AVG(CASE WHEN rn BETWEEN 1 AND 3 THEN evaluation_score END) AS recent_avg,

                -- Moyenne des 3 évaluations précédentes.
                AVG(CASE WHEN rn BETWEEN 4 AND 6 THEN evaluation_score END) AS previous_avg
            FROM eval_ranked
            GROUP BY freelancer_id
        ),

        trend_score AS (
            SELECT
                freelancer_id,

                -- Score de tendance sur 100.
                --
                -- Si on n'a pas assez d'historique, la tendance est neutre : 50.
                -- Sinon, on compare les 3 dernières évaluations avec les 3 précédentes.
                CASE
                    WHEN previous_avg IS NULL THEN 50
                    ELSE GREATEST(0, LEAST(100, 50 + ((recent_avg - previous_avg) * 15)))
                END AS trend_score,

                -- Label de tendance utilisé dans le front.
                CASE
                    WHEN previous_avg IS NULL THEN 'STABLE'
                    WHEN recent_avg - previous_avg > 0.3 THEN 'IMPROVING'
                    WHEN recent_avg - previous_avg < -0.3 THEN 'DECLINING'
                    ELSE 'STABLE'
                END AS trend_label
            FROM trend
        ),

        mission_counts AS (
            SELECT
                freelancer_id,

                -- Nombre de missions associées au freelancer.
                COUNT(*) AS missions_count
            FROM mission_freelance
            GROUP BY freelancer_id
        ),

        experience AS (
            SELECT
                f.id AS freelancer_id,

                -- Score d'expérience sur 100.
                --
                -- Il dépend :
                -- - du nombre d'années d'expérience,
                -- - du nombre de missions.
                --
                -- Formule :
                -- années_experience * 10 + nombre_missions * 3
                GREATEST(
                    0,
                    LEAST(
                        100,
                        COALESCE(f.years_experience, 0) * 10
                        + COALESCE(mc.missions_count, 0) * 3
                    )
                ) AS experience_score
            FROM freelancers f
            LEFT JOIN mission_counts mc ON mc.freelancer_id = f.id
        ),

        availability AS (
            SELECT
                id AS freelancer_id,

                -- Transformation du statut de disponibilité en score numérique.
                CASE availability_status
                    WHEN 'AVAILABLE' THEN 100
                    WHEN 'PARTIALLY_AVAILABLE' THEN 60
                    WHEN 'BUSY' THEN 30
                    WHEN 'UNAVAILABLE' THEN 0
                    ELSE 50
                END AS availability_score
            FROM freelancers
        ),

        final_ranking AS (
            SELECT
                -- Informations principales du freelancer.
                f.id AS freelancerId,
                CONCAT(f.prenom, ' ', f.nom) AS fullName,
                f.skill AS skill,
                f.seniority AS seniority,
                f.availability_status AS availabilityStatus,
                f.company_name AS companyName,
                f.years_experience AS yearsExperience,

                -- Score final global sur 100.
                --
                -- C'est le score utilisé pour classer les freelancers.
                --
                -- Formule :
                -- 45% qualité récente
                -- 25% fiabilité
                -- 15% tendance
                -- 10% expérience
                -- 5% disponibilité
                ROUND((
                    0.45 * COALESCE(rq.recent_evaluation_score, 0)
                    + 0.25 * COALESCE(rel.reliability_score, 50)
                    + 0.15 * COALESCE(ts.trend_score, 50)
                    + 0.10 * COALESCE(exp.experience_score, 0)
                    + 0.05 * COALESCE(av.availability_score, 50)
                )::numeric, 2)::double precision AS finalScore,

                -- Détail des sous-scores retournés au front.
                ROUND(COALESCE(rq.recent_evaluation_score, 0)::numeric, 2)::double precision AS recentEvaluationScore,
                ROUND(COALESCE(rel.reliability_score, 50)::numeric, 2)::double precision AS reliabilityScore,
                ROUND(COALESCE(ts.trend_score, 50)::numeric, 2)::double precision AS trendScore,
                ROUND(COALESCE(exp.experience_score, 0)::numeric, 2)::double precision AS experienceScore,
                ROUND(COALESCE(av.availability_score, 50)::numeric, 2)::double precision AS availabilityScore,

                -- Label de tendance et compteurs.
                COALESCE(ts.trend_label, 'STABLE') AS trendLabel,
                COALESCE(mc.missions_count, 0) AS missionsCount,
                COALESCE(rq.evaluations_count, 0) AS evaluationsCount

            FROM freelancers f

            -- LEFT JOIN pour garder tous les freelancers,
            -- même ceux qui n'ont pas encore d'évaluation ou de mission.
            LEFT JOIN recent_quality rq ON rq.freelancer_id = f.id
            LEFT JOIN reliability rel ON rel.freelancer_id = f.id
            LEFT JOIN trend_score ts ON ts.freelancer_id = f.id
            LEFT JOIN experience exp ON exp.freelancer_id = f.id
            LEFT JOIN availability av ON av.freelancer_id = f.id
            LEFT JOIN mission_counts mc ON mc.freelancer_id = f.id
        )

        -- Résultat final du ranking.
        SELECT *
        FROM final_ranking

        -- On affiche les meilleurs freelancers en premier.
        ORDER BY finalScore DESC

        -- Pagination : limite le nombre de lignes retournées.
        LIMIT :limit OFFSET :offset
        """, nativeQuery = true)
    List<FreelancerScoreRow> findRanking(
            @Param("limit") int limit,
            @Param("offset") int offset
    );
}