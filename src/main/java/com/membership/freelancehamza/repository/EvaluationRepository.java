package com.membership.freelancehamza.repository;

import com.membership.freelancehamza.entity.Evaluation;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface EvaluationRepository extends JpaRepository<Evaluation, Long> {

    List<Evaluation> findByFreelancer_IdOrderByCreatedAtDesc(Long freelancerId);

    List<Evaluation> findTop4ByFreelancer_IdOrderByCreatedAtDescIdDesc(Long freelancerId);

    long countByFreelancer_Id(Long freelancerId);
}