package com.example.freelance.repository;

import com.example.freelance.model.Client;
import com.example.freelance.model.Freelancer;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import com.example.freelance.model.enums.FreelancerStatus;
import java.util.List;
import java.util.Collection;
import java.util.List;


@Repository
public interface FreelancerRepository extends JpaRepository<Freelancer, Long> {
    //pour me rajouter
    @EntityGraph(attributePaths = {"competences"})
    @Query("SELECT f FROM Freelancer f WHERE f.id IN :freelancerIds")
    List<Freelancer> findAllWithCompetencesByIdIn(@Param("freelancerIds") Collection<Long> ids);



    Freelancer findByEmail(String email);
    @EntityGraph(attributePaths = {"competences"})
    List<Freelancer> findAllByStatus(FreelancerStatus status);
    @EntityGraph(attributePaths = {"competences"})
    @Query("""
        select distinct f
        from Freelancer f
        join f.competences c
        where f.status = com.example.freelance.model.enums.FreelancerStatus.AVAILABLE
          and lower(c.nom) = lower(:competenceName)
    """)
    List<Freelancer> findAvailableByCompetenceName(String competenceName);
}
