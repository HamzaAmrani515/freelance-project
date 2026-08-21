package com.membership.freelancehamza.repository;

import com.membership.freelancehamza.entity.Freelancer;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface FreelancerRepository extends JpaRepository<Freelancer, Long> {

    List<Freelancer> findAllByOrderByFinalScoreDesc(Pageable pageable);

    List<Freelancer> findBySkillContainingIgnoreCaseOrderByFinalScoreDesc(
            String skill,
            Pageable pageable
    );
}