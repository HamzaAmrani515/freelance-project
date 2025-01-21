package com.example.freelance.repository;
import com.example.freelance.model.MissionFreelance;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface MissionFreelanceRepository extends JpaRepository<MissionFreelance, Long> {
}
