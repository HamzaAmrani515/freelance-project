package com.example.freelance.repository;

import com.example.freelance.model.Freelance;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface FreelanceRepository extends JpaRepository<Freelance, Long> {//intéragir avec la base de donnée
}

