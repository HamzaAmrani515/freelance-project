package com.example.freelance.repository;

import com.example.freelance.model.Mission;
import com.example.freelance.model.enums.MissionStatut;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.Set;

/**
 * @author Assala Hamoudi
 */
@Repository
public interface MissionRepository extends JpaRepository<Mission, Long> {

    /**
     * @author Assala Hamoudi
     */
    @Query("""
            SELECT m.id
            FROM Mission m
            JOIN m.competences comp
            WHERE comp.id IN :targetCompetenceIds
              AND m.id <> :missionId
            GROUP BY m.id
            HAVING COUNT(DISTINCT comp.id) >= :similar
        """)
    List<Long> findSimilarMissionIds(@Param("targetCompetenceIds") Set<Long> compIds, @Param("missionId") Long missionId, @Param("similar") int similar);

    List<Mission> findTop50ByClientIdAndStatut(Long clientId, MissionStatut statut);

    @Query(value = """
            select m.id
                from missions m
                join mission_competences mc ON  m.id = mc.mission_id
                where m.statut = 'EN_ATTENTE' and mc.competence_id in(
                select x.competence_id from freelancer_competences x where x.freelancer_id = :freelanceId
                )
                 group by m.id
                having count(distinct mc.competence_id)>= :similar
        """,nativeQuery = true)
    List<Long> findSimilarMissionIdF(Long freelanceId, Integer similar);

    List<Mission> findByIdBetween(Long start, Long end);

    @EntityGraph(attributePaths = {"competences"})
    Optional<Mission> findWithCompetencesById(Long id);



}
