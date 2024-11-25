package com.example.freelance.repository;

import com.example.freelance.model.FreelanceModel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface FreelanceRepository extends JpaRepository<FreelanceModel, Long> {
}

