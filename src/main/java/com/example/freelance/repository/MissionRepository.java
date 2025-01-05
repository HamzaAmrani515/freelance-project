package com.example.freelance.repository;

import com.example.freelance.model.Mission;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Set;

@Repository
public interface MissionRepository extends JpaRepository<Mission, Long> {

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
}
