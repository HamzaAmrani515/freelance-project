package com.membership.freelancehamza.repository;

import com.membership.freelancehamza.entity.Evaluation;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.Repository;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface FreelancerDetailRepository extends Repository<Evaluation, Long> {

    @Query(value = """
        WITH scored_evaluations AS (
            SELECT
                e.*,
                GREATEST(
                    0,
                    EXTRACT(EPOCH FROM (now() - e.created_at)) / 86400.0
                ) AS age_days,

                CASE
                    WHEN GREATEST(0, EXTRACT(EPOCH FROM (now() - e.created_at)) / 86400.0) <= 7 THEN 5.0
                    WHEN GREATEST(0, EXTRACT(EPOCH FROM (now() - e.created_at)) / 86400.0) <= 30 THEN 4.0
                    WHEN GREATEST(0, EXTRACT(EPOCH FROM (now() - e.created_at)) / 86400.0) <= 90 THEN 3.0
                    WHEN GREATEST(0, EXTRACT(EPOCH FROM (now() - e.created_at)) / 86400.0) <= 180 THEN 2.0
                    ELSE 1.0
                END AS recency_score,

                (
                    0.30 * COALESCE(e.technical_quality, e.note)
                    + 0.18 * COALESCE(e.communication, e.note)
                    + 0.18 * COALESCE(e.deadline_respect, e.note)
                    + 0.14 * COALESCE(e.autonomy, e.note)
                    + 0.10 * COALESCE(e.test_quality, e.note)
                    + 0.10 * (
                        CASE
                            WHEN GREATEST(0, EXTRACT(EPOCH FROM (now() - e.created_at)) / 86400.0) <= 7 THEN 5.0
                            WHEN GREATEST(0, EXTRACT(EPOCH FROM (now() - e.created_at)) / 86400.0) <= 30 THEN 4.0
                            WHEN GREATEST(0, EXTRACT(EPOCH FROM (now() - e.created_at)) / 86400.0) <= 90 THEN 3.0
                            WHEN GREATEST(0, EXTRACT(EPOCH FROM (now() - e.created_at)) / 86400.0) <= 180 THEN 2.0
                            ELSE 1.0
                        END
                    )
                ) AS mission_score
            FROM evaluations e
        )
        SELECT
            e.mission_id AS missionId,
            m.title AS missionTitle,

            CASE
                WHEN e.mission_score <= 2.0
                  OR COALESCE(e.deadline_respect, e.note) <= 1
                THEN 'CANCELLED'

                WHEN COALESCE(e.deadline_respect, e.note) <= 2
                  OR e.mission_score < 3.0
                THEN 'LATE_DELIVERY'

                ELSE 'SUCCESS'
            END AS eventType,

            e.note AS note,
            e.technical_quality AS technicalQuality,
            e.communication AS communication,
            e.deadline_respect AS deadlineRespect,
            e.autonomy AS autonomy,
            e.test_quality AS testQuality,

            ROUND(e.mission_score::numeric, 2)::double precision AS missionScore,

            e.feedback AS feedback,
            e.created_at AS evaluatedAt

        FROM scored_evaluations e
        JOIN missions m ON m.id = e.mission_id
        WHERE e.freelancer_id = :freelancerId
        ORDER BY e.created_at DESC
        LIMIT 30
        """, nativeQuery = true)
    List<FreelancerMissionHistoryRow> findMissionHistory(
            @Param("freelancerId") Long freelancerId
    );
}