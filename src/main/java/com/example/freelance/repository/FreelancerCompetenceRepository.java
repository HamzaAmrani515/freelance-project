package com.example.freelance.repository;

import com.example.freelance.model.FreelancerCompetence;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface FreelancerCompetenceRepository extends JpaRepository<FreelancerCompetence, Long> {
    @Query("SELECT DISTINCT fc.freelancer.id FROM FreelancerCompetence fc WHERE fc.competence.id IN :competenceIds")
    List<Long> findFreelancersByCompetences(@Param("competenceIds") List<Long> competenceIds);
}
